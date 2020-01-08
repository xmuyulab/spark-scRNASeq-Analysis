/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.fileio

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord

trait FileLoader {

  def loadFastqR1ToRdd(sc:SparkContext,filePath:String):RDD[FastqRecord]
}
