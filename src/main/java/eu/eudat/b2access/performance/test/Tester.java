package eu.eudat.b2access.performance.test;

import eu.eudat.b2access.performance.Statistic;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author wilelb
 */
public interface Tester extends Runnable {
    public AbstractTester setDriverBinaryPath(String path);
    public AbstractTester setSilent(boolean silent);
    public AbstractTester setHeadless(boolean headless);
    
    public WebDriver createDriver();    
    public List<Map<String, Statistic>> getStatistics();
}
