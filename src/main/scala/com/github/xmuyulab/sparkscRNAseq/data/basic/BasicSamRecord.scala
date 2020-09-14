package com.github.xmuyulab.sparkscRNAseq.data.basic;

/*
 * SAM文件主题区有以下几个字段
 * QNAME 比对的序列名称
 * FLAG 1:该read是成对的paired reads中的一个
 *      2:paired reads中每个都正确比对到参考序列上
 *      4:该序列没比对到参考序列上
 *      8:与该read成对的matepair read没有比对到参考序列上
 *      16:该read其反向互补序列能够比对到参考序列
 *      32:与该read成对的matepair read其反向互补序列能够比对到参考序列
 *      64:在paired reads中，该read食欲参考序列比对的第一条
 *      128:在paired reads中，该read食欲参考序列比对的第二条
 *      256:该read是次优的比对结果
 *      512:该read没有通过质量控制
 *      1024:由于PCR或测序错误产生的重复reads
 *      2048:补充匹配的read
 * RNAME read比对序列的序列名字
 * POS 表示read比对到RNAME这条序列的最左边的位置
 * MAPQ mapping的质量值
 * CIGAR reads mapping到第三列序列的mapping状态
 * MRNM 这条read第二次比对的位置
 * ISIZE 
 */

import java.io.Serializable;
import com.github.xmuyulab.sparkscRNAseq.const.SamRecordConst;

object BasicSamRecord extends Serializable {

    def apply(samLine: String): BasicSamRecord = {
        val splits = org.apache.commons.lang3.StringUtils.split(samLine, '\t')
        val qName = splits(0)
        val flag = splits(1).toInt
        val contigName = splits(2)
        val pos = splits(3).toInt
        val mapQ = splits(4).toInt
        val cigar = splits(5)
        val rawMateContigName = splits(6)
        var mateContigName = ""
        var mateContigId = SamRecordConst.FAKE_CONTIG_ID
        var matePosition = 0
        var infferdSize = 0
        if (!rawMateContigName.equals("*")) {
            if (rawMateContigName.equals("=")) {
                mateContigName = contigName
                //mateContigId = contigId
            } else {
                mateContigName = rawMateContigName
            }
            matePosition = splits(7).toInt
            infferdSize = splits(8).toInt
        }
        var sequence = splits(9)
        var quality = splits(10)
        var attributeList = splits.takeRight(splits.length - 11).toList
        new BasicSamRecord(qName, flag, contigName, pos, mapQ, cigar, mateContigId, mateContigName,
                            matePosition, infferdSize, sequence, quality, attributeList)
    }
    
}

class BasicSamRecord(val qName: String,
                        val flag: Int,
                        //val contigId: Int,
                        val contigName: String,
                        val position: Int,
                        val mapQ: Int,
                        val cigar: String,
                        val mateContigId: Int,
                        val mateContigName: String,
                        val matePosition: Int,
                        val infferdSize: Int,
                        val sequence: String,
                        val quality: String,
                        val attributeList: List[String]) extends Serializable {   
    
    override def toString(): String = {
        "%s\t%d\t%s\t%d\t%d\t%s\t%s\t%d\t%d\t%s\t%s\t%s".format(qName, flag, contigName, position, mapQ, cigar, matePosition, infferdSize,
                            sequence, quality, attributeList)
    }
}