/**
 * Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.utils

import java.io.{File, FileInputStream}
import java.util.Properties

import org.apache.commons.io.FilenameUtils

object FileUtils {
    def isExists(path:String):Boolean={
        new File(path).exists()
    }

    def getDirPathNoEndSeparator(path:String):String={
        FilenameUtils.getFullPathNoEndSeparator(path)
    }

    def join(basePath:String,fileName:String):String={
        FilenameUtils.concat(basePath,fileName)
    }
}
