package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord;

import java.io.IOException;

import java.util.List;

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

  public static List<FastqRecord> pairAlign(String starJNILibPath, List<FastqRecord> fastqRecords) throws IOException {
    StarInit starInit = null;
    try {
      starInit = getStarInitInstance(starJNILibPath);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int chunkSize = 1000;

    return fastqRecords;
  }

}