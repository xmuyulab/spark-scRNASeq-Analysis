# spark-scRNASeq-Analysis
use spark to improve scRNASeq analysis

project structure

|-bin
 |-config.properties            全局参数
 |-runScSpark.sh                运行脚本
|-src
 |-main
  |-java
  |-resource
   |-logback.xml                日志配置文件
  |-scala
   |-com.github.xmuyulab.sparkscRNAseq
   |-const
    |-BinTools.scala            读取配置文件
   |-data
    |-basic
     |-FastqRecord.scala        序列化fastq文件
   |-engine
    |-Pipeline.scala            运行流程
   |-scAnalysis.scala           启动文件