package eu.eudat.b2access.performance;

import eu.eudat.b2access.performance.test.ChromeTester;
import eu.eudat.b2access.performance.test.FirefoxTester;
import eu.eudat.b2access.performance.test.Tester;
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
        String driverPath = "/Users/wilelb/Downloads/chromedriver";
        
        int numThreads = 1;
        int numTestsPerThread = 1;
        
        //Initialize performance tester
        PerformanceTester tester = 
            new PerformanceTester(url, username, password, displayName)
                .setOutputformat(OutputFormat.PRETTY)
                .setThreads(numThreads)
                .setNumTestsPerThreads(numTestsPerThread);
                
        //Customize tester based on command line arguments
        if(args.length > 0) {
            for(String arg : args) {
                String[] keyValuePair = new String[]{arg};
                if(arg.contains("=")) {
                    keyValuePair = arg.split("=");
                }
                
                switch(keyValuePair[0]) {                        
                    case "-t":
                    case "--type":
                        tester.setType(keyValuePair[1]);
                        break;
                    case "-d":
                    case "--driver": 
                        tester.setDriverPath(keyValuePair[1]);
                        break;
                    case "-b":
                    case "--binary":
                        tester.setBinaryPath(keyValuePair[1]); 
                        break;
                    case "-o":
                    case "--output": 
                        tester.setOutputFormat(keyValuePair[1]); 
                        break;
                    case "-v":
                    case "--verbose":
                        tester.setSilent(false);
                        break;
                    case "--headless":
                        tester.setHeadless(true);
                        break;
                    case "-h":
                    case "--help":
                        displayHelp(true);
                        break;
                    default:
                        displayHelp(false);
                        break;
                }                
            }
        }
        
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
        /*
        if(tester.isSilent()) {
            Logger logger = Logger.getLogger("");
            logger.setLevel(Level.WARNING);
            java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
        } else {
            Logger logger = Logger.getLogger("");
            logger.setLevel(Level.FINE);
            java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.FINE);
        }
        */
        //Start the performance tests
        try {
            tester.run();  
        } catch(Exception ex) {
            System.err.println(ex.getMessage());
            displayHelp(false);
        }
    }
    
    private static void displayHelp(boolean ok) {
        System.out.println("Usage: java -jar b2access-performance-1.0-SNAPSHOT-jar-with-dependencies.jar <options>");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("  -b|--binary     Path the google chrome binary.");
        System.out.println("  -d|--driver     Path the google chrome driver.");
        System.out.println("     --headless   Ru in headless mode.");
        System.out.println("  -o|--output     Specify the output format. Supported formats: TSV, PRETTY.");
        System.out.println("  -v|--verbose    Verbose logging.");
        System.out.println("  -t|--type       Driver type. Supported alues: CHROME, FIREFOX.");
        System.out.println("  -h|--help       Display this help.");
        
        if (ok) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
    
    private String username;
    private String password;   
    private final String displayName;
    private String url;
    
    private int numThreads = 1;
    private int numTestsPerThread = 1;
    
    private String driverPath;
    private String binaryPath;
    private boolean silent;
    private OutputFormat format = OutputFormat.TSV;
    private Type type;
    private boolean headless;
    
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
        this.silent = true;
        this.type = Type.CHROME;
        this.headless = false;
    }

    public boolean isSilent() {
        return silent;
    }
    
    public PerformanceTester setType(String type) {
        return setType(Type.valueOf(type));
    }
    
    public PerformanceTester setType(Type type) {
        this.type = type;
        return this;
    }
    
    public PerformanceTester setUrl(String url) {
        this.url = url;
        return this;
    }
    
    public PerformanceTester setUsername(String username) {
        this.username = username;
        return this;
    }
    
    public PerformanceTester setPassword(String password) {
        this.password = password;
        return this;
    }
    
    public PerformanceTester setDriverPath(String path) {
        this.driverPath = path;
        return this;
    }
    
    public PerformanceTester setBinaryPath(String path) {
        this.binaryPath = path;
        return this;
    }
    
    public PerformanceTester setSilent(boolean silent) {
        this.silent = silent;
        return this;
    }
     
    public PerformanceTester setHeadless(boolean headless) {
        this.headless = headless;
        return this;
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
    
    public PerformanceTester setOutputFormat(String format) {
        return this.setOutputformat(OutputFormat.valueOf(format));
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
        if(driverPath == null || driverPath.isEmpty()) {
            throw new IllegalArgumentException("Driver path is required");
        }
        if(binaryPath == null || binaryPath.isEmpty()) {
            throw new IllegalArgumentException("Binary path is required");
        }
        
        System.out.println("Configuration:");
        System.out.println("\tType: "+this.type);
        System.out.println("\tDriver: "+this.driverPath);
        System.out.println("\tBinary: "+this.binaryPath);
        System.out.println("\tHeadless: "+this.headless);
        System.out.println("\tVerbose: "+!this.silent);
        System.out.println(String.format("Running %d threads with %d test(s) per thread, totalling %d tests.", numThreads, numTestsPerThread, numThreads*numTestsPerThread));
        
        switch(type) {
            case CHROME: 
                System.setProperty("webdriver.chrome.driver", driverPath);
                break;
            case FIREFOX:
                System.setProperty("webdriver.gecko.driver", driverPath);
                break;
        }
        
        //Create all testers
        Tester[] testers = new Tester[this.numThreads];
        for(int i = 0; i < this.numThreads; i++) {
            switch(type) {
                case CHROME:
                    testers[i] = new ChromeTester(url, username, password, displayName, this.numTestsPerThread);
                    break;
                case FIREFOX:
                    testers[i] = new FirefoxTester(url, username, password, displayName, this.numTestsPerThread);
                    break;
            }            
            //Customize tester configuration
            if(this.binaryPath != null && !this.binaryPath.isEmpty()) {
                testers[i].setDriverBinaryPath(binaryPath);
            }
            testers[i].setSilent(silent);
            testers[i].setHeadless(headless);
        }
        
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
        //System.out.println(String.format("threads: %d, tests: %d, total tests:%d, time elapsed: %.4f", numThreads, numTestsPerThread, numThreads*numTestsPerThread, msElapsed));
        System.out.println(String.format("%5s %10s %10s %10s %10s", "", "Total", "Get", "Render", "Authn"));
        System.out.println(String.format("%5s %10.2f %10.2f %10.2f %10.2f", "min", statsTotal.getMin(), statsGet.getMin(), statsRender.getMin(), statsAuthn.getMin()));
        System.out.println(String.format("%5s %10.2f %10.2f %10.2f %10.2f", "max", statsTotal.getMax(), statsGet.getMax(), statsRender.getMax(), statsAuthn.getMax()));
        System.out.println(String.format("%5s %10.2f %10.2f %10.2f %10.2f", "avg", statsTotal.getAvg(), statsGet.getAvg(), statsRender.getAvg(), statsAuthn.getAvg()));
    }
}
