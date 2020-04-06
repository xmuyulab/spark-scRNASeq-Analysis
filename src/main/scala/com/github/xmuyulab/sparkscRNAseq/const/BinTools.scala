/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.const

import java.io.FileInputStream
import java.util.Properties

import com.github.xmuyulab.sparkscRNAseq.utils.FileUtils

object BinTools {
  val binDirPath=FileUtils.join(FileUtils.getDirPathNoEndSeparator(
    FileUtils.getDirPathNoEndSeparator(
      FileUtils.getDirPathNoEndSeparator(
        FileUtils.getDirPathNoEndSeparator(
          FileUtils.getDirPathNoEndSeparator(
            FileUtils.getDirPathNoEndSeparator(
              FileUtils.getDirPathNoEndSeparator(
                this.getClass().getResource("").getPath()))))))),"bin")

  val confPath={
    val tmpPath=FileUtils.join(binDirPath,"config.properties")
    if(tmpPath.startsWith("file:")) tmpPath.substring(5) else tmpPath
  }

  val processOptimize={
    val properties=new Properties()
    properties.load(new FileInputStream(confPath))
    properties.getProperty("processOptimize").toBoolean
  }
}