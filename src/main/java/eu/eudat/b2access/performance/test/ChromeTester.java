package eu.eudat.b2access.performance.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author wilelb
 */
public class ChromeTester extends AbstractTester {
    
    public ChromeTester(String url, String username, String password, String displayName, int numTests) {
        super(url, username, password, displayName, numTests);
    }
    
    @Override
    public WebDriver createDriver() {        
        System.setProperty("webdriver.chrome.silentOutput", this.silent ? "true" : "false");
        
	ChromeOptions options = new ChromeOptions();
        options.setBinary(driverBinaryPath);//"/Applications/Google Chrome Canary.app/Contents/MacOS/Google Chrome Canary");
        if(this.silent) {
            options.addArguments("--log-level=3");
            options.addArguments("--silent");
        }        
        //options.addArguments("--headless");
       
        //DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        //capabilities.setCapability("chrome.verbose", false);
        //capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        //return new ChromeDriver(capabilities);
        return new ChromeDriver(options);
    }
}
