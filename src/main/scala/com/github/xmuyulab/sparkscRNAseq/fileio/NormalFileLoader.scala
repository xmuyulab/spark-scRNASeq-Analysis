/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.fileio

import org.apache.hadoop.io.Text
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.fileio.format.FastqR1InputFormat

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
    val records=sc.newAPIHadoopFile(      //newAPIHadoopFile(path,format,key,value)
      transFilePath(filePath),
      classOf[FastqR1InputFormat],
      classOf[Void],
      classOf[Text]
    )

    val compressFlagValue=sc.broadcast(true).value
    records.map(record=>{
      val strIter=StringUtils
    })
  }

}
