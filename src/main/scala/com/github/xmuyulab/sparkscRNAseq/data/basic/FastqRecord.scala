/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.data.basic

import java.io.Serializable

object FastqRecord extends Serializable{
  def apply(descriptionLine: String, sequence: String): FastqRecord = {
    new FastqRecord(descriptionLine, sequence)
  }
}

class FastqRecord(val descriptionLine : String,
                  val sequence : String
                 ) extends Serializable {
  override def toString: String = {
    "%s\n%s\n+\n%s".format(descriptionLine, sequence, sequence)
  }
}