// package com.github.xmuyulab.sparkscRNAseq.engine

// import com.github.xmuyulab.sparkscRNAseq.resource.{Resource, ResourcePool}
// import org.apache.spark.SparkContext
// import scala.collection.mutable.ListBuffer

// trait Process extends Runnable {

// /**
//  * 读取inputResource和写入outputResource的资源池
//  */

//     protected var resourcePool: ResourcePool
//     protected var sc: SparkContext
    
//     protected var pipeline: Pipeline
//     def setResourcePool(pool: ResourcePool): Unit = {
//         resourcePool = pool
//     }
    
//     def getResourcePool(): ResourcePool = {
//         resourcePool
//     }
    
//     def setSparkContext(sc: SparkContext): Unit = {
//         this.sc = sc
//     }
    
//     def getSparkContext(): SparkContext = {
//         sc
//     }
    
//     def setPipeline(pipeline: Pipeline): Unit = {
//         this.pipeline = pipeline
//     }
    
//     def getPipeline(): Pipeline = {
//         pipeline
//     }
    
//     val inputResources: ListBuffer[Resource] = ListBuffer()
//     val outputResources: ListBuffer[Resource] = ListBuffer()

//     def dependsOn(other: Process): Boolean = {
//         inputResources.exists(resource => other.outputResources.contains(resource))
//     }
// }