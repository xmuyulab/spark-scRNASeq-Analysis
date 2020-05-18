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
                                  filePath2 : String) : RDD[(Text, (Text, Iterable[Text]))] = {
    val whitelist = sc.parallelize(loadFastqR1ToRdd(sc,filePath1)
                                    .groupByKey()
                                    .sortBy(_._2.size,false)
                                    .take(100))
                                    //.foreach(line => println(line._1.toString))
    val fastqR2Rdd = loadFastqR2ToRdd(sc,filePath2)
                                    .join(whitelist)
    fastqR2Rdd
  }

}
