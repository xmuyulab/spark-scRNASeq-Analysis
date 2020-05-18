package com.github.xmuyulab.sparkscRNAseq.engine

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import org.apache.spark.SparkContext

import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import com.github.xmuyulab.sparkscRNAseq.resource.{ResourcePool,Resource}


object Pipeline{
  def apply(name: String, sc: SparkContext): Pipeline = {
    val pipeline = new Pipeline(name, sc)
    pipeline
  }
}

class Pipeline(val name: String, sc: SparkContext) extends Runnable{
  val doOptimize = BinTools.processOptimize         //config.properties True

  private val resourcePool: ResourcePool = ResourcePool()
  private val processList: ListBuffer[Process] = ListBuffer()

  private def init(): Unit = {

  }

  //生成process的执行列表
  private def generateRunnableSequence(): List[Process] = {
    val undone: mutable.HashSet[Process] = mutable.HashSet()
    val done: ListBuffer[Process] = ListBuffer()

    undone ++= processList

    val resourcePoolTmp: ResourcePool = resourcePool.copy()

    while (undone.nonEmpty) {
      val processToBeDone: ListBuffer[Process] = ListBuffer()
      undone.foreach(process => {
        if (!process.inputResources.exists(
          resource => !resourcePoolTmp.containsResource(resource))) {
          processToBeDone += process
        }
      })
      //处理待执行列表
      if (processToBeDone.isEmpty) {
        throw new PipelineException("Can't generate execute topology graph for pipeline " + name)
      }
      processToBeDone.foreach(process => {
        undone.remove(process)
        done += process
        process.outputResources.foreach(
          resource => resourcePoolTmp.addResource(resource)
        )
      })
    }
    done.toList
  }

  def run(): Unit = {
    if (done) {
      return
    }

    init()

    if (doOptimize) {
      Partition
    }
  }
}