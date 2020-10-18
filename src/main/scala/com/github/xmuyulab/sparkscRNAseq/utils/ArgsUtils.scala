package com.github.xmuyulab.sparkscRNAseq.utils

import java.io.Serializable

/**
 * Author: 6liuyu123
 */

object ArgsUtils extends Serializable  {
    def apply(r1: String,
                r2: String,
                cellnumber: String,
                STARThreads: String) {
        ArgsUtils(r1, r2, cellnumber, STARThreads)
    }
    
}

class ArgsUtils(val r1: String,
                val r2: String,
                val cellnumber: String,
                val STARThreads: String) extends Serializable {
    val R1Path = r1
    val R2Path = r2
    val cellN = cellnumber.toInt
    val STARTs = STARThreads

    def getR1Path(): String = {
        R1Path
    }

    def getR2Path(): String = {
        R2Path
    }

    def getCellNumber(): Int = {
        cellN
    }

    def getSTARTs(): String = {
        STARTs + "\0"
    }

    override def toString: String = {
        "%s%s%s%s".format(R1Path, R2Path, cellN, STARTs)
    }
}