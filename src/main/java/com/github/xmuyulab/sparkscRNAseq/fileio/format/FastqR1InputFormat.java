package com.github.xmuyulab.sparkscRNAseq.fileio.format;

/**
 * This is Description
 *
 * @author liuyu
 * @date 2020/03/02
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
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


public class FastqR1InputFormat extends FileInputFormat<Text, Text> {
  //The record reader breaks the data into key/value pairs for input to the Mapper
  // @method initialize(InputSplit split,TaskAttemptContext context) throws IOException,InterruptedException
  //  called once at initialization
  // @method nextKeyValue() read the next key,value pair @return true if a key/value pair was read
  // @method getCurrentKey() get the current key @return the current key or null if there is no current key
  // @method getCurrentValue() get the current value @return the object that was read
  // @method getProgress() the current progress of the record reader through its data
  // @method close() close the record reader
  public static class FastqR1RecordReader extends RecordReader<Text, Text> {
    private long start;

    private long end;

    private long pos;

    private Path file;

    //
    private LineReader lineReader;
    //
    private InputStream inputStream;
    //
    private Text currentValue1;
    private Text currentValue2;
    private Text currentValue3;
    private Text currentValue4;
    //
    private byte[] newline = "\n".getBytes();

    //
    private static final int MAX_LINE_LENGTH=10000;

    //A section of an input file
    public FastqR1RecordReader(Configuration conf, FileSplit split) throws IOException {
      //The file containing this split's data
      file = split.getPath();
      //The position of the first byte
      start = split.getStart();

      end = start+split.getLength();

      FileSystem fs = file.getFileSystem(conf);
      FSDataInputStream fileIn = fs.open(file);

      CompressionCodecFactory codecFactory = new CompressionCodecFactory(conf);
      CompressionCodec codec = codecFactory.getCodec(file);

      if (codec == null) { // no codec.  Uncompressed file.
        positionAtFirstRecord(fileIn);
        inputStream = fileIn;
      } else {
        // compressed file
        if (start != 0) {
          throw new RuntimeException("Start position for compressed file is not 0! (found " + start + ")");
        }

        inputStream = codec.createInputStream(fileIn);
        end = Long.MAX_VALUE; // read until the end of the file
      }

      lineReader = new LineReader(inputStream);
    }

    @Override
    public void initialize(InputSplit split,TaskAttemptContext context) throws IOException,InterruptedException{

    }

    private void positionAtFirstRecord(FSDataInputStream stream) throws IOException {
      Text buffer = new Text();

      //seek to the given offset
      stream.seek(start);
      //A class that provides a line reader from an input stream.Depending on the constructor used,
      // lines will either be terminated by:'\n','\r','\r\n',a custom byte sequence delimiter
      LineReader reader = new LineReader(stream);

      int bytesRead = 0;
      do {
        //@return the number of bytes including the newline
        bytesRead = reader.readLine(buffer, (int) Math.min(MAX_LINE_LENGTH, end - start));

        int bufferLength = buffer.getLength();

        if (bytesRead > 0 &&
            (bufferLength <= 0 || buffer.getBytes()[0] != '@')) {
          start += bytesRead;
        } else {
          long backtrackPosition = start + bytesRead;

          bytesRead = reader.readLine(buffer, (int) Math.min(MAX_LINE_LENGTH, end - start));    //Sequence
          bytesRead = reader.readLine(buffer, (int) Math.min(MAX_LINE_LENGTH, end - start));    //'+'

          if (bytesRead > 0 && buffer.getLength() > 0 && buffer.getBytes()[0] == '+') {
            break;
          } else {
            start = backtrackPosition;
            stream.seek(start);
            reader = new LineReader(stream);
          }
        }
      } while (bytesRead > 0);

      stream.seek(start);

      pos = start;

    }

    @Override
    public boolean nextKeyValue() throws IOException {
      currentValue1 = new Text();
      currentValue2 = new Text();
      currentValue3 = new Text();
      currentValue4 = new Text();
      return next(currentValue1, currentValue2, currentValue3, currentValue4);
    }

    @Override
    public Text getCurrentKey() {
      return new Text(currentValue2.toString().substring(0,16));
    }

    @Override
    public Text getCurrentValue(){
      return new Text(currentValue2.toString().substring(16) + currentValue1.toString());
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
      if (start == end)
        return 1.0f;
      else
        return Math.min(1.0f, (pos - start) / (float)(end - start));
    }

    @Override
    public void close() throws IOException{
      inputStream.close();
    }

    public boolean next(Text value1, Text value2, Text value3, Text value4) throws IOException {
      if(pos >= end) {
        return false;
      }
      try {
        Text readName = new Text();
        value1.clear();
        value2.clear();
        value3.clear();
        value4.clear();

        boolean gotData = lowLevelFastqR1Read(readName, value1, value2, value3, value4);

        return gotData;
      } catch(EOFException e) {
        throw new RuntimeException("unexpected end of file in fastq record");
      }
    }

    protected boolean lowLevelFastqR1Read(Text readName, Text value1, Text value2, Text value3, Text value4) throws IOException {

      readName.clear();

      long skipped = appendLineInto(readName,true);

      if(skipped == 0) {
        return false;
      }

      if(readName.getBytes()[0] != '@') {
        throw new RuntimeException("unexpected fastq record didn't start with '@' at "
                    + makePositionMessage() + ".Line: " + readName + ".\n");
      }

      value1.append(readName.getBytes(),0,readName.getLength());

      //sequence
      appendLineInto(value2,false);

      //separator,'+'
      appendLineInto(value3,false);

      //quality
      appendLineInto(value4,false);

      return true;

    }

    private long appendLineInto(Text dest,boolean eofOk) throws IOException{

      Text buf = new Text();
      int bytesRead = lineReader.readLine(buf,MAX_LINE_LENGTH);

      if(bytesRead < 0 || (bytesRead == 0 && !eofOk)){
        throw new EOFException();
      }

      dest.append(buf.getBytes(),0,buf.getLength());
      dest.append(newline,0,1);

      pos += bytesRead;

      return bytesRead;

    }

    public String makePositionMessage(){
      return file.toString()+":"+pos;
    }

  }

  //create a record reader for a given split.The framework will call
  // RecordReader.initialize(InputSplit,TaskAttemptContext before the split is used
  // @param split - the split to be read
  // @param context - the information about the task
  // @return a new record reader
  public RecordReader<Text,Text> createRecordReader(
      InputSplit genericSplit,
      TaskAttemptContext context) throws IOException,InterruptedException{
    context.setStatus(genericSplit.toString());
    return new FastqR1RecordReader(context.getConfiguration(),(FileSplit)genericSplit);
  }

}
