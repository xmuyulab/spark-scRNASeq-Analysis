package com.github.xmuyulab.sparkscRNAseq.engine

import com.github.xmuyulab.sparkscRNAseq.const.BinTools
import com.twitter.chill.ResourcePool
import org.apache.spark.SparkContext


object Pipeline{
    def apply(name:String,sc:SparkContext):Pipeline={
        val pipeline=new Pipeline(name,sc)
        pipeline
    }
}

class Pipeline(val name:String,sc:SparkContext) extends Runnable{
    val doOptimize=BinTools.processOptimize
    private val resourcePool:ResourcePool
}