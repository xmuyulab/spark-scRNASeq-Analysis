package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import java.io.*;
import java.util.List;
import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord;
import com.github.xmuyulab.sparkscRNAseq.algorithms.tools.STARCommandGenerate;

public class StarAlign {
    private StarInit starInit = null;
    
    public StarAlign(StarInit starInit) {
        this.starInit = starInit;
    }

    public void tranFastq(final List<String> reads) throws IOException {
        tranFastq(starInit.cAddress, reads.toArray(new String[reads.size()]));
    }

    public String startAlign() {
        STARCommandGenerate sCommand = new STARCommandGenerate();
        return runStar(starInit.cAddress, sCommand.getCommandLine());
    }

    private native void tranFastq(long cAddress, final String fqR[]);
    private native String runStar(long cAddress, char[][] commandLine);
}