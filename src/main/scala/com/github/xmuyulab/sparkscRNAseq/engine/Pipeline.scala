//package com.github.xmuyulab.sparkscRNAseq.engine
//
//import scala.collection.mutable
//import scala.collection.mutable.ListBuffer
//
//import org.apache.spark.SparkContext
//
//import com.github.xmuyulab.sparkscRNAseq.const.BinTools
//import com.github.xmuyulab.sparkscRNAseq.resource.ResourcePool
//
//
//object Pipeline{
//    def apply(name:String,sc:SparkContext):Pipeline={
//        val pipeline=new Pipeline(name,sc)
//        pipeline
//    }
//}
//
//class Pipeline(val name:String,sc:SparkContext) extends Runnable{
//    val doOptimize=BinTools.processOptimize         //config.properties True
//
//    private val resourcePool:ResourcePool=ResourcePool()
//    private val processList:ListBuffer[Process]=ListBuffer()
//
//    private def init():Unit={
//
//    }
//
//    private def generateRunnableSequence():List[Process]={
//        val undone:mutable.HashSet[Process]=mutable.HashSet()
//        val done:ListBuffer[Process]=ListBuffer()
//
//        undone++=processList
//
//    }
//
//}