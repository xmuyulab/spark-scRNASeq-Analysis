/**
 * Author: liuyu
 */
package com.sparkSTAR.yulab.fileIO

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import com.sparkSTAR.yulab.data.basic.FastqRecord

trait FileLoader {

  def loadFastqR1ToRdd(sc:SparkContext,filePath:String):RDD[FastqRecord]
}
