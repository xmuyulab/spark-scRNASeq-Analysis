/*
 * @author: 6liuyu123
 * @date: 2020/10/27
 */

package com.github.xmuyulab.sparkscRNAseq

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.data.basic.BasicSamRecord
import com.github.xmuyulab.sparkscRNAseq.processes.featurecount.FeatureAndCount
import com.github.xmuyulab.sparkscRNAseq.processes.finding.FindAndJoinProcess
import com.github.xmuyulab.sparkscRNAseq.processes.mapping.JNIStarInitProcess
import com.github.xmuyulab.sparkscRNAseq.utils.StringUtils
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils

import java.util

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.kohsuke.args4j.{Argument, CmdLineParser, Option}

object scAnalysis {
    @Option(required = true, name = "-fq1", usage = "Fastq R1 for test")
    val fastq1: String = StringUtils.EMPTY
    
    @Option(required = true, name = "-fq2", usage = "Fastq R2 for test")
    val fastq2: String = StringUtils.EMPTY

    @Option(required = false, name = "-cellNumber", usage = "extract cell number")
    val cellNumber: String = StringUtils.EMPTY
    
    @Option(required = false, name = "-STARThreads", usage = "set STAR's threads")
    val STARThreads: String = StringUtils.EMPTY

    @Option(required = false, name = "-gtf", usage = "set gtf's path")
    val gtf: String = StringUtils.EMPTY

    @Argument
    val arguments: util.ArrayList[String] = new util.ArrayList[String]()

    def main(args: Array[String]): Unit = {
        val parser: CmdLineParser = new CmdLineParser(this);
        parser.setUsageWidth(300)
        val argList = new util.ArrayList[String]()
        args.foreach(arg => argList.add(arg))
        parser.parseArgument(argList)
        val conf = new SparkConf().setAppName("SparkStar")
                                      .set("spark.driver.maxResultSize", "60g")
                                      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                                      .registerKryoClasses(Array(classOf[FastqRecord], classOf[BasicSamRecord]))
        if (conf.getOption("spark.master").isEmpty) {
            conf.setMaster("local[%d]".format(Runtime.getRuntime.availableProcessors()))
        }
        val sc = new SparkContext(conf)
        val argsUtils = new ArgsUtils(fastq1, fastq2, cellNumber, STARThreads, gtf)
        val extractFastqRdd = FindAndJoinProcess.findAndJoin(sc, argsUtils)
        val samRdd = JNIStarInitProcess.runStar(sc, extractFastqRdd, argsUtils)
        FeatureAndCount.feature(sc, samRdd, argsUtils)
    }
}