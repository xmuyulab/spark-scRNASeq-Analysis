//package com.github.xmuyulab.sparkscRNAseq.resource
//
//import scala.collection.mutable
//
//import com.github.xmuyulab.sparkscRNAseq.execptions.ResourceException
//
///**
// * Author:liuyu
// */
//
//object ResourcePool {
//  def apply(): ResourcePool = new ResourcePool()
//}
//
//class ResourcePool{
//
//  //维持两套hash，以hashMap为主，通过resource自带的key索引，hashSet用于快速值索引
//  private val resourceMap: mutable.HashMap[String, Resource] = mutable.HashMap()
//  private val resourceSet: mutable.HashSet[Resource] = mutable.HashSet()
//
//  //将资源添加金资源池，有重复的key抛出异常
//  def addResource(resource: Resource): Unit = {
//    if (resourceMap.contains(resource.key)){
//      throw new ResourceException("Same resource key: " + resource.key)
//    }
//    resourceMap.put(resource.key, resource)
//    resourceSet.add(resource)
//  }
//
//  //将资源添加进资源池，如果有重复的key，则替换原有的resource
//  def replaceResource(resource: Resource): Unit = {
//    if (resourceMap.contains(resource.key)) {
//      val resourceOld = resourceMap.get(resource.key).get
//      resourceSet.remove(resourceOld)
//    }
//    resourceMap.put(resource.key, resource)
//    resourceSet.add(resource)
//  }
//
//  def containsResourceKey(key: String): Boolean = {
//    resourceMap.contains(key)
//  }
//
//  def containsResource(resource: Resource): Boolean = {
//    resourceSet.contains(resource)
//  }
//
//  def copy(): ResourcePool = {
//    val newPool = ResourcePool()
//    resourceSet.foreach(resource => newPool.addResource((resource)))
//    newPool
//  }
//
//}