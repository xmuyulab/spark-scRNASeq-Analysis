package com.github.xmuyulab.sparkscRNAseq.resource

import scala.collection.mutable

import com.github.xmuyulab.sparkscRNAseq.execptions.ResourceException

/**
 * Author:liuyu
 */

object ResourcePool {
  def apply():ResourcePool=new ResourcePool()
}

class ResourcePool{

  private val resourceMap:mutable.HashMap[String,Resource]=mutable.HashMap()
  private val resourceSet:mutable.HashSet[Resource]=mutable.HashSet()

  def addResource(resource: Resource):Unit={
    if(resourceMap.contains(resource.key)){
      throw new ResourceException("Same resource key: "+resource.key)
    }
    resourceMap.put(resource.key,resource)
    resourceSet.add(resource)
  }

}