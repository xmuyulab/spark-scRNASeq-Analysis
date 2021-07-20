package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

/**
 * This is Description
 *
 * @author liuyu
 * @date 2020/04/27
 */

public class StarInit {

  protected long cAddress;

  public native long getAddress();

  public StarInit() {
    cAddress = getAddress();
  }

}

//  ./STAR --runThreadN 4 --genomeDir /mnt/md0/liuyu/STARExample --readFilesIn /mnt/md0/liuyu/hgmm_100_R2_extracted.fastq.gz --readFilesCommand zcat --outFilterMultimapNmax 1 --outSAMtype BAM SortedByCoordinate