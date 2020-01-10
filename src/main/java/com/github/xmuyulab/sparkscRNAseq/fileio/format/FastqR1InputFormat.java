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

  //interface RecordReader<K,V>
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

      if(codec==null){
        positionAtFirstRecord(fileIn);
        inputStream=fileIn;
      }else{
        if(start!=0){
          throw new RuntimeException("Start position for compressed file is not 0!(found "+start+")");
        }
      }
    }

    private void positionAtFirstRecord(FSDataInputStream stream) throws IOException{
      Text buffer=new Text();

      stream.seek(start);
      LineReader reader=new LineReader(stream);
      int bytesRead=0;
      do{
        bytesRead=reader.readLine(buffer,(int)Math.min(MAX_LINE_LENGTH,end-start));
        int bufferLength=buffer.getLength();
        if(bytesRead>0&&(buffer.getLength()<=0||buffer.getBytes()[0]!='@')){
          start+=bytesRead;
        }else{
          long backtrackPosition=start+bytesRead;
          bytesRead=reader.readLine(buffer)
        }
      }
    }

  }

  public RecordReader<Void,Text> createRecordReader(InputSplit genericSplit,TaskAttemptContext context)
    throws IOException,InterruptedException{
    context.setStatus(genericSplit.toString());
    return new FastqR1RecordReader(context.getConfiguration(),(FileSplit)genericSplit);
  }

}
