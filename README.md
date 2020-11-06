<!--
 * @author: 6liuyu123
 * @date: 2020/11/05
-->
# spark-scRNASeq-Analysis


Use spark to improve scRNASeq analysis

### Environment
1. Java(Upper 8)
2. Scala
3. Apache Hadoop
4. Apache Spark
5. Maven

### How to run?
1. Set Java's environment and Scala's environment
2. Build Spark's cluster
3. make .so file
```shell
cd /src/main/java/com/github/xmuyulab/sparkscRNAseq/jstar
make all
cp libSTAR.so -/bin
```
4. make .jar file
```shell
mvn clean package
```
5. Set .sh and run
```shell
sh runScSpark.sh
```

### Project structure
main: scAnalysis.scala
Refactoring: Later will complete this component.

### Attention
1. The output location can't set in shell, we will improve soon. And you can change in processes/featurecount/FeatureAndCount.scala
2. If you have any problem need to solve soon, you can contact 1272635425@qq.com
