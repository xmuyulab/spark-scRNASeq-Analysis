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
      myParameters[i]=new char[64];
      for(int j=0;j<64;j++){
        myParameters[i][j]='\0';
      }
    }
    myParameters[0]="STAR --runThreadN 4 \\".toCharArray();
    myParameters[1]="--genomeDir hg38_noalt_junc85-89.dir \\".toCharArray();
    myParameters[2]="--readFilesIn hgmm_100_R2_extracted.fastq.gz \\".toCharArray();
    myParameters[3]="--readFilesCommand zcat \\".toCharArray();
    myParameters[4]="--outFilterMultimapNmax 1 \\".toCharArray();
    myParameters[5]="--outSAMtype BAM SortedByCoordinate".toCharArray();
    ParametersInputParameters(nativeParameters,6,myParameters);
  }
}

