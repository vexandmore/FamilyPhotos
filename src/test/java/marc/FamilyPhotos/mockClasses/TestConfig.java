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
	
	private String testURL;
	private Credential viewCredential, limitedCredential;
	private String chromeWebDriver;

	public String getChromeWebDriver() {
		return chromeWebDriver;
	}
	
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
