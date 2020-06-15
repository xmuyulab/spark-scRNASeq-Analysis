package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import com.github.xmuyulab.sparkscRNAseq.algorithms.adapter.StarInit;
//import Java.io.IOException;
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
          System.load(starJNILibPath);
          starInitInstance = new StarInit();
        }
      }
    }
    return starInitInstance;
  }

  public static String pairAlign(String starJNILibPath) throws IOException {
    //  System.out.println("############Here is StarInitAdapter.############\n");
    StarInit starInit = null;
    try {
      starInit = getStarInitInstance(starJNILibPath);
    } catch (IOException e) {
      //  System.out.println("############Error when load index in JNI STAR.\n############\n");
      e.printStackTrace();
      return "Error when load index in JNI STAR\n";
      //  throw new PipelineException("Error when load index in JNI STAR\n");
    }
    starInit.openGenome(starInit.ParametersAddress, starInit.GenomeAddress, starInit.TranscriptomeAddress, starInit.sjdbAddress);
    return "Hello\n";
  }

}