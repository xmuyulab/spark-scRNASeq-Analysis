/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.fileio

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import org.apache.hadoop.io.Text
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class NormalFileLoader extends FileLoader{

  def transFilePath(filePath:String):String= {
    if (filePath.startsWith("hdfs://")) {
      filePath
    } else if (filePath.startsWith("file://")) {
      filePath
    } else {
      "file://" + filePath
    }
  }

  override def loadFastqR1ToRdd(sc:SparkContext,filePath:String):RDD[FastqRecord]={
    val records=sc.textFile(filePath)

    val compressFlagValue=sc.broadcast(true).value
    records.map(record=>{
      val strIter
    })
  }

}
