package eu.eudat.b2access.performance.test;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import sun.util.logging.PlatformLogger;

/**
 * https://github.com/SeleniumHQ/selenium/wiki/FirefoxDriver
 * @author wilelb
 */
public class FirefoxTester extends AbstractTester {
    
    public FirefoxTester(String url, String username, String password, String displayName, int numTests) {
        super(url, username, password, displayName, numTests);
    }
    
    @Override
    public WebDriver createDriver() {
        System.setProperty("webdriver.firefox.logfile", "firefoxdriver.log");
        
        if(driverBinaryPath != null) {
            System.setProperty("webdriver.firefox.bin", driverBinaryPath);
        }
        
        checkVersion();
                
        final FirefoxOptions options = new FirefoxOptions();
        options.setProfile(new FirefoxProfile());
        /*
        if(this.silent) {
            options.setLogLevel(Level.OFF);
        }
        */
        if(this.headless) {
            options.addArguments("--headless");
        }             
        
        
        System.out.println("Firefox driver options: " + options.toString());
        return new FirefoxDriver(options);
    }

    @Override
    public void checkVersionString(String version) throws IllegalStateException {
        Pattern versionRegex = Pattern.compile("Mozilla Firefox (.+)\\.(.+)\\.(.+)(.*)");
        Matcher versionMatcher = versionRegex.matcher(version);
        if(versionMatcher.matches()) {
            String majorVersion = versionMatcher.group(1);
            String minorVersion = versionMatcher.group(2);
            String revVersion = versionMatcher.group(3);
            //System.out.println(String.format("Major: %s, Minor: %s, Patch: %s", majorVersion, minorVersion, revVersion));
            
            if(Integer.parseInt(majorVersion) < 56 && headless) {
                throw new IllegalStateException("Firefox version 56+ is required for headless mode. Version "+majorVersion+" found.");
            }
        }
    }
}
