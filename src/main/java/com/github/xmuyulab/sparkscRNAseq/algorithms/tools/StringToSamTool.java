/*
 * @author: 6liuyu123
 * @date: Do not edit
 */
package com.github.xmuyulab.sparkscRNAseq.algorithms.tools;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StringToSamTool implements Serializable {
   public List<String> StringToSam(String[] s) {
       int n = s.length;
       List<String> res = new LinkedList<String>();
       for (int i = 0; i < n; i++) {
       	   String[] tmp = s[i].split("\n");
       	   res.addAll(Arrays.asList(tmp));
       }
       return res;
   }
   public String toString() {
				return "StringToSamTool{}";
			}
}