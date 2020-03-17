package com.github.xmuyulab.sparkscRNAseq.jstar;

import com.github.xmuyulab.sparkscRNAseq.jstar.JniParameters;

/**
 * This is Description
 *
 * @author liuyu
 * @date 2020/03/16
 */
public class STAR {

  static{
    System.load("/Users/liuyu/Desktop/spark-scRNASeq-Analysis/bin/libParameters.so");
  }

  public static void main(String[] args){
    JniParameters jniParameters=new JniParameters();
  }

}