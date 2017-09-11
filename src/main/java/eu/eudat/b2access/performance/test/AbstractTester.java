package eu.eudat.b2access.performance.test;

import eu.eudat.b2access.performance.Statistic;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author wilelb
 */
public abstract class AbstractTester implements Tester, Runnable {
    
    protected final String url;
    protected final String username;
    protected final String password;   
    protected final String displayName;
    protected final List<Map<String, Statistic>> statistics = new ArrayList<>(); 
    protected int numTests = 1;
    protected String driverBinaryPath;
    protected boolean silent;
    
    public AbstractTester(String url, String username, String password, String displayName, int numTests) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.numTests = numTests;  
        this.silent = true;
    }
    
    @Override
    public AbstractTester setDriverBinaryPath(String path) {
        if(!Files.exists(Paths.get(path))) { 
            throw new IllegalArgumentException("Selenium driver binary not found: "+path);
        }
        this.driverBinaryPath = path;
        return this;
    }
    
    @Override
    public AbstractTester setSilent(boolean silent) {
        this.silent = silent;
        return this;
    }
    
    /**
     * @return the statistics
     */
    @Override
    public List<Map<String, Statistic>> getStatistics() {
        return statistics;
    }
    
    @Override
    public abstract WebDriver createDriver();
    
    @Override
    public void run() {
        for(int i = 0; i < numTests; i++) {
            Map<String, Statistic> stats = new HashMap<>();
            stats.put("total", new Statistic("total"));
            stats.put("get", new Statistic("get"));
            stats.put("render", new Statistic("render"));
            stats.put("authn", new Statistic("authn"));
        
            WebDriver driver = createDriver();
            try {          
                    stats.get("total").start();
                    stats.get("get").start();
                    driver.get(this.url);
                    stats.get("get").stop();

                    stats.get("render").start();
                    (new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
                        @Override
                        public Boolean apply(WebDriver d) {
                            WebElement elementUsername = driver.findElement(By.id("AuthenticationUI.username"));
                            return elementUsername != null;
                        }
                    });
                    stats.get("render").stop();

                    WebElement elementUsername = driver.findElement(By.id("AuthenticationUI.username"));
                    WebElement elementPassword = driver.findElement(By.id("WebPasswordRetrieval.password"));
                    WebElement elementAuthenticateButton = driver.findElement(By.id("AuthenticationUI.authnenticateButton"));
                    elementUsername.sendKeys(username);
                    elementPassword.sendKeys(password);          
                    elementAuthenticateButton.click();

                    stats.get("authn").start();
                    (new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
                        @Override
                        public Boolean apply(WebDriver d) {
                            WebElement element = d.findElement(By.id("gwt-uid-3"));
                            return element.getText().equalsIgnoreCase(displayName);
                        }
                    });
                    stats.get("authn").stop();
                    stats.get("total").stop();
                    
                    this.statistics.add(stats);

            } catch(Exception ex) {
                System.out.println("Error: \n"+ex.getMessage());
                System.out.println("Source: \n");
                System.out.println(driver.getPageSource());
            } finally {
                if(driver != null) {
                    driver.quit();
                }
            }
        }
    }
}
