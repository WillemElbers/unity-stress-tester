/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eudat.b2access.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wilelb
 */
public class Statistics {
    
    private List<Map<String, Statistic>> stats = new ArrayList<>();
            
    public void add(Map<String, Statistic> stat) {
        stats.add(stat);
    }
    
    @Override
    public String toString() {
        String result = "";
        /*
        printStat("get", statistics);
        printStat("render", statistics);
        printStat("authn", statistics);
        printStat("total", statistics);
        */
        for (Map<String, Statistic> stat: stats) {
            result += printStat("authn", stat) +"\n";
        }
        return result;
    }
    
    protected String printStat(String key, Map<String, Statistic> statistics) {
        return String.format("%7s: ", key)+": "+String.format("%12.4f", statistics.get(key).getDurationInMs())+"ms.";
    }
}
