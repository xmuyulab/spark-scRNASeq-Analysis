/*
 * @author: 6liuyu123
 * @date: 2020/11/02
 */

package com.github.xmuyulab.sparkscRNAseq.processes.featurecount

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.SamMatchGtf
import com.github.xmuyulab.sparkscRNAseq.fileio.NormalFileLoader
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext

object FeatureAndCount {
    def feature(sc: SparkContext, samRdd: RDD[String], argsUtils: ArgsUtils) : Unit = {
        val samListRdd = samRdd.map(line => {
                                                 val row = line.split('\t')
                                                 val cb_umi = row(0).split('_')
                                                 (row(2), Array(cb_umi(1), cb_umi(2), row(3)))
                                            }).groupByKey(argsUtils.getWorker() * 32)
        val gtfListRdd = NormalFileLoader.loadGTFToRdd(sc, argsUtils.getGtfPath())
                                         .filter(line => {
                                             line.split('\t')(2).equals("exon")
                                         }).map(line => {
                                             val row = line.split('\t')
                                             val feature = row(8).split(";")
                                             (row(0),Array(row(3),row(4),feature(0).substring(9,feature(0).length()-1)))
                                         }).groupByKey(argsUtils.getWorker() * 32)
        val sMatchG = new SamMatchGtf()
        samListRdd.join(gtfListRdd)
                .flatMap(
                    line => {
                        sMatchG.samMatchGtf(line._2._1.iterator.toArray, line._2._2.iterator.toArray).toArray
                    }
                ).saveAsTextFile("file:///root/result")
    }
}