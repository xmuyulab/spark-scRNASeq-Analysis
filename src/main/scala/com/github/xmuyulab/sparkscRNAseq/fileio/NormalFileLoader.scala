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

object NormalFileLoader extends FileLoader {

  def transFilePath(filePath:String):String= {
    if (filePath.startsWith("hdfs://")) {
      filePath
    } else if (filePath.startsWith("file://")) {
      filePath
    } else {
      "file://" + filePath
    }
  }

  override def loadWhitelistToRdd(sc: SparkContext, filePath: String): RDD[String] = {
    val whitelist = sc.textFile(transFilePath(filePath))
    whitelist
  }

  override def loadFastqR1ToRdd(sc: SparkContext, filePath: String): RDD[(Text, Text)] = {
    val records = sc.newAPIHadoopFile(
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
                                  filePath2 : String) :  RDD[FastqRecord] = {
    val fastqR1Rdd = loadFastqR1ToRdd(sc, filePath1)
    // key:ine1
    // value:line2(16)+_+line2(0,16)
    val FR1RDD = sc.parallelize(fastqR1Rdd.map(line => (line._1, 1))
      .reduceByKey((a, b) => a + b)
      .sortBy(_._2, false)
      .take(100))
      .join(fastqR1Rdd)
      .map(line => (new Text(line._2._2.toString().substring(11)), new Text(line._1.toString() + "_" + line._2._2.toString().substring(0, 10))))

    //  key:  fastq2.line1+fastq1.line2()
    //  value:  fastq2.line2+line3+line4
    val whitelist = loadFastqR2ToRdd(sc, filePath2)
        .join(FR1RDD)
        .map(
          line => {
            FastqRecord(line._1.toString() + "_" + line._2._2.toString(), line._2._1.toString())
          }
        )

    whitelist
  }

}
