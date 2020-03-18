package com.github.xmuyulab.sparkscRNAseq.jstar;

import com.github.xmuyulab.sparkscRNAseq.jstar.JniParameters;

/**
 * 目前的使用需要新建一个java工程，native代码生成的动态链接库(.so)在bin目录下，目前实现了类的调用
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