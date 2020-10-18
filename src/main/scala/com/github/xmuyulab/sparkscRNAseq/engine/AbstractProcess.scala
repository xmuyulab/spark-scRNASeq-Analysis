// package com.github.xmuyulab.sparkscRNAseq.engine

// import org.apache.spark.SparkContext
// import com.github.xmuyulab.sparkscRNAseq.execptions.PipelineException
// import com.github.xmuyulab.sparkscRNAseq.resource.{Resource, ResourcePool}

// abstract class AbstractProcess() extends Process {

//   // Process接口中resourcePool和sc的实现
//   protected var pipeline: Pipeline = null
//   protected var resourcePool: ResourcePool = null
//   protected var sc: SparkContext = null
//   var name: String = null

//   def this(name: String) {
//     this()
//     this.name = name
//     this.inputResources ++= getInputResourceList()
//     this.outputResources ++= getOutputResourceList()
//   }

//   /**
//     * 负责将output的资源set掉。
//     */
//   def runProcess(): Unit

//   def getInputResourceList(): List[Resource]

//   def getOutputResourceList(): List[Resource]

//   override def run(): Unit = {
//     if (resourcePool == null) {
//       throw new PipelineException("Resource pool is not set yet <process: " + name + ">")
//     }

//     // 检查存在未set的输出resource
//     if (outputResources == null || !outputResources.exists(resource => !resource.isSet)) {
//       return
//     }

//     // 检查所有的输入resource是否已经存在
//     if (inputResources != null && inputResources.exists(
//       resource => !(resourcePool.containsResource(resource) && resource.isSet))) {
//       throw new PipelineException("Missing resource for <process" + name + ">")
//     }

//     // 运行process
//     runProcess()

//     // 将输出的resource加入资源池
//     if (outputResources != null) {
//       outputResources.foreach(resource => {
//         if (!resource.isSet) {
//           throw new PipelineException("Output resource is not set in <process" + name + ">")
//         }
//         resourcePool.addResource(resource)
//       })
//     }

//     done = true
//   }
// }