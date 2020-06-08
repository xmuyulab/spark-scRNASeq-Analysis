package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import com.github.xmuyulab.sparkscRNAseq.jstar.jni.StarInit;
//import com.github.xmuyulab.sparkscRNAseq.execptions.PipelineException;

import java.io.File;
import java.io.IOException;

/**
 * This is Description
 *
 * @author liuyu
 * @date 2020/04/27
 */
public class StarInitAdapter {

  private static volatile StarInit starInitInstance = null;

  private static StarInit getStarInitInstance(String starJNILibPath) throws IOException {
    if (starInitInstance == null) {
      synchronized (StarInitAdapter.class) {
        if (starInitInstance == null) {
          System.loadLibrary(starJNILibPath);
          starInitInstance = new StarInit();
        }
      }
    }
    return starInitInstance;
  }

  public static void pairAlign(String starJNILibPath) {
    System.out.println("############Here is StarInitAdapter.############\n");
    StarInit starInit = null;
    try {
      starInit = getStarInitInstance(starJNILibPath);
    } catch (IOException e) {
      System.out.println("############Error when load index in JNI STAR.\n############\n");
      e.printStackTrace();
      //  throw new PipelineException("Error when load index in JNI STAR\n");
    }
  }

}