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

  private native void ParametersInputParameters(long address,int argInN,char[][] argIn);

  public JniParameters(){
    nativeParameters=createNativeParameters();
    char[][] myParameters=new char[6][];
    for(int i=0;i<6;i++){
      myParameters[i]=new char[6];
      for(int j=0;j<6;j++){
        myParameters[i][j]='a';
      }
    }
    ParametersInputParameters(nativeParameters,6,myParameters);
  }
}

