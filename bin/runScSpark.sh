spark_master=spark://master:7077
driver_memory=30G
executor_memory=30G
total_executor_cores=1024

spark-submit --class com.github.xmuyulab.sparkscRNAseq.scAnalysis \
 --master ${spark_master} \
 --driver-memory ${driver_memory} \
 --executor-memory ${executor_memory} \
 --total-executor-cores ${total_executor_cores} \
 /root/spark-scRNASeq-Analysis/target/yulab-1.0-SNAPSHOT-jar-with-dependencies.jar \
 -fq1 /root/data/R1.fastq \
 -fq2 /root/data/R2.fastq