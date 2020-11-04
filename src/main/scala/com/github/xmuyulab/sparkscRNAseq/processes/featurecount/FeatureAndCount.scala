/*
 * @author: 6liuyu123
 * @date: 2020/11/02
 */

package com.github.xmuyulab.sparkscRNAseq.processes.featurecount

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.FeatureCount
import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInitAdapter
import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.SamMatchGtf
import com.github.xmuyulab.sparkscRNAseq.algorithms.tools.StringToSamTool
import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.fileio.NormalFileLoader
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.hadoop.io.Text

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

object FeatureAndCount {
    def feature(sc: SparkContext, samRdd: RDD[String], argsUtils: ArgsUtils) : Unit = {
        val samListRdd = samRdd.map (
                         line => {
                             var row = line.split('\t')
                             var cbumi = row(0).split('_')
                             (row(2), Array(cbumi(1), cbumi(2), row(3)))
                          }
                      ).groupByKey()
        val gtfListRdd = NormalFileLoader.loadGTFToRdd(sc, argsUtils.getGtfPath())
                         .filter(
                             line => {
                                 var row = line.split('\t')
                                 row(2).equals("exon")
                             }
                          ).map(
                              line => {
                                  var row = line.split('\t')
                                  var feature = row(8).split(";")
                                  (row(0), Array(row(3), row(4), feature(0).substring(9, feature(0).length()-1)))
                              }
                          ).groupByKey()
        val matchRdd = samListRdd.join(gtfListRdd)
                .flatMap(
                    line => {
                        val sMatchG = new SamMatchGtf()
                        sMatchG.samMatchGtf(line._2._1.iterator.toArray, line._2._2.iterator.toArray).toArray
                    }
                ).saveAsTextFile("file:///root/result")
        // .map(
        //     line => {
        //         val fCount = new FeatureCount()
        //         fCount.count(line)
        //     }
        // )
    }
}