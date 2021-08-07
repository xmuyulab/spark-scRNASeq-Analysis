spark_master=spark://master:7077
driver_memory=32G
executor_memory=8G
total_executor_cores=1024

spark-submit --class com.github.xmuyulab.sparkscRNAseq.scAnalysis \
 --master ${spark_master} \
 --driver-memory ${driver_memory} \
 --executor-memory ${executor_memory} \
 --total-executor-cores ${total_executor_cores} \
 /home/liuyu/bin/yulab-1.0-SNAPSHOT-jar-with-dependencies.jar \
 -fq1 /mnt/spark/dataset/10k_pbmc/640m/pbmc_10k_v3_1.fastq \
 -fq2 /mnt/spark/dataset/10k_pbmc/640m/pbmc_10k_v3_2.fastq \
 -cellNumber 1000 \
 -STARThreads 16 \
 -genomeDir /mnt/spark/dataset/index \
 -gtf file:///mnt/spark/dataset/gencode.v31.annotation.gtf \
 -worker 3 \
 -totalCore 32