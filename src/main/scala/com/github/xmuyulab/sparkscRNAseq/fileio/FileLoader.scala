package com.github.xmuyulab.sparkscRNAseq.fileio

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.hadoop.io.Text

trait FileLoader {

  def loadWhitelistToRdd(sc: SparkContext, filePath: String): RDD[String]

  def loadFastqR1ToRdd(sc: SparkContext, filePath: String): RDD[(Text, Text)];

  def loadFastqR2ToRdd(sc: SparkContext, filePath: String): RDD[(Text, Text)];

  def loadFastqPairToRdd(sc: SparkContext,
                         filePath1: String,
                         filePath2: String) :  RDD[FastqRecord];
}
