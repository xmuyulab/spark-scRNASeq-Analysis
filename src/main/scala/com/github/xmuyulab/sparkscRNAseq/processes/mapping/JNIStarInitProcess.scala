package com.github.xmuyulab.sparkscRNAseq.processes.mapping

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInitAdapter
import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.collection.JavaConversions._
import org.apache.hadoop.io.Text

object JNIStarInitProcess {
  def runStar(sc: SparkContext,
              extractedFastq: RDD[FastqRecord]): Void = {

    val starLibPath = BinTools.starLibPath
    val starLibPathBD =  sc.broadcast(starLibPath).value
    //  val referencePathBD = sc.broadcast(referencePath).value
    System.out.println("############### Here is JNIStarInitProcess. ###############\n")
    extractedFastq.repartition(200).mapPartitions(
      it => {
        StarInitAdapter.pairAlign(starLibPathBD, it.toSeq)
        it
      }
    ).repartition(1).saveAsTextFile("file:/root/result1")
    return null;
  }
}
