package com.github.xmuyulab.sparkscRNAseq.algorithms.tools;

public class STARCommandGenerate {
    private char[][] commandLine;

    public STARCommandGenerate() {
        commandLine=new char[11][];
        for(int i = 0; i < 11; i++){
      		commandLine[i] = new char[64];
      		for(int j = 0;j < 64; j++){
        		commandLine[i][j] = '\0';
      		}
    	}
		commandLine[0] = "STAR\0".toCharArray();
    	commandLine[1] = "--runThreadN\0".toCharArray();
    	commandLine[2] = "4\0".toCharArray();
    	commandLine[3] = "--genomeDir\0".toCharArray();
    	commandLine[4] = "/home/liuyu/STAR_INDEX\0".toCharArray();
    	commandLine[5] = "--readFilesIn\0".toCharArray();
    	commandLine[6] = "/mnt/spark/hgmm_100_R2_extracted.fastq.gz\0".toCharArray();
    	commandLine[7] = "--outFilterMultimapNmax\0".toCharArray();
    	commandLine[8] = "1\0".toCharArray();
    	commandLine[9] = "--outSAMtype\0".toCharArray();
    	commandLine[10] = "SAM\0".toCharArray();
    }

    // public STARCommandGenerate(int processNum, String outDir);

    public char[][] getCommandLine() {
        return this.commandLine;
	}
}