package com.github.xmuyulab.sparkscRNAseq.processes.finding;

import org.apache.hadoop.io.Text
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import com.github.xmuyulab.sparkscRNAseq.fileio.NormalFileLoader
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord

//import com.github.xmuyulab.sparkscRNAseq.engine.AbstractProcess
//import com.github.xmuyulab.sparkscRNAseq.exceptions.{ResourceNotSetException, ResourceSetException}
//import com.github.xmuyulab.sparkscRNAseq.resource.Resource

/**
 * Author: 6liuyu123
 */

object FindAndJoinProcess {
    def findAndJoin(sc: SparkContext, argsUtils: ArgsUtils): RDD[FastqRecord] = {
        val R1RDD = NormalFileLoader.loadFastqR1ToRdd(sc, argsUtils.getR1Path)
        val whitelist = sc.parallelize(R1RDD.map(line => (line._1, 1))
                            .reduceByKey((a, b) => a + b)
                            .sortBy(_._2, false)
                            .take(argsUtils.getCellNumber()))
        val ER1RDD = whitelist.join(R1RDD)
                                .map(line => (new Text(line._2._2.toString().substring(11)), new Text(line._1.toString() + "_" + line._2._2.toString().substring(0, 10))))
        val R2RDD = NormalFileLoader.loadFastqR2ToRdd(sc, argsUtils.getR2Path)
        val res = R2RDD.join(ER1RDD)
                        .map(
                            line => {
                                FastqRecord(line._1.toString() + "_" + line._2._2.toString(), line._2._1.toString())
                            }
                        )
        res
    }
}