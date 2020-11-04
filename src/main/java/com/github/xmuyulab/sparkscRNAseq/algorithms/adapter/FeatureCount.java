/*
 * @author: 6liuyu123
 * @date: 2020/11/02
 */

package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord;
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils;
import com.github.xmuyulab.sparkscRNAseq.algorithms.tools.StringToSamTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FeatureCount {
    public Map<String, Integer> count(Object samList) {
        Map<String, Integer> tmpRes = new HashMap<String, Integer>();
        return tmpRes;
    }
}