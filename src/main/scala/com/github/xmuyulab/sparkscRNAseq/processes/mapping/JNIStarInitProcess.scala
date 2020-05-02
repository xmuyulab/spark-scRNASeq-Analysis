package com.github.xmuyulab.sparkscRNAseq.processes.mapping

import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object JNIStarMemProcess {
  def runStar(sc: SparkContext,
              referencePath: String,
              extractedFastq: RDD[(String,(String,String,Int))]): Void = {

    val starLibPath = BinTools.starLibPath
    val starLibPathBD =  sc.broadcast(starLibPath).value
    val referencePathBD = sc.broadcast(referencePath).value

    extractedFastq.mapPartitions(record => {

    })
    return null;
  }
}
