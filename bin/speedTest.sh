cd ~/spark-scRNASeq-Analysis
rm ~/jars/bin/libSTAR.so
rm ~/jars/yulab-1.0-SNAPSHOT-jar-with-dependencies.jar
mvn clean
mvn clean package
cd ./target/classes
javah com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInit
cp com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit.h ~/spark-scRNASeq-Analysis/src/main/java/com/github/xmuyulab/jstar
cd ~/spark-scRNASeq-Analysis/src/main/java/com/github/xmuyulab/jstar
sed -i '2s/<jni.h>/"jni.h"/g' com_github_xmuyulab_sparkscRNAseq_algorithms_adapter_StarInit.h
#   make clean
cd htslib
#   make clean
cd ..
make all
cd ~/spark-scRNASeq-Analysis
cd ./target/
cp ./yulab-1.0-SNAPSHOT-jar-with-dependencies.jar ~/jars
cp ../src/main/java/com/github/xmuyulab/jstar/libSTAR.so ~/jars/bin
scp -r ~/jars liuyu@slave2:/home/liuyu
scp -r ~/jars liuyu@slave3:/home/liuyu
scp -r ~/jars liuyu@slave4:/home/liuyu
scp -r ~/bin liuyu@slave2:/home/liuyu
scp -r ~/bin liuyu@slave3:/home/liuyu
scp -r ~/bin liuyu@slave4:/home/liuyu
cd ~/bin
sh runScSpark.sh    #   >> log.txt