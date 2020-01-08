/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.logs

import org.slf4j.LoggerFactory
import ch.qos.logback.core.util.StatusPrinter
import ch.qos.logback.classic.LoggerContext

object LOG{
  def apply(className: Object):LOG=new LOG(this.getClass)
}

class LOG(className:Object) extends App{
  def logger=LoggerFactory.getLogger(className.getClass)

  def DEBUG(content:String):Unit={
    logger.debug(content)
  }

  def INFO(content:String):Unit={
    logger.info(content)
  }

  def WARN(content:String):Unit={
    logger.warn(content)
  }

  def ERROR(content:String):Unit={
    logger.error(content)
  }

}
