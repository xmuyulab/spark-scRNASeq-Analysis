# spark-scRNASeq-Analysis
use spark to improve scRNASeq analysis

project structure

|-bin<br>
-|-config.properties                        全局参数<br>
-|-runScSpark.sh                            运行脚本<br>
|-src<br>
-|-main<br>
--|-java<br>
---|-resource<br>
----|-logback.xml                           日志配置文件<br>
--|-scala<br>
---|-com.github.xmuyulab.sparkscRNAseq<br>
----|-const<br>
-----|-BinTools.scala                       读取配置文件<br>
----|-data<br>
-----|-basic<br>
------|-FastqRecord.scala                   序列化fastq文件<br>
----|-engine<br>
-----|-Pipeline.scala                       运行流程<br>
----|-scAnalysis.scala                      启动文件<br>