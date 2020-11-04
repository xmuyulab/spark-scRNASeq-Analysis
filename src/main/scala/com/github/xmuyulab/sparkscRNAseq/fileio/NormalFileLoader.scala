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
    
    def transFilePath(filePath:String) : String = {
        if (filePath.startsWith("hdfs://")) {
            filePath
        } else if (filePath.startsWith("file://")) {
            filePath
        } else {
            "file://" + filePath
        }
    }

    override def loadWhitelistToRdd(sc: SparkContext, filePath: String) : RDD[String] = {
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
        val records = sc.newAPIHadoopFile(
            transFilePath(filePath),            //path
            classOf[FastqR2InputFormat],        //F
            classOf[Text],                      //K
            classOf[Text]                       //V
        )
        records
    }

    override def loadGTFToRdd(sc: SparkContext, filePath: String): RDD[String] = {
        val GTF = sc.textFile(filePath).filter(_.charAt(0) != '#')
        GTF
    }

}
