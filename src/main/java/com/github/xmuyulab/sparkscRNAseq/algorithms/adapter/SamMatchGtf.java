/*
 * @author: 6liuyu123
 * @date: Do not edit
 */

package com.github.xmuyulab.sparkscRNAseq.algorithms.adapter;

import com.github.xmuyulab.sparkscRNAseq.data.basic.FastqRecord;
import com.github.xmuyulab.sparkscRNAseq.utils.ArgsUtils;
import com.github.xmuyulab.sparkscRNAseq.algorithms.tools.StringToSamTool;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SamMatchGtf {

    public void dfs(ArrayList<ArrayList<Integer>> graph, boolean[] visited, int begin) {
        int neighborNum = graph.get(begin).size();
        visited[begin] = true;
        for (int i = 0; i < neighborNum; i++) {
            if (!visited[graph.get(begin).get(i)]) {
                dfs(graph, visited, graph.get(begin).get(i));
            }
        }
    }

    public int countComponent(ArrayList<ArrayList<Integer>> graph) {
        int vertexNum = graph.size();
        int res = 0;
        boolean[] visited = new boolean[vertexNum];
        for (int i = 0; i < vertexNum; i++) {
            visited[i] = false;
        }
        for (int i = 0; i < vertexNum; i++) {
            if (!visited[i]) {
                res++;
                dfs(graph, visited, i);
            }
        }
        return res;

    }


    public int count(Map<String, Integer> m) {
        // create graph
        int i, j, k, d, n = m.size();
        String[] barcode = new String[n];
        int[] times = new int[n];
        ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>(n);
        i = 0;
        for (Map.Entry<String, Integer> entry : m.entrySet()) {
            barcode[i] = entry.getKey();
            times[i] = entry.getValue();
            graph.add(i, new ArrayList<Integer>());
            i++;
        }
        int barcodeLength = barcode[0].length();
        for (i = 0; i < n; i++) {
            for (j = i; j < n; j++) {
                for (k = 0, d = 0; k < barcodeLength; k++) {
                    if (barcode[i].charAt(k) != barcode[j].charAt(k)) {
                        d++;
                    }
                    if (d > 1) {
                        break;
                    }
                }
                if (d < 2) {
                    if (times[i] >= 2*times[j]) {
                        graph.get(i).add(j);
                    } else {
                        if (times[j] >= 2*times[i]) {
                            graph.get(j).add(i);
                        }
                    }
                }
            }
        }
        return countComponent(graph);
    }

    public List<String> samMatchGtf(String[][] samArray, String[][] gtfArray) {
        List<String> res = new ArrayList<String>();
        Map<String, Map<String, Integer>> m = new HashMap<String, Map<String, Integer>>();
        int samLength = samArray.length;
        int gtfLength = gtfArray.length;
        int target, begin, end, i, j = 0, k;
        
        // sort samArray according to position
        Arrays.sort(samArray, new Comparator<String[]>() {
            public int compare(String[] a, String[] b) {
                return Integer.parseInt(a[2]) - Integer.parseInt(b[2]);
            }
        });
        // sort gtfArray according to start position
        Arrays.sort(gtfArray, new Comparator<String[]>() {
            public int compare(String[] a, String[] b) {
                return Integer.parseInt(a[0]) - Integer.parseInt(b[0]);
            }
        });

        for (i = 0; i < samLength; i++) {
            target = Integer.parseInt(samArray[i][2]);
            for (k = j; k < gtfLength; k++) {
                begin = Integer.parseInt(gtfArray[k][0]);
                end = Integer.parseInt(gtfArray[k][1]);
                if (target >= begin) {
                    if (target <= end) {
                        String tmp = gtfArray[k][2] + "\t" + samArray[i][0];
                        Map<String, Integer> mi = m.getOrDefault(tmp, new HashMap<String, Integer>());
                        mi.put(samArray[i][1], mi.getOrDefault(samArray[i][1], 0) + 1);
                        m.put(tmp, mi);
                        j = k;
                        break;
                    }
                    j = k;
                } else {
                    if (j != 0) {
                        j = k - 1;
                        break;
                    }
                }
            }
        }

        int mLength = m.size(), times;

        Iterator<Map.Entry<String, Map<String, Integer>>> entries = m.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry)entries.next();
            String cellAndUMI = (String)entry.getKey();
            times = count((Map<String, Integer>)entry.getValue());
            res.add(cellAndUMI + "\t" + times);
            entries.remove();
        }

        return res;
    }
}