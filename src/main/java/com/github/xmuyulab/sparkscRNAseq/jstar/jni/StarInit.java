package com.github.xmuyulab.sparkscRNAseq.jstar.jni;

/**
 * This is Description
 *
 * @author liuyu
 * @date 2020/04/27
 */

import java.io.IOException;

public class StarInit {
  protected long classAddress = 0L;

  public StarInit(String reference) throws IOException {
    classAddress = _open("hg38_noalt_junc85-89.dir", "zcat", reference);
  }

  @Override
  protected void finalize() {
  }

  private static native long _open(String genomeDir, String readFilesCommand, String reference) throws IOException;

}
