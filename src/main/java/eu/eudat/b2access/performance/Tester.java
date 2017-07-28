package eu.eudat.b2access.performance;

import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
    private final Map<String, Statistic> statistics;
    
    public Tester(String url, String username, String password, String displayName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        
        statistics = new HashMap<>();
        statistics.put("total", new Statistic("total"));
        statistics.put("get", new Statistic("get"));
        statistics.put("render", new Statistic("render"));
        statistics.put("authn", new Statistic("authn"));
    }
    
    protected WebDriver createDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("chrome.verbose", false);
        return new ChromeDriver(capabilities);
    }
    
    @Override
    public void run() {
        WebDriver driver = createDriver();
        try {          
            getStatistics().get("total").start();
            getStatistics().get("get").start();
            driver.get(this.url);
            getStatistics().get("get").stop();

            getStatistics().get("render").start();
            (new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver d) {
                    WebElement elementUsername = driver.findElement(By.id("AuthenticationUI.username"));
                    return elementUsername != null;
                }
            });
            getStatistics().get("render").stop();

            WebElement elementUsername = driver.findElement(By.id("AuthenticationUI.username"));
            WebElement elementPassword = driver.findElement(By.id("WebPasswordRetrieval.password"));
            WebElement elementAuthenticateButton = driver.findElement(By.id("AuthenticationUI.authnenticateButton"));
            elementUsername.sendKeys(username);
            elementPassword.sendKeys(password);          
            elementAuthenticateButton.click();

            getStatistics().get("authn").start();
            (new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver d) {
                    WebElement element = d.findElement(By.id("gwt-uid-3"));
                    return element.getText().equalsIgnoreCase(displayName);
                }
            });
            getStatistics().get("authn").stop();
            getStatistics().get("total").stop();
        } catch(Exception ex) {
            System.out.println("Error: "+ex.getMessage());
            System.out.println("Source:");
            System.out.println(driver.getPageSource());
        } finally {
            if(driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * @return the statistics
     */
    public Map<String, Statistic> getStatistics() {
        return statistics;
    }
}
