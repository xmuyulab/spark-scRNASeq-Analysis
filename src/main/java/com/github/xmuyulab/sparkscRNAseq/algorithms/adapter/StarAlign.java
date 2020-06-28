package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import java.io.*;
import java.util.List;

public class StarAlign {
    private StarInit starinit = null;
    
    public StarAlign(StarInit starinit) {
        this.starinit = starinit;
    }

    public void align(final List<FastqRecord> reads) throws IOException {
        return align(this.starinit, reads);
    }

    private native void align(StarInit starinit, final FastqRecord fqR) throws IOException;
}