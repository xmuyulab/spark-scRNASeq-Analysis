package com.github.xmuyulab.sparkscRNAseq.processes.mapping

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInitAdapter
import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.hadoop.io.Text

object JNIStarInitProcess {
  def runStar(sc: SparkContext,
              extractedFastq: RDD[(Text, Iterable[(Text, Text, Text)])]): Void = {

    val starLibPath = BinTools.starLibPath
    val starLibPathBD =  sc.broadcast(starLibPath).value
    //  val referencePathBD = sc.broadcast(referencePath).value
    System.out.println("############Here is JNIStarInitProcess.############\n")
    val test1 = extractedFastq.map(
      line => {
        (StarInitAdapter.pairAlign(starLibPathBD))
      }
    )
    test1.collect().foreach(print)
    return null;
  }
}
