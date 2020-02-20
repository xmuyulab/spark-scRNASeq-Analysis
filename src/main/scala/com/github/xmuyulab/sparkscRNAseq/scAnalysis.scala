/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq

import java.util

import com.github.xmuyulab.sparkscRNAseq.logs.LOG
import com.github.xmuyulab.sparkscRNAseq.utils.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkConf
import org.kohsuke.args4j.{Argument, CmdLineParser, Option}
import com.github.xmuyulab.sparkscRNAseq.utils.StringUtils
import com.github.xmuyulab.sparkscRNAseq.logs.LOG

object scAnalysis {
  //Default arguments
  @Option(required=true,name="-fq1",usage="fastq r1 for test")
  val fastq1:String=StringUtils.EMPTY

  @Option(required=true,name="-fq2",usage="fastq r2 for test")
  val fastq2:String=StringUtils.EMPTY


  @Option(required=true,name="-output",usage="result path")
  val resultPath:String=StringUtils.EMPTY
  //Other arguments
  @Argument
  val arguments:util.ArrayList[String]=new util.ArrayList[String]()

  def main(args:Array[String]):Unit={
    val log=LOG(scAnalysis)
    val parser:CmdLineParser=new CmdLineParser(this);
    parser.setUsageWidth(300);
    val argList=new util.ArrayList[String]()
    args.foreach(arg=>argList.add(arg))
    parser.parseArgument(argList)
    val conf=new SparkConf()
      .setAppName("SparkStar")
      .set("spark.driver.maxResultSize","24g")

    if(conf.getOption("spark.master").isEmpty){
      conf.setMaster("local[%d]".format(Runtime.getRuntime.availableProcessors()))
    }

    val sc=new SparkContext(conf)
    val pipelineName="myPipeline"
    
  }
}
