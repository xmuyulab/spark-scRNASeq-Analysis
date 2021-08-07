package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import com.github.xmuyulab.sparkscRNAseq.algorithms.tools.STARCommandGenerate;
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils;

import java.util.List;

public class StarAlign {
    private final StarInit starInit;
    private final ArgsUtils argsUtils;
    
    public StarAlign(StarInit starInit, ArgsUtils argsUtils) {
        this.starInit = starInit;
        this.argsUtils = argsUtils;
    }

    public void tranFastq(final List<String> reads) {
        tranFastq(starInit.cAddress, reads.toArray(new String[reads.size()]));
    }

    public String[] startAlign() {
        STARCommandGenerate sCommand = new STARCommandGenerate(argsUtils);
        return runStar(starInit.cAddress, sCommand.getCommandLine());
    }

    private native void tranFastq(long cAddress, final String fqR[]);
    private native String[] runStar(long cAddress, char[][] commandLine);
}