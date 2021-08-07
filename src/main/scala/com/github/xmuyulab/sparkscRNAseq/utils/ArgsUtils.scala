/*
 * @author: 6liuyu123
 * @date: 2020/10/27
 */
package com.github.xmuyulab.sparkscRNAseq.utils

import java.io.Serializable

object ArgsUtils extends Serializable  {
    def apply(r1: String, r2: String, cellNumber: String, STARThreads: String, gtf: String, genomeDir: String, worker: String, totalCore: String) {
        ArgsUtils(r1, r2, cellNumber, STARThreads, gtf, genomeDir, worker, totalCore)
    }
}

class ArgsUtils(val r1: String, val r2: String, val cellNumber: String, val STARThreads: String, val gtf: String,
                val genomeDir: String, val worker: String, val totalCore: String) extends Serializable {
    val R1PATH = r1
    val R2PATH = r2
    val CELLNUMBER = cellNumber
    val STARTHREADS = STARThreads
    val GTF = gtf
    val GENOMEDIR = genomeDir
    val WORKER = worker
    val TOTALCORE = totalCore

    def getR1Path(): String = {
        R1PATH
    }
    
    def getR2Path(): String = {
        R2PATH
    }
    
    def getCellNumber(): Int = {
        CELLNUMBER.toInt
    }
    
    def getStarThreads(): String = {
        STARTHREADS + "\0"
    }

    def getGtfPath(): String = {
        GTF
    }

    def getGenomedir(): String = {
        GENOMEDIR
    }

    def getWorker(): Int = {
        WORKER.toInt
    }

    def getTotalCore(): Int = {
        TOTALCORE.toInt
    }

    override def toString: String = {
        "%s%s%s%s%s%s%s%s".format(R1PATH, R2PATH, CELLNUMBER, STARTHREADS, GTF, GENOMEDIR, WORKER, TOTALCORE)
    }

}