package eu.eudat.b2access.performance.test;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver
 * @author wilelb
 */
public class ChromeTester extends AbstractTester {
    
    public ChromeTester(String url, String username, String password, String displayName, int numTests) {
        super(url, username, password, displayName, numTests);
    }
    
    @Override
    public WebDriver createDriver() {        
        System.setProperty("webdriver.chrome.silentOutput", this.silent ? "true" : "false");
        System.setProperty("webdriver.chrome.verboseLogging", "true");
        System.setProperty("webdriver.chrome.logfile", "chromedriver.log" );
        
        
	final ChromeOptions options = new ChromeOptions();
        if(driverBinaryPath != null) {
            options.setBinary(driverBinaryPath);
        }
        /*
        if(this.silent) {
            options.addArguments("--log-level=3");
            options.addArguments("--silent");
        } 
        */
        options.addArguments("--log-level=0");
        if(this.headless) {
            options.addArguments("--headless");
        }
        /*
        options.addArguments("--ignore-urlfetcher-cert-requests");
        options.addArguments("--disable-gpu");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-machine-cert-request");
        */
        System.out.println("Chrome driver options: "+options.toString());
        ChromeDriver driver = new ChromeDriver(options);
        driver.setLogLevel(Level.FINE);
        return driver;
    }

    @Override
    public void checkVersionString(String version) throws IllegalStateException {
        Pattern versionRegex = Pattern.compile("Google Chrome (.+)\\.(.+)\\.(.+)\\.(.+)(.*)");
        Matcher versionMatcher = versionRegex.matcher(version);
        if(versionMatcher.matches()) {
            String majorVersion = versionMatcher.group(1);
            String minorVersion = versionMatcher.group(2);
            String revVersion = versionMatcher.group(3);
            
            if(Integer.parseInt(majorVersion) < 59 && headless) {
                throw new IllegalStateException("Chrome version 59+ is required for headless mode. Version "+majorVersion+" found.");
            }
        }
    }
}
