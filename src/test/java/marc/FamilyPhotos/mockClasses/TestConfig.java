package marc.FamilyPhotos.mockClasses;

import com.google.gson.*;
import java.nio.file.*;
import java.util.stream.Collectors;

/**
 * Provides configuration for tests.
 * @author Marc
 */
public class TestConfig {
	private static TestConfig config;
	
	private String testURL, testDatabaseURL;
	private Credential viewCredential, limitedCredential, testDatabaseCredential;
	private String chromeWebDriver, geckoWebDriver;
	
	/**
	 * @return The test configuration for the website.
	 * @throws Exception if anything goes wrong reading or parsing.
	 */
	public static TestConfig getConfig() throws Exception {
		if (config == null) {
			config = readConfig();
		}
		return config;
	}
	
	private static TestConfig readConfig() throws Exception {
		Path configPath = Paths.get("./src/test/TestConfig.json");
		String json = Files.lines(configPath).collect(Collectors.joining());
		TestConfig newConfig = new Gson().fromJson(json, TestConfig.class);
		return newConfig;
	}
	
	public String getChromeWebDriver() {
		return chromeWebDriver;
	}

	public String getGeckoWebDriver() {
		return geckoWebDriver;
	}
	
	public String getTestDatabaseURL() {
		return testDatabaseURL;
	}

	public Credential getTestDatabaseCredential() {
		return testDatabaseCredential;
	}
	
	public String getTestURL() {
		return testURL;
	}

	public Credential getViewCredential() {
		return viewCredential;
	}

	public Credential getLimitedCredential() {
		return limitedCredential;
	}
}
