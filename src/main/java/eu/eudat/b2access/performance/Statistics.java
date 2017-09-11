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
    
    private String key;
    private List<Double> values = new ArrayList<>();
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private double sum = 0;
    
    Statistics(String key) {
        this.key = key;
    }
    
    public void add(Map<String, Statistic> stat) {
        stats.add(stat);
        
        double current = stat.get(key).getDurationInMs();
        getValues().add(current);
        sum += current;
        if(current < getMin()) {
            min = current;
        }
        if(current> getMax()) {
            max = current;
        }
    }
    
    public void addAll(List<Map<String, Statistic>> s) {
        for(Map<String,Statistic> stat : s) {
            add(stat);
        }
    }
    
    @Override
    public String toString() {
        String result = "";
        result += "*** "+key+" ***\n";
        result += "Individual values:\n";
        for (double value: getValues()) {
            result += "\t"+String.format("%12.4f", value)+"\n";
        }
        result += "Summary:\n";
        result += "\tmin = "+String.format("%12.4f", getMin())+"\n";
        result += "\tmax = "+String.format("%12.4f", getMax())+"\n";
        result += "\tavg = "+String.format("%12.4f", getSum()/getValues().size())+"\n";
        return result;
    }

    /**
     * @return the values
     */
    public List<Double> getValues() {
        return values;
    }

    /**
     * @return the min
     */
    public double getMin() {
        if (min == Double.MAX_VALUE) {
            return 0.0;
        } else {
            return min;
        }
    }

    /**
     * @return the max
     */
    public double getMax() {
        if (max == Double.MIN_VALUE) {
            return 0.0;
        } else {
            return max;
        }
    }

    /**
     * @return the sum
     */
    public double getSum() {
        return sum;
    }
    
    public double getAvg() {
        double avg = getSum()/getValues().size();
        if (avg == Double.NaN) {
            return 0.0;
        } else {
            return avg; 
        }
    }
}
