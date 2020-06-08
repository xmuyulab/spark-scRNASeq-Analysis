package com.github.xmuyulab.sparkscRNAseq.processes.mapping

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInitAdapter
import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.hadoop.io.Text

object JNIStarInitProcess {
  def runStar(sc: SparkContext,
              extractedFastq: RDD[(Text, (Text, Text))]): Void = {

    val starLibPath = BinTools.starLibPath
    val starLibPathBD =  sc.broadcast(starLibPath).value
    //  val referencePathBD = sc.broadcast(referencePath).value

    extractedFastq.map(record => {
      StarInitAdapter.pairAlign(starLibPathBD)
    })
    return null;
  }
}
