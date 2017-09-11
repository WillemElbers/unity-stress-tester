package eu.eudat.b2access.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author wilelb
 */
public class Tester implements Runnable {
    
    private final String url;
    private final String username;
    private final String password;   
    private final String displayName;
    private final List<Map<String, Statistic>> statistics = new ArrayList<>(); 
    private int numTests = 1;
            
    public Tester(String url, String username, String password, String displayName, int numTests) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.numTests = numTests;  
    }
    
    protected WebDriver createDriver() {
        //System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty("webdriver.chrome.silentOutput", "true");
   
	ChromeOptions options = new ChromeOptions();
        options.setBinary("/Applications/Google Chrome Canary.app/Contents/MacOS/Google Chrome Canary");
        options.addArguments("--log-level=3");
        options.addArguments("--silent");
        options.addArguments("--headless");
       
        //DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        //capabilities.setCapability("chrome.verbose", false);
        //capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        //return new ChromeDriver(capabilities);
        return new ChromeDriver(options);
    }
    
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

    /**
     * @return the statistics
     */
    public List<Map<String, Statistic>> getStatistics() {
        return statistics;
    }
}
