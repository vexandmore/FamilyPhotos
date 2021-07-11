package marc.FamilyPhotos;

import marc.FamilyPhotos.mockClasses.*;
import static marc.FamilyPhotos.util.Utils.anyEmptyString;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;

/**
 * Test login as well as the limited user filter.
 * @author Marc
 */
public class LoginAndFilterIT {
	private static TestConfig config;
	private static WebDriver driver;
	
	private static final ExpectedCondition<Boolean> foundNoResults = 
			pDriver -> pDriver.getTitle().contains("No result");
	private static final ExpectedCondition<Boolean> foundSomeResults = 
			pDriver -> pDriver.getTitle().contains("Images page");
	private static final ExpectedCondition<Boolean> atSearchPage = 
			pDriver -> pDriver.getTitle().contains("Search");
	private static final ExpectedCondition<Boolean> imageLoaded = 
			pDriver -> pDriver.findElements(By.tagName("img")).size() > 0;
	

	@BeforeClass
	public static void setup() throws Exception {
		config = TestConfig.getConfig();
		if (anyEmptyString(config.getChromeWebDriver())) {
			System.setProperty("webdriver.gecko.driver", config.getGeckoWebDriver());
			driver = new FirefoxDriver();
		} else {
			System.setProperty("webdriver.chrome.driver", config.getChromeWebDriver());
			driver = new ChromeDriver();
		}
		driver.get(config.getTestURL());
	}
	
	@Test
	public void testBothLogins() throws Exception {
		logoutIfLoggedIn(driver);
		
		login(driver, config.getViewCredential());
		(new WebDriverWait(driver, 10)).until(atSearchPage);
		
		login(driver, config.getLimitedCredential());
		(new WebDriverWait(driver, 10)).until(atSearchPage);
	}
	
	@Test
	public void testLimitedSearch() {
		logoutIfLoggedIn(driver);
		login(driver, config.getLimitedCredential());
		
		textSearch(driver, "Dogs");
		(new WebDriverWait(driver, 10)).until(foundNoResults);
		textSearch(driver, "Building");
		(new WebDriverWait(driver, 10)).until(foundSomeResults);
	}
	
	@Test
	public void testLimitedFilter() {
		logoutIfLoggedIn(driver);
		
		login(driver, config.getViewCredential());
		textSearch(driver, "Animals");
		(new WebDriverWait(driver, 10)).until(foundSomeResults);
		String restrictedImageURL = driver.findElement(By.className("thumbnail")).getAttribute("src");
		driver.get(restrictedImageURL);
		(new WebDriverWait(driver, 10)).until(imageLoaded);
		
		login(driver, config.getLimitedCredential());
		driver.get(restrictedImageURL);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.not(imageLoaded));
		(new WebDriverWait(driver, 1)).until((drv) -> 
				drv.findElement(By.tagName("h1")).getText().contains("Error 403"));
	}
	
	private static void login(WebDriver driver, Credential credential) {
		logoutIfLoggedIn(driver);
		driver.findElement(By.id("username")).sendKeys(credential.getUsername());
		driver.findElement(By.id("password")).sendKeys(credential.getPassword());
		driver.findElement(By.cssSelector("input[type=submit]")).click();
	}
	
	private static void textSearch(WebDriver driver, String query) {
		if (!driver.getCurrentUrl().equals(config.getTestURL())) {
			driver.navigate().to(config.getTestURL());
		}
		driver.findElement(By.id("searchQuery")).sendKeys(query);
		driver.findElement(By.cssSelector("input[type=submit]")).click();
	}
	
	/**
	 * Logs out if already logged in. In either case, navigates back to the
	 * login page.
	 */
	private static void logoutIfLoggedIn(WebDriver driver) {
		if (loggedIn(driver)) {
			WebElement logoutLink = driver.findElement(By.id("navLogout"));
			if (!logoutLink.isDisplayed()) {
				driver.findElement(By.className("nav-toggle-label")).click();
			}
			new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOf(logoutLink));
			logoutLink.click();
		}
		driver.get(config.getTestURL());
	}
	
	/**
	 * Tests if currently logged in. Navigates away from current page if no nav
	 * is present.
	 */
	private static boolean loggedIn(WebDriver driver) {
		if (driver.findElements(By.id("nav")).isEmpty()) {
			driver.navigate().to(config.getTestURL());
		}
		return driver.findElements(By.id("navLogout")).size() > 0;
	}
	
	@AfterClass
	public static void tearDown() {
		driver.quit();
	}
	
}
