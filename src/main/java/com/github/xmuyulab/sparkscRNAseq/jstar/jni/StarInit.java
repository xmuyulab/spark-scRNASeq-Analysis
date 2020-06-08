package com.github.xmuyulab.sparkscRNAseq.jstar.jni;

/**
 * This is Description
 *
 * @author liuyu
 * @date 2020/04/27
 */

import java.io.IOException;

public class StarInit {

  protected long ParametersAddress = 0L;
  protected long GenomeAddress = 0L;
  protected long TranscriptomeAddress = 0L;
  protected long sjdbAddress = 0L;

  public StarInit() throws IOException {
    char[][] myParameters=new char[14][];
    for(int i = 0; i < 14; i++){
      myParameters[i] = new char[64];
      for(int j = 0;j < 64; j++){
        myParameters[i][j] = '\0';
      }
    }
    myParameters[0] = "STAR\0".toCharArray();
    myParameters[1] = "--runThreadN\0".toCharArray();
    myParameters[2] = "4\0".toCharArray();
    myParameters[3] = "--genomeDir\0".toCharArray();
    myParameters[4] = "/mnt/md0/liuyu/STARExample\0".toCharArray();
    myParameters[5] = "--readFilesIn\0".toCharArray();
    myParameters[6] = "/mnt/md0/liuyu/hgmm_100_R2_extracted.fastq.gz\0".toCharArray();
    myParameters[7] = "--readFilesCommand\0".toCharArray();
    myParameters[8] = "zcat\0".toCharArray();
    myParameters[9] = "--outFilterMultimapNmax\0".toCharArray();
    myParameters[10] = "1\0".toCharArray();
    myParameters[11] = "--outSAMtype\0".toCharArray();
    myParameters[12] = "BAM\0".toCharArray();
    myParameters[13] = "SortedByCoordinate\0".toCharArray();
    long[] Address = openParameters(14, myParameters);
    ParametersAddress = Address[0];
    GenomeAddress = Address[1];
    TranscriptomeAddress = Address[2];
    sjdbAddress = Address[3];
//openStar(ParametersAddress, GenomeAddress, TranscriptomeAddress, sjdbAddress);
  }

  @Override
  protected void finalize() {
  }

  private static native long[] openParameters(int reference, char[][] readFilesCommand) throws IOException;

  //  private static native void openStar(long pAddress, long gAddress, long tAddress, long sjdbAddress);

  //  private static native long openGenome(long ParametersAddress);

}

//./STAR --runThreadN 4 --genomeDir /mnt/md0/liuyu/STARExample --readFilesIn /mnt/md0/liuyu/hgmm_100_R2_extracted.fastq.gz --readFilesCommand zcat --outFilterMultimapNmax 1 --outSAMtype BAM SortedByCoordinate