/*
 * @author: 6liuyu123
 * @date: Do not edit
 */
/*
 * @author: 6liuyu123
 * @date: Do not edit
 */
package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import com.github.xmuyulab.sparkscRNAseq.algorithms.tools.StringToSamTool;
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord;
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

  public static List<String> pairAlign(String starJNILibPath, List<FastqRecord> fastqRecords, ArgsUtils argsUtils) {
    StarInit starInit;
    StarAlign starAlign = null;
    try {
      starInit = getStarInitInstance(starJNILibPath);
      starAlign = new StarAlign(starInit, argsUtils);
    } catch (IOException e) {
      e.printStackTrace();
    }
    int chunkSize = 2000;
    List<String> reads = new ArrayList<>(chunkSize);
    for(FastqRecord fRecord : fastqRecords) {
      if (reads.size() == chunkSize) {
        assert starAlign != null;
        starAlign.tranFastq(reads);
        reads.clear();
      }
      reads.add(fRecord.toString());
    }
    StringToSamTool stringToSamTool = new StringToSamTool();
    assert starAlign != null;
    return stringToSamTool.StringToSam(starAlign.startAlign());
  }

}