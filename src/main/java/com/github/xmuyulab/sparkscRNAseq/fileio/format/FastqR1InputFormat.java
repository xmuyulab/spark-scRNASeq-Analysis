/*
  Author: liuyu
 */
package com.github.xmuyulab.sparkscRNAseq.fileio.format;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FastqR1InputFormat extends FileInputFormat<Void,Text> {

  public static class FastqR1RecordReader extends RecordReader<Void,Text>{
    private long start;
    private long end;
    private long pos;
    private Path file;

    private LineReader lineReader;
    private InputStream inputStream;
    private Text currentValue;
    private byte[] newline="\n".getBytes();

    private static final int MAX_LINE_LENGTH=10000;     //How long can a read get?

    public FastqR1RecordReader(Configuration conf,FileSplit split) throws IOException{
      file=split.getPath();     //The file containing this split's data,return Path
      start=split.getStart();   //The position of the first byte in the file to process,return long
      end=start+split.getLength();

      FileSystem fs=file.getFileSystem(conf);
      FSDataInputStream fileIn=fs.open(file);

      CompressionCodecFactory codecFactory=new CompressionCodecFactory(conf);
      CompressionCodec codec=codecFactory.getCodec(file);
    }
  }

  public RecordReader<Void,Text> createRecordReader{
  }

}
