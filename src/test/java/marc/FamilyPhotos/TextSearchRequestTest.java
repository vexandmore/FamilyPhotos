package marc.FamilyPhotos;

import jakarta.servlet.ServletException;
import java.sql.*;
import java.util.*;
import marc.FamilyPhotos.testClasses.SearchHttpRequest;
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
	public static void setUpClass() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/photostest", "root", "clevercombeliottwin");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@AfterClass
	public static void tearDownClass() {
		try {
			con.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
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
		
		SearchHttpRequest HttpRequest = makeRequestForTextSearch("Quebec City");
		//test case 1
		try {
			TextSearchRequest instance = new TextSearchRequest(HttpRequest, con);
			try (ResultSet result = instance.buildQuery(con).executeQuery();) {
				result.next();
				assertEquals(result.getString(1), "images/thumbnails/mixed nfld & Quebec/imgICE055.jpg");
				result.next();
				assertEquals(result.getString(1), "images/thumbnails/Box/imgICE026.jpg");
				result.last();
				assertEquals(result.getString(1), "images/thumbnails/Family&Friends 1971&1973 (early)/C11/imgICE003.jpg");
			}
		} catch (InvalidRequest | ServletException | SQLException e) {
			fail(e.getMessage());
		}
		//test case 2
		HttpRequest = makeRequestForTextSearch("Laurie or Carol");
		try {
			TextSearchRequest instance = new TextSearchRequest(HttpRequest, con);
			try (ResultSet result = instance.buildQuery(con).executeQuery();) {
				result.next();
				assertEquals(result.getString(1), "images/thumbnails/Box/imgICE067.jpg");
				result.next();
				assertEquals(result.getString(1), "images/thumbnails/Box/imgICE069.jpg");
				result.last();
				assertEquals(result.getString(1), "images/thumbnails/Family&Friends 1971&1973 (early)/C11/imgICE020.jpg");
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
