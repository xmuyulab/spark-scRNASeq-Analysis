// package com.github.xmuyulab.sparkscRNAseq.engine

// import scala.collection.mutable
// import scala.collection.mutable.ListBuffer
// import org.apache.spark.SparkContext
// import com.github.xmuyulab.sparkscRNAseq.const.BinTools
// import com.github.xmuyulab.sparkscRNAseq.execptions.PipelineException
// import com.github.xmuyulab.sparkscRNAseq.resource.{Resource, ResourcePool}

// // scala中的单例模式
// object Pipeline{
//     def apply(name: String, sc: SparkContext): Pipeline = {
//         val pipeline = new Pipeline(name, sc)
//         pipeline
//     }
// }

// class Pipeline(val name: String, sc: SparkContext) extends Runnable {
//     val doOptimize = BinTools.processOptimize         //config.properties True
//     private val resourcePool: ResourcePool = ResourcePool()
//     private val processList: ListBuffer[Process] = ListBuffer()
    
//     private def init(): Unit = {

//     }
//     //生成process的执行列表
//     private def generateRunnableSequence(): List[Process] = {
//         val undone: mutable.HashSet[Process] = mutable.HashSet()
//         val done: ListBuffer[Process] = ListBuffer()
//         // 初始化，将所有Process加入undone
//         undone ++= processList
//         // 拷贝一份临时资源池，用于生成执行顺序时的判断

//         val resourcePoolTmp: ResourcePool = resourcePool.copy()
        
//         // 循环遍历未完成的process列表，直到所有列表都被移入已完成列表中
//         while (undone.nonEmpty) {
//             val processToBeDone: ListBuffer[Process] = ListBuffer()
//             // 如果有process的输入资源ready，则加入到待执行列表
//             undone.foreach(process => {
//                 // 如果有process的输入资源ready，则加入到待执行列表
//                 if (!process.inputResources.exists(
//                     resource => !resourcePoolTmp.containsResource(resource))) {
//                         processToBeDone += process
//                     }
//                 })
//                 //处理待执行列表
//                 if (processToBeDone.isEmpty) {
//                     throw new PipelineException("Can't generate execute topology graph for pipeline " + name)
//                 }
//                 processToBeDone.foreach(process => {
//                     undone.remove(process)
//                     done += process
//                     process.outputResources.foreach(
//                         resource => resourcePoolTmp.addResource(resource)
//                         )
//                     })
//                 }
//                 done.toList
//             }
//             def run(): Unit = {
//                 if (done) {
//                     return
//                 }
//                 init()
//                 if (doOptimize) {
//                     PartitionOptimizer
//                 }
//             }
//         }