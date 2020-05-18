spark_master=spark://master:7077
driver_memory=30G
executor_memory=30G
total_executor_cores=1024

spark-submit --class org.ncic.bioinfo.sparkseq.WGSPipeline \
 --master ${spark_master} \
 --driver-memory ${driver_memory} \
 --executor-memory ${executor_memory} \
 --total-executor-cores ${total_executor_cores} \
 /PATH/TO/GPF/target/spark-seq-0.9.0-jar-with-dependencies.jar \
 -runThreadN 4 \
 -readFilesCommand zcat \
 -ref /PATH/TO/DATA/human_g1k_v37.fasta \
 -outFilterMultimapNmax 1 \
 -outSAMtype BAM_SortedByCoordinate \
 -genomeDir /PATH/TO/DATAhg38_noalt_junc85-89.dir \
 -fq1 /PATH/TO/DATA/1.fastq \
 -fq2 /PATH/TO/DATA/2.fastq \
 -output /PATH/TO/OUTPUT/result.vcf