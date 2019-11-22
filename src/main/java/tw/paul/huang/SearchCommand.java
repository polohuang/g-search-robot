package tw.paul.huang;


import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


public class SearchCommand {
	private static final String GOOGLE_URL = "https://www.google.com.tw";
	private static final String TARGET_URL = "your-target-url";
	private static final String SEARCH_KEYWORD = "your-search-keyword";
	
	private static final Logger LOGGER = LogManager.getLogger(SearchCommand.class);

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.chrome.driver", "location-of-chrome-driver");
		
		WebDriver driver = new ChromeDriver();
		StopWatch watch;
		for (int i = 0; i < 100; i++) {
			watch = new StopWatch();
			watch.start();
			doSearchAndGetIn(driver);
			Thread.sleep((new Random().nextInt(10) + 10) * 1000);
			watch.stop();
			LOGGER.log(Level.DEBUG, "Operation Time: " + watch.getTime(TimeUnit.SECONDS));
		}
	}

	private static void doSearchAndGetIn(WebDriver driver) {
		driver.get(GOOGLE_URL);
		WebElement inputElem = driver.findElement(By.xpath("//input[@class='gLFyf gsfi']"));
		inputElem.sendKeys(SEARCH_KEYWORD);
		WebElement searchElem = driver.findElement(By.xpath("//input[@class='gNO89b']"));
		searchElem.submit();

		boolean isFound = false;
		List<WebElement> linkElems;
		WebElement npElem;
		while (!isFound) {
			linkElems = driver.findElements(By.xpath("//div[@id='rso']//div[@class='rc']/div[@class='r']/a"));
			List<WebElement> matchedElems = linkElems.parallelStream()
													  .filter(e -> TARGET_URL.equals(e.getAttribute("href")))
													  .collect(Collectors.toList());
			if (matchedElems.isEmpty()) {
				npElem = driver.findElement(By.xpath("//a[@id='pnnext']"));
				npElem.click();
				continue;
			}
			
			matchedElems.get(0).click();
			isFound = true;
		}
	}

}
