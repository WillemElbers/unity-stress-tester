package eu.eudat.b2access.performance;

/**
 *
 * @author wilelb
 */
public class Statistic {
    
    private final String name;
    private long t1;
    private long t2;
    
    public Statistic(String name) {
        this.name = name;
    }
    
    public void start() {
        this.t1 = System.nanoTime();
    }
    
    public void stop() {
        this.t2 = System.nanoTime();
    }
    
    public long getDelta() {
        return this.t2 = this.t1;
    }
    
    public double getDurationInMs() {
        return getDelta()/1000000.0;
    }
    
    public double getDurationInS() {
        return getDelta()/1000000000.0;
    }
}
