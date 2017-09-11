package eu.eudat.b2access.performance.test;

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 *
 * @author wilelb
 */
public class FirefoxTester extends AbstractTester {
    
    public FirefoxTester(String url, String username, String password, String displayName, int numTests) {
        super(url, username, password, displayName, numTests);
    }
    
    @Override
    public WebDriver createDriver() {
        File binaryPath = new File(driverBinaryPath);
        FirefoxBinary firefoxBinary = new FirefoxBinary(binaryPath);
        firefoxBinary.addCommandLineOptions("--headless");
        return new FirefoxDriver(firefoxBinary, new FirefoxProfile());
    }
}
