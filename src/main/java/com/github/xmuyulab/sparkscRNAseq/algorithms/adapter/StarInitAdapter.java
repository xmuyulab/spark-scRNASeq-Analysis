package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import java.io.IOException;

//import Java.io.IOException;
//import com.github.xmuyulab.sparkscRNAseq.execptions.PipelineException;

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
          System.load(starJNILibPath);
          starInitInstance = new StarInit();
        }
      }
    }
    return starInitInstance;
  }

  public static String pairAlign(String starJNILibPath) throws IOException {
    StarInit starInit = null;
    try {
      starInit = getStarInitInstance(starJNILibPath);
    } catch (IOException e) {
      e.printStackTrace();
      return "Error when load index in JNI STAR\n";
    }
    if (starInit == null) {
      return "starInit is null.\n";
    }
    return starInit.startAlign();
  }

}