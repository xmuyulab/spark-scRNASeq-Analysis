package com.github.xmuyulab.sparkscRNAseq.algorithms.tools;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.github.xmuyulab.sparkscRNAseq.data.basic.BasicSamRecord;

public class StringToSamTool {
    public List<String> StringToSam(String s) {
		String[] waitChange = s.split("\n");
		int n = waitChange.length;
		List<String> res = new ArrayList<String>(n);
		Collections.addAll(res, waitChange);
		return res;
	}
}