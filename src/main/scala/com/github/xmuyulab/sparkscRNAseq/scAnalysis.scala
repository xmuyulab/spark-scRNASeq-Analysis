/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq

import java.util

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.logs.LOG
import com.github.xmuyulab.sparkscRNAseq.processes.mapping.JNIStarInitProcess
import com.github.xmuyulab.sparkscRNAseq.utils.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkConf
import org.kohsuke.args4j.{Argument, CmdLineParser, Option}
import com.github.xmuyulab.sparkscRNAseq.utils.StringUtils
//import com.github.xmuyulab.sparkscRNAseq.engine.Pipeline
import com.github.xmuyulab.sparkscRNAseq.fileio.NormalFileLoader
import com.github.xmuyulab.sparkscRNAseq.logs.LOG
import org.apache.hadoop.io.Text;

object scAnalysis {
  //Default arguments
  @Option(required = true, name = "-ref", usage = "reference")
  val reference: String = StringUtils.EMPTY

  @Option(required = true, name = "-runThreadN", usage = "how many thread star process run")
  val runThreadN: String = StringUtils.EMPTY

  @Option(required = true, name = "-readFilesCommand", usage = "STAR's parameter")
  val readFilesCommand: String = StringUtils.EMPTY

  @Option(required = true, name = "-outFilterMultimapNmax", usage = "STAR's parameter")
  val outFilterMultimapNmax: String = StringUtils.EMPTY

  @Option(required = true, name = "-genomeDir", usage = "STAR's parameter")
  val genomeDir: String = StringUtils.EMPTY

  @Option(required = true, name = "-fq1", usage = "fastq r1 for test")
  val fastq1: String = StringUtils.EMPTY

  @Option(required = true, name = "-fq2", usage = "fastq r2 for test")
  val fastq2: String = StringUtils.EMPTY

  @Option(required = true, name = "-output", usage = "result path")
  val resultPath: String = StringUtils.EMPTY
  //Other arguments
  @Argument
  val arguments: util.ArrayList[String] = new util.ArrayList[String]()

  def main(args: Array[String]): Unit={
    val log = LOG(scAnalysis)

    //对参数进行解析
    val parser: CmdLineParser = new CmdLineParser(this);
    parser.setUsageWidth(300);
    val argList = new util.ArrayList[String]()
    args.foreach(arg => argList.add(arg))
    parser.parseArgument(argList)

    val conf = new SparkConf()
      .setAppName("SparkStar")
      .set("spark.driver.maxResultSize", "36g")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array(classOf[FastqRecord]))

    if(conf.getOption("spark.master").isEmpty){
      conf.setMaster("local[%d]".format(Runtime.getRuntime.availableProcessors()))
    }

    val sc = new SparkContext(conf)
    //  val pipelineName = "myPipeline"
    //  val pipeline = Pipeline(pipelineName, sc)

    val extractFastqRdd = NormalFileLoader.loadFastqPairToRdd(sc, fastq1, fastq2)

//    val fastqPartitions = extractFastqRdd.map(line =>
//                                          (line._2._2.toString().substring(0,16), ( line._1.toString() + "\n" + line._2._1.toString() + "\n" + line._2._2.toString().substring(16))))
//                                          .groupByKey()
//                                          .repartition(4)

    val afterMapping = JNIStarInitProcess.runStar(sc, extractFastqRdd)

  }
}
