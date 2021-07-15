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
		//Test case 1
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("Dogs");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg");
		//test case 2
		HttpRequest = makeRequestForTextSearch("Dogs or Circuits");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg",
				"images/thumbnails/folder2/umberto-jXd2FSvcRr8-unsplash.jpg");
	}
	
	@Test
	public void testCollectionSearch() {
		System.out.println("Test search for collections");
		
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("vertical or circuit");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder2/umberto-jXd2FSvcRr8-unsplash.jpg",
				"images/thumbnails/folder2/sorasak-KxCJXXGsv9I-unsplash.jpg",
				"images/thumbnails/folder1/laura-college-K_Na5gCmh38-unsplash.jpg");
	}
	
	@Test
	public void testCollectionAndTagSearch() {
		System.out.println("Test search for collection and tag");
		
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("vertical and animal");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder1/laura-college-K_Na5gCmh38-unsplash.jpg");
	}
	
	@Test
	public void testISODateSearch() {
		System.out.println("Test search for date");
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("1975-01-01");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder1/lily-banse--YHSwy6uqvk-unsplash.jpg");
	}
	
	@Test
	public void testOtherDateFormats() {
		System.out.println("Test other date formats");
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("01/01/1975");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder1/lily-banse--YHSwy6uqvk-unsplash.jpg");
		HttpRequest = makeRequestForTextSearch("1975/01/01");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder1/lily-banse--YHSwy6uqvk-unsplash.jpg");
	}
	
	@Test
	public void testDecadeSearch() {
		System.out.println("Test decade search");
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("1980s");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder1/laura-college-K_Na5gCmh38-unsplash.jpg");
	}
	
	//test all search types together
	@Test
	public void testTagDecadeCollection() {
		System.out.println("Test search of tags, decades, and collections");
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("1980s and animals or vertical or 1975-01-01");
		assertResultContains(HttpRequest,
				"images/thumbnails/folder1/lily-banse--YHSwy6uqvk-unsplash.jpg",
				"images/thumbnails/folder2/sorasak-KxCJXXGsv9I-unsplash.jpg",
				"images/thumbnails/folder1/laura-college-K_Na5gCmh38-unsplash.jpg");
	}
	
	/**
	 * Tests that the given request will, when fed to TextSearchRequest, provide
	 * the given paths as results.
	 * @param request Request to pass along to TextSearchRequest.
	 * @param expPathResults Expexted results.
	 */
	private void assertResultContains(SearchHttpRequest request, String... expPathResults) {
		try {
			TextSearchRequest instance = new TextSearchRequest(request, con);
			try (ResultSet result = instance.buildQuery(con).executeQuery();) {
				List<String> resultPaths = new ArrayList<>();
				while(result.next()) {
					resultPaths.add(result.getString(1));
				}
				assertEquals(expPathResults.length, resultPaths.size());
				for (String expPath: expPathResults) {
					assertTrue(resultPaths.contains(expPath));
				}
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
