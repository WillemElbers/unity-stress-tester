package eu.eudat.b2access.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        new PerformanceTester(url, username, password, displayName).runNTests(5);
    }
    
    private final String username;
    private final String password;   
    private final String displayName;
    private final String url;
    
    public PerformanceTester(String url, String username, String password, String displayName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        System.setProperty("webdriver.chrome.driver", "/Users/wilelb/Downloads/chromedriver");
    }

    public void runNTests(int n) {
        //Create all testers
        Tester[] testers = new Tester[n];
        for(int i = 0; i < n; i++) {
            testers[i] = new Tester(url, username, password, displayName);
        };
        
        //Start all tester threads
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
        
        //Process results
        Statistics stats = new Statistics();
        for(Tester t : testers) {
            stats.add(t.getStatistics());
        }
        
        System.out.println(stats.toString());
    } 
}
