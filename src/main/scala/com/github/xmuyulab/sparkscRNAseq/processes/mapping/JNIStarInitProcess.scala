package com.github.xmuyulab.sparkscRNAseq.processes.mapping

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInitAdapter
import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.collection.JavaConversions._
import org.apache.hadoop.io.Text
import scala.util.control.Breaks._
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue

object JNIStarInitProcess {

  def bfs(graph: Array[Array[Boolean]], visited: Array[Boolean], p: Int, n: Int): Unit = {
    var q = new Queue[Int]
    var i = 0
    q += p
    while (!q.isEmpty()) {
      var head = q.dequeue()
      visited(head) = true
      i = 0
      while (i < n) {
        if (graph(head)(i) && !visited(i)) {
          q += i
        }
        i = i+1
      }
    }
  }

  def delRepeat(sim: ListBuffer[String], simTimes: ListBuffer[Int]): Int = {
    var n = sim.size()
    var res = 0
    var gra = new Array[Array[Boolean]](n)
    var visited = new Array[Boolean](n)
    var i = 0
    var j = 0
    var k = 0
    var d = 0
    var sl = sim(0).length()
    while (i < n) {
      visited(i) = false
      gra(i) = new Array[Boolean](n)
      j = 0
      while (j < n) {
        gra(i)(j) = false
        j = j+1
      }
      i = i+1
    }
    i = 0
    while (i < n) {
      j = i
      while (j < n) {
        k = 0
        d = 0
        breakable {
          while (k < sl) {
            if (sim(i).charAt(k) != sim(j).charAt(k)) {
              d = d+1
              if (d > 1) {
                break()
              }
            }
            k = k+1
          }
        }
        if (d <= 1) {
          if (simTimes(i) >= simTimes(j)*2) {
            gra(i)(j) = true
          }
          if (simTimes(j) >= simTimes(i)*2) {
            gra(j)(i) = true
          }
        }
        j = j+1
      }
      i = i+1
    }
    i = 0
    while (i < n) {
      if (!visited(i)) {
        bfs(gra, visited, i, n)
        res = res+1
      }
      i = i+1
    }
    return res
  }
  
  def count(waitCount: List[(String, Int)]): TreeMap[String, Int] = {
    var pre0: String = ""
    var pre1: String = ""
    var pre2: String = ""
    var judge = true
    var times = 0
    var res: TreeMap[String, Int] = TreeMap()
    var p:Array[String] = new Array[String](3)
    var sim: ListBuffer[String] = new ListBuffer[String]
    var simTimes: ListBuffer[Int] = new ListBuffer[Int]
    var m = 0
    var wl = waitCount.size()
    while (m < wl) {
      var key = waitCount(m)._1
      p = key.split('_')
      if (judge) {
        pre0 = p(0)
        pre1 = p(1)
        pre2 = p(2)
        judge = false
      } else {
        if (pre0 == p(0) && pre1 == p(1)) {
          times = times+1
          sim.append(pre2)
          simTimes.append(waitCount(m)._2)
          pre2 = p(2)
        } else {
          if (times == 0) {
            res += ((pre0+"_"+pre1) -> 1)
            pre0 = p(0)
            pre1 = p(1)
            pre2 = p(2)
          } else {
            sim.append(pre2)
            simTimes.append(waitCount(m)._2)
            res += ((pre0+"_"+pre1) -> delRepeat(sim, simTimes))
            //res += ((pre0+"_"+pre1) -> sim.size())
            sim.clear()
            times = 0
            pre0 = p(0)
            pre1 = p(1)
            pre2 = p(2)
          }
        }
      }
      m = m+1
    }
    if (pre0 == p(0) && pre1 == p(1)) {
      times = times+1
      sim.append(pre2)
      simTimes.append(waitCount(wl-1)._2)
      res += ((pre0+"_"+pre1) -> delRepeat(sim, simTimes))
      //res += ((pre0+"_"+pre1) -> sim.size())
      sim.clear()
    }
    return res
  }

  def runStar(sc: SparkContext,
              extractedFastq: RDD[FastqRecord],
              argsUtils: ArgsUtils): Void = {

    val starLibPath = BinTools.starLibPath
    val starLibPathBD =  sc.broadcast(starLibPath).value
    //  val referencePathBD = sc.broadcast(referencePath).value
    System.out.println("############### Here is JNIStarInitProcess. ###############\n")
    var samRdd = extractedFastq.repartition(2).mapPartitions(
      it => {
        StarInitAdapter.pairAlign(starLibPathBD, it.toSeq, argsUtils).iterator
      }
    ).filter(_.contains("NH:i:1")).filter { 
        line =>
        line.split('\t')(13).substring(5).toInt > 35
      }.map {
        line =>{
          var row = line.split('\t')
          var cbumi = row(0).split('_')
          (row(2),(cbumi(1),cbumi(2),row(3)))}
      }.groupByKey()
      var gtfRdd = sc.textFile("file:///root/data/gencode.v31.annotation.gtf")
      .filter(_.charAt(0) != '#')
      .filter{ line =>
        var row = line.split('\t')
        row(2).equals("exon")
      }.map{line =>
        var row = line.split('\t')
        var feature = row(8).split(";")
        (row(0),(row(3),row(4),feature(0).substring(9,feature(0).length()-1)))
      }.groupByKey()
      val joinRdd = samRdd.join(gtfRdd).map(
        line => {
          var res:TreeMap[String, Int] = TreeMap()
          var array1 = line._2._1.iterator.toArray.sortBy(r => (r._3.toInt))(Ordering.Int)
          var array2 = line._2._2.iterator.toArray.sortBy(r => (r._1.toInt))(Ordering.Int)
          var i = 0
          var j = 0
          var n1 = array1.length
          var n2 = array2.length
          var target = 1
          var begin = 1
          var end = 1
          for (i <- i until n1) {
            breakable{
              for (k <- j until n2) {
                target = array1(i)._3.toInt
                begin = array2(k)._1.toInt
                end = array2(k)._2.toInt
                 if (target >= begin) {
                   if (target <= end) {
                     var tmp = array2(k)._3+"_"+array1(i)._1+"_"+array1(i)._2
                     if (res.contains(tmp)) {
                       var times = 1 + res.get(tmp).getOrElse(0)
                       res += (tmp -> times)
                     } else {
                       res += (tmp -> 1)
                     }
                   }
                 } else {
                   if (j != 0) {
                     j = (k-1)
                   }
                   break()
                 }
                 j = k
               }
             }
           }

          (count(res.toList))          
        }
      ).saveAsTextFile("file:///root/result")
    System.out.println("############### Here is JNIStarInitProcess. ###############\n")
    return null;
  }
}
