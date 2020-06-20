cd ~/spark-scRNASeq-Analysis
cd ./target/
cp ./yulab-1.0-SNAPSHOT-jar-with-dependencies.jar ~/bin
cp ./src/main/java/com/github/xmuyulab/jstar/libSTAR.so ~/jars/bin
scp -r ~/jars liuyu@slave2:/home/liuyu
scp -r ~/jars liuyu@slave3:/home/liuyu
scp -r ~/jars liuyu@slave4:/home/liuyu
cd ~/bin
sh runScSpark.sh