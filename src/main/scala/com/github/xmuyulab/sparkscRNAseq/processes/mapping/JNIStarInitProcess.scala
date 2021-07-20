/*
 * @author: 6liuyu123
 * @date: 2020/10/27
 */
package com.github.xmuyulab.sparkscRNAseq.processes.mapping

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInitAdapter
import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import scala.collection.JavaConversions._

object JNIStarInitProcess {
    def runStar(sc: SparkContext, extractedFastq: RDD[FastqRecord], argsUtils: ArgsUtils): RDD[String] = {
        val starLibPath = BinTools.starLibPath
        val starLibPathBD =  sc.broadcast(starLibPath).value
        val samRdd = extractedFastq.repartition(4).mapPartitions(it => {
                    StarInitAdapter.pairAlign(starLibPathBD, it.toSeq, argsUtils).iterator
                }).filter(_.contains("NH:i:1")).filter (line =>
                    line.split('\t')(13).substring(5).toInt > 35
                )
        samRdd
    }
}
