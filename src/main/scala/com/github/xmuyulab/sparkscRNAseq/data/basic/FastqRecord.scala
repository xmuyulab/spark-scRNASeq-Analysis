/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.data.basic

import java.io.Serializable

object FastqRecord extends Serializable{
  def apply(descriptionLine:String,sequence:Array[Byte],quality:Array[Byte],compressFlag:Boolean):FastqRecord={
    new FastqRecord(compressFlag,descriptionLine,sequence,quality)
  }
}

class FastqRecord(val compressFlag : Boolean,
                  val descriptionLine : String,
                  val sequence : Array[Byte],
                  val quality : Array[Byte]
                 ) extends Serializable {
  override def toString: String = {
    "%s\n%s\n+\n%s".format(descriptionLine,sequence,quality)
  }
}