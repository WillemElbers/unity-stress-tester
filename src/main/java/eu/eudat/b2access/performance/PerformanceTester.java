package eu.eudat.b2access.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 * @author wilelb
 */
public class PerformanceTester {

    public static void main(String[] args) {
        String username = "willem";
        String password = "blaaat";        
        String displayName = "test@willemelbers.nl";
        String url = "https://unity.eudat-aai.fz-juelich.de:8443/home/home";
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.OFF);
        new PerformanceTester(url, username, password, displayName).setThreads(5).setNumTestsPerThreads(20).run();
    }
    
    private final String username;
    private final String password;   
    private final String displayName;
    private final String url;
    
    private int numThreads = 1;
    private int numTestsPerThread = 1;
    
    private OutputFormat format = OutputFormat.TSV;
    
    /**
     * 
     * @param url       unity url for home endpoint
     * @param username  username to use during authentication
     * @param password  password to use during authentication
     * @param displayName display name associated with this account, used to verify the response after succesful auhtentication
     */
    public PerformanceTester(String url, String username, String password, String displayName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        System.setProperty("webdriver.chrome.driver", "/Users/wilelb/Downloads/chromedriver");
    }

    /**
     * Set the number of threads to use. Each thread will manage its own 
     * webdriver.
     * 
     * @param n The number of threads to use.
     * @return 
     */
    public PerformanceTester setThreads(int n) {
        this.numThreads = n;
        return this;
    }
    
    /**
     * Set the number of tests to run per thread. The webdriver will be 
     * re-created for each test.
     * 
     * @param n The number of tests to run per thread
     * @return 
     */
    public PerformanceTester setNumTestsPerThreads(int n) {
        this.numTestsPerThread = n;
        return this;
    }
    
    /**
     * Set the output format used when printing results.
     * 
     * @param format
     * @return 
     */
    public PerformanceTester setOutputformat(OutputFormat format) {
        this.format = format;
        return this;
    }
    
    /**
     * Run the actual tests based on the configured number of threads and tests
     * per thread.
     */
    public void run() {
        System.out.println(String.format("Running %d threads, %d test per thread totalling %d tests.", numThreads, numTestsPerThread, numThreads*numTestsPerThread));
        
        //Create all testers
        Tester[] testers = new Tester[this.numThreads];
        for(int i = 0; i < this.numThreads; i++) {
            testers[i] = new Tester(url, username, password, displayName, this.numTestsPerThread);
        };
        
        //Start all tester threads
        long t1 = System.nanoTime();
        List<Thread> threads = new ArrayList<>(testers.length);        
        for(int i = 0; i < testers.length; i++) {            
            Thread t = new Thread(testers[i], "Tester"+i);
            threads.add(t);
            t.start();
        }
        
        //Wait for all tester threads to finish
        for(Thread t: threads) {
            try {
                t.join();
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        long t2 = System.nanoTime();
        double tDelta = (t2 - t1)/1000000.0;
        System.out.println(String.format("Running tests finished in %.2fms.", tDelta));
        
        //Process results
        Statistics statsTotal = new Statistics("total");
        Statistics statsGet = new Statistics("get");
        Statistics statsRender = new Statistics("render");
        Statistics statsAuthn = new Statistics("authn");
        for(Tester t : testers) {
            statsTotal.addAll(t.getStatistics());
            statsGet.addAll(t.getStatistics());
            statsRender.addAll(t.getStatistics());
            statsAuthn.addAll(t.getStatistics());
        }
        
        //Display results
        switch(format) {
            case PRETTY: 
                    printPretty(statsTotal, statsGet, statsRender, statsAuthn, tDelta);
                break;
            case TSV: 
                    printTsv(statsTotal, statsGet, statsRender, statsAuthn, tDelta);
                break;
            default: 
                    printPretty(statsTotal, statsGet, statsRender, statsAuthn, tDelta);
                break;
                    
        }
    } 
    
    /**
     * Output the results in tsv (tab separated values) format to stdout. This allows for easy copy-paste into
     * a spreadsheet application such as google sheets.
     * 
     * @param statsTotal
     * @param statsGet
     * @param statsRender
     * @param statsAuthn
     * @param msElapsed 
     */
    public void printTsv(Statistics statsTotal, Statistics statsGet, Statistics statsRender, Statistics statsAuthn, double msElapsed) {
        System.out.println(String.format("threads\t%d\ttests\t%d\ttotal tests\t%d\ttime\t%.4f", numThreads, numTestsPerThread, numThreads*numTestsPerThread, msElapsed));
        System.out.println(String.format("%s\t%s\t%s\t%s\t%s", "", "Total", "Get", "Render", "Authn"));
        System.out.println(String.format("%s\t%.2f\t%.2f\t%.2f\t%.2f", "min", statsTotal.getMin(), statsGet.getMin(), statsRender.getMin(), statsAuthn.getMin()));
        System.out.println(String.format("%s\t%.2f\t%.2f\t%.2f\t%.2f", "max", statsTotal.getMax(), statsGet.getMax(), statsRender.getMax(), statsAuthn.getMax()));
        System.out.println(String.format("%s\t%.2f\t%.2f\t%.2f\t%.2f", "avg", statsTotal.getAvg(), statsGet.getAvg(), statsRender.getAvg(), statsAuthn.getAvg()));
    }
    
    /**
     * Pretty print the result to stdout in a human readable format.
     * 
     * @param statsTotal
     * @param statsGet
     * @param statsRender
     * @param statsAuthn
     * @param msElapsed 
     */
    public void printPretty(Statistics statsTotal, Statistics statsGet, Statistics statsRender, Statistics statsAuthn, double msElapsed) {
        System.out.println(String.format("threads: %d, tests: %d, total tests:%d, time elapsed: %.4f", numThreads, numTestsPerThread, numThreads*numTestsPerThread, msElapsed));
        System.out.println(String.format("%5s %10s %10s %10s %10s", "", "Total", "Get", "Render", "Authn"));
        System.out.println(String.format("%5s %10.2f %10.2f %10.2f %10.2f", "min", statsTotal.getMin(), statsGet.getMin(), statsRender.getMin(), statsAuthn.getMin()));
        System.out.println(String.format("%5s %10.2f %10.2f %10.2f %10.2f", "max", statsTotal.getMax(), statsGet.getMax(), statsRender.getMax(), statsAuthn.getMax()));
        System.out.println(String.format("%5s %10.2f %10.2f %10.2f %10.2f", "avg", statsTotal.getAvg(), statsGet.getAvg(), statsRender.getAvg(), statsAuthn.getAvg()));
    }
}
