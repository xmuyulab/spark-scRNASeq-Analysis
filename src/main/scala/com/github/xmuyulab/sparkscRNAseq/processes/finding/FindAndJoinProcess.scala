/*
 * @author: 6liuyu123
 * @date: 2020/10/27
 */
package com.github.xmuyulab.sparkscRNAseq.processes.finding;

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.fileio.NormalFileLoader
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext

object FindAndJoinProcess {
    def findAndJoin(sc: SparkContext, argsUtils: ArgsUtils): RDD[FastqRecord] = {
        val R1RDD = NormalFileLoader.loadFastqR1ToRdd(sc, argsUtils.getR1Path()).cache()
        val whitelist = sc.parallelize(R1RDD.map(line => (line._1, 1))
                          .reduceByKey((a, b) => a + b)
                          .sortBy(_._2, false)
                          .take(argsUtils.getCellNumber()))
        // @Test whitelist.saveAsTextFile("file:///mnt/spark/result")
        val ZR1 = R1RDD.zipWithIndex()
        R1RDD.unpersist()
        val ER1RDD = ZR1.map(line => (line._1._1,(line._1._2, line._2))).join(whitelist)
                        .map(line => (line._2._1._2, line._1.toString + "_" + line._2._1._1.toString))
        // @Test ER1RDD.saveAsTextFile("file:///mnt/spark/result")
        val R2RDD = NormalFileLoader.loadFastqR2ToRdd(sc, argsUtils.getR2Path()).zipWithIndex().map(line => (line._2, line._1))
        // @Test R2RDD.saveAsTextFile("file:///mnt/spark/result1")
        val res = R2RDD.join(ER1RDD).map(line => {FastqRecord("@" + line._1.toString + "_" + line._2._2, line._2._1._2.toString)})
        res
    }
}