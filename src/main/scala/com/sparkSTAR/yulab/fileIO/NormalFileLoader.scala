/**
 * Author: liuyu
 */
package com.sparkSTAR.yulab.fileIO

import org.apache.hadoop.io.Text

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import com.sparkSTAR.yulab.data.basic.FastqRecord

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
