package com.github.xmuyulab.sparkscRNAseq.jstar;

/**
 * This is Description
 *
 * @author liuyu
 * @date 2020/03/16
 */
public class JniParameters {

  long nativeParameters;

  private native long createNativeParameters();

  public JniParameters(){
    nativeParameters=createNativeParameters();
  }
}
