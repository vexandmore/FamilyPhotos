package marc.FamilyPhotos.Search;

import jakarta.servlet.ServletException;
import java.sql.*;
import java.util.*;
import marc.FamilyPhotos.mockClasses.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Marc
 */
public class TextSearchRequestTest {
	private static Connection con;
	
	public TextSearchRequestTest() {
	}
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		TestConfig config = TestConfig.getConfig();
		Credential dbCred = config.getTestDatabaseCredential();
		con = DriverManager.getConnection(config.getTestDatabaseURL(), dbCred.getUsername(), dbCred.getPassword());
	}
	
	@AfterClass
	public static void tearDownClass() throws SQLException {
		con.close();
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void testBuildQuery() {
		System.out.println("Test text query");
		
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("Dogs");
		//test case 1
		try {
			TextSearchRequest instance = new TextSearchRequest(HttpRequest, con);
			try (ResultSet result = instance.buildQuery(con).executeQuery();) {
				result.next();
				assertEquals("images/thumbnails/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg", result.getString(1));
				assertFalse(result.next());
			}
		} catch (InvalidRequest | ServletException | SQLException e) {
			fail(e.getMessage());
		}
		//test case 2
		HttpRequest = makeRequestForTextSearch("Dogs or Circuits");
		try {
			TextSearchRequest instance = new TextSearchRequest(HttpRequest, con);
			try (ResultSet result = instance.buildQuery(con).executeQuery();) {
				List<String> resultPaths = new ArrayList<>();
				while(result.next()) {
					resultPaths.add(result.getString(1));
				}
				assertTrue(resultPaths.contains("images/thumbnails/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg"));
				assertTrue(resultPaths.contains("images/thumbnails/folder2/umberto-jXd2FSvcRr8-unsplash.jpg"));
				assertEquals(2, resultPaths.size());
			}
		} catch (InvalidRequest | ServletException | SQLException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void doFuzzingTest() {
		System.out.println("Fuzzing test");
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		
		for (int i = 0; i < 10; i++) {
			String query = makeRandomString(rand, 128);
			SearchHttpRequest HttpRequest = makeRequestForTextSearch(query);
			try {
				TextSearchRequest request = new TextSearchRequest(HttpRequest, con);
				assertTrue("Expect that random string: " + query + 
						" generates warning message on parse. seed: " + seed, 
						request.getWarningMessage().isPresent());
			} catch (Exception e) {
				fail("Expected "  + HttpRequest + " would not cause another exception. Seed " + seed);
			}
			
		}
	}
	
	
	private static final char[] RANDOM_CHARACTERS = "abcdefghijklmnopqrstuvwxyz   0123456789".toCharArray();
	
	private String makeRandomString(Random rand, int length) {
		StringBuilder builder = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomNumber = (int)(rand.nextDouble() * RANDOM_CHARACTERS.length);
			builder.append(RANDOM_CHARACTERS[randomNumber]);
		}
		return builder.toString();
	}
	
	private SearchHttpRequest makeRequestForTextSearch(String query) {
		Map<String, String[]> queryMap = new HashMap<>();
		queryMap.put("simpleSearchQuery", new String[]{query});
		SearchHttpRequest shr = new SearchHttpRequest(queryMap, Arrays.asList("family"), "");
		return shr;
	}
}
