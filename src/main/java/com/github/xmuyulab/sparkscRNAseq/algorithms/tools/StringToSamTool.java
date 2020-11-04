/*
 * @author: 6liuyu123
 * @date: Do not edit
 */
package com.github.xmuyulab.sparkscRNAseq.algorithms.tools;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.io.Serializable;
import com.github.xmuyulab.sparkscRNAseq.data.basic.BasicSamRecord;

public class StringToSamTool implements Serializable {
   public List<String> StringToSam(String[] s) {
       int n = s.length;
		     List<String> res = new LinkedList<String>();
		     for (int i = 0; i < n; i++) {
		 	       String[] tmp = s[i].split("\n");
		 	       res.addAll(Arrays.asList(tmp));
		     }
		// res.addAll(Arrays.asList(s));
		     return res;
			}
			
			public List<String> stringToSam(List<String> l) {
							String[] arr = l.toArray(new String[l.size()]);
							return StringToSam(arr);
			}

			public String toString() {
				return "StringToSamTool{}";
			}
}