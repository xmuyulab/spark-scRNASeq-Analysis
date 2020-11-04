/*
 * @author: 6liuyu123
 * @date: 2020/10/27
 */
package com.github.xmuyulab.sparkscRNAseq.utils

import java.io.Serializable


object ArgsUtils extends Serializable  {
    def apply(r1: String, r2: String, cellnumber: String,
                  STARThreads: String, gtf: String) {
        ArgsUtils(r1, r2, cellnumber, STARThreads, gtf)
    }
}

class ArgsUtils(val r1: String, val r2: String, val cellnumber: String,
                    val STARThreads: String, val gtf: String) extends Serializable {
    val R1PATH = r1
    val R2PATH = r2
    val CELLNUMBER = cellnumber.toInt
    val STARTHREADS = STARThreads
    val GTF = gtf
    def getR1Path(): String = {
        R1PATH
    }
    
    def getR2Path(): String = {
        R2PATH
    }
    
    def getCellNumber(): Int = {
        CELLNUMBER
    }
    
    def getStarThreads(): String = {
        STARTHREADS + "\0"
    }

    def getGtfPath(): String = {
        GTF
    }

    override def toString: String = {
        "%s%s%s%s%s".format(R1PATH, R2PATH, CELLNUMBER, STARTHREADS)
    }
}