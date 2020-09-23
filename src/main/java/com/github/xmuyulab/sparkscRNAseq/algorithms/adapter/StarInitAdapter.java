package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord;
import com.github.xmuyulab.sparkscRNAseq.algorithms.tools.StringToSamTool;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

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

  public static List<String> pairAlign(String starJNILibPath, List<FastqRecord> fastqRecords) throws IOException {
    StarInit starInit = null;
    StarAlign starAlign = null;
    try {
      starInit = getStarInitInstance(starJNILibPath);
      starAlign = new StarAlign(starInit);
    } catch (IOException e) {
      e.printStackTrace();
    }
    int chunkSize = 2000;
    List<String> reads = new ArrayList<>(chunkSize);
    for(FastqRecord fRecord : fastqRecords) {
      if (reads.size() == chunkSize) {
        starAlign.tranFastq(reads);
        reads.clear();
        //break;
      }
      reads.add(fRecord.toString());
    }
    StringToSamTool stringToSamTool = new StringToSamTool();
    List<String> res;
    res = stringToSamTool.StringToSam(starAlign.startAlign()); 
    return res;
  }

}