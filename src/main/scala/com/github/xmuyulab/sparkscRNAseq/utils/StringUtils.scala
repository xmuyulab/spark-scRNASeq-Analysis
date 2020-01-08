/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.utils

object StringUtils {

  def EMPTY=""

  def split(str:String,seperator:String):java.util.Iterator[String]={
    com.google.common.base.Splitter.on(seperator).split(str).iterator()
  }

  def isNotEmpty(str:String):Boolean={
    str!=null&&str.length>0
  }

  def isEmpty(str:String):Boolean={
    str==null||str.length==0
  }

}