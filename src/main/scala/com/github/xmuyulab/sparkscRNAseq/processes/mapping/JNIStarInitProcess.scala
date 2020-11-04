/*
 * @author: 6liuyu123
 * @date: 2020/10/27
 */
package com.github.xmuyulab.sparkscRNAseq.processes.mapping

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInitAdapter
import com.github.xmuyulab.sparkscRNAseq.algorithms.tools.StringToSamTool
import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.fileio.NormalFileLoader
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.hadoop.io.Text

import scala.collection.immutable.TreeMap
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import scala.util.control.Breaks._

object JNIStarInitProcess {

    def runStar(sc: SparkContext, extractedFastq: RDD[FastqRecord], argsUtils: ArgsUtils): RDD[String] = {
        val starLibPath = BinTools.starLibPath
        val starLibPathBD =  sc.broadcast(starLibPath).value
        System.out.println("############### Here is JNIStarInitProcess. ###############\n")
        var samRdd = extractedFastq.repartition(4).mapPartitions(
                it => {
                    StarInitAdapter.pairAlign(starLibPathBD, it.toSeq, argsUtils).iterator
                }).filter(_.contains("NH:i:1")).filter (
                    line =>
                        line.split('\t')(13).substring(5).toInt > 35
                )
        System.out.println("############### Here is JNIStarInitProcess. ###############\n")
        return samRdd
    }
    
}
