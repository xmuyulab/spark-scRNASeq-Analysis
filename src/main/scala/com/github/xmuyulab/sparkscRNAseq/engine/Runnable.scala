package com.github.xmuyulab.sparkscRNAseq.engine

/**
 * Author: liuyu
 */
trait Runnable {
  /**
   * 标识该过程是否已经完成
   */
  var done: Boolean = false

  /**
   * 执行过程
   */
  def run()
}
