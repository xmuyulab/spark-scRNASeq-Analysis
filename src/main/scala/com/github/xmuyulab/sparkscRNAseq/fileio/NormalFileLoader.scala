/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.fileio

import org.apache.hadoop.io.Text
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.fileio.format.FastqR1InputFormat
import com.github.xmuyulab.sparkscRNAseq.fileio.format.FastqR2InputFormat

object NormalFileLoader extends FileLoader{

  def transFilePath(filePath:String):String= {
    if (filePath.startsWith("hdfs://")) {
      filePath
    } else if (filePath.startsWith("file://")) {
      filePath
    } else {
      "file://" + filePath
    }
  }

  //[K,V,F<:NewInputFormat[K,V]],return RDD[(K,V)]
  override def loadFastqR1ToRdd(sc:SparkContext,filePath:String):RDD[(Text,Text)]={
    val records=sc.newAPIHadoopFile(
      transFilePath(filePath),            //path
      classOf[FastqR1InputFormat],        //F
      classOf[Text],                      //K
      classOf[Text]                       //V
    )
    records
  }

  override def loadFastqR2ToRdd(sc: SparkContext, filePath: String): RDD[(Text, Text)] = {
    val records=sc.newAPIHadoopFile(
      transFilePath(filePath),            //path
      classOf[FastqR2InputFormat],        //F
      classOf[Text],                      //K
      classOf[Text]                       //V
    )
    records
  }

  override def loadFastqPairToRdd(sc : SparkContext,
                                  filePath1 : String,
                                  filePath2 : String) : Void ={//RDD[(String, (String, String, Int))]={
    val whitelist = sc.parallelize(loadFastqR1ToRdd(sc,filePath1)
                                    .groupByKey()
                                    .sortBy(_._2.size,false)
                                    .take(100))
                                    .foreach(line => print(line._1))
    null
//    val fastqR2Rdd = loadFastqR2ToRdd(sc,filePath2)
//              .join(whitelist)
//              .map(line =>
//                (new Text(line._2._1.toString().substring(10,line._2._1.toString().length()-16)),(line._1.toString()+line._2._1.toString().substring(0,10),line._2._2)))
////              .join(fastqR2Rdd)
////              .map(line=>(line._1.toString(),(line._2._1._1,line._2._2.toString(),line._2._1._2)))
  }

}
