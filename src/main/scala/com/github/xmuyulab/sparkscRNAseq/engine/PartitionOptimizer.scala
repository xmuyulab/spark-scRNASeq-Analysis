package com.github.xmuyulab.sparkscRNAseq.engine

object PartitionOptimizer{
  def markProcess(rawProcessList: List[Process]): Unit = {
    val processList = rawProcessList.filter(_.isInstanceOf)
  }
}
