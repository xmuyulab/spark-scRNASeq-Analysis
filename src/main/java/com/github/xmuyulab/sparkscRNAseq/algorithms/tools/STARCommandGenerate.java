/*
 * @author: 6liuyu123
 * @date: Do not edit
 */
package com.github.xmuyulab.sparkscRNAseq.algorithms.tools;

import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils;

public class STARCommandGenerate {
    private char[][] commandLine;

    public STARCommandGenerate(ArgsUtils argsUtils) {
		commandLine = new char[11][];
		for (int i = 0; i < 11; i++) {
			commandLine[i] = new char[64];
			for (int j = 0; j < 64; j++) {
				commandLine[i][j] = '\0';
			}
		}
		commandLine[0] = "STAR\0".toCharArray();
		commandLine[1] = "--runThreadN\0".toCharArray();
		commandLine[2] = argsUtils.getStarThreads().toCharArray();
		commandLine[3] = "--genomeDir\0".toCharArray();
		commandLine[4] = (argsUtils.getGenomedir()+"\0").toCharArray();
		commandLine[5] = "--readFilesIn\0".toCharArray();
		commandLine[6] = "not_used_now\0".toCharArray();
		commandLine[7] = "--outFilterMultimapNmax\0".toCharArray();
		commandLine[8] = "1\0".toCharArray();
		commandLine[9] = "--outSAMtype\0".toCharArray();
		commandLine[10] = "SAM\0".toCharArray();
	}

    public char[][] getCommandLine() {
        return this.commandLine;
	}
}