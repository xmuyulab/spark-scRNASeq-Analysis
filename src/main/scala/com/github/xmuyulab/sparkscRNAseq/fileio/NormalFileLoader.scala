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
                                  filePath2 : String) :  RDD[(Text, (Text, Text))] = {
    //    val whitelistTmp = loadFastqR1ToRdd(sc,filePath1)
    //                      .groupByKey()
    //                      .sortBy(_._2.size,false)
    //                      .take(100)
    //    var whitelist : RDD[(Text, (Text, Text))] = sc.parallelize(whitelistTmp(0)._2.toList).map(line => (new Text(line.toString().substring(10)),(new Text(line.toString().substring(0,10)), whitelistTmp(0)._1)))
    //    for (i <- 1 to 99) {
    //      whitelist.union(sc.parallelize(whitelistTmp(i)._2.toList).map(line => (new Text(line.toString().substring(10)),(new Text(line.toString().substring(0,10)), whitelistTmp(i)._1))))
    //    }
    val fastqR1Rdd = loadFastqR1ToRdd(sc, filePath1)
    val whitelist = sc.parallelize(fastqR1Rdd.map(line => (line._1, 1))
      .reduceByKey((a, b) => a + b)
      .sortBy(_._2, false)
      .take(100))
      .join(fastqR1Rdd)
      .map(line => (new Text(line._2._2.toString().substring(11)), new Text(line._1.toString() + line._2._2.toString().substring(0, 10))))
    val FR2Rdd = loadFastqR2ToRdd(sc, filePath2)
      .join(whitelist)
    FR2Rdd
  }

}
