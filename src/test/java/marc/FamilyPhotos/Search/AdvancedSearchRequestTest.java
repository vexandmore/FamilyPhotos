package marc.FamilyPhotos.Search;

import marc.FamilyPhotos.mockClasses.SearchHttpRequest;
import java.sql.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import marc.FamilyPhotos.Search.*;
import marc.FamilyPhotos.mockClasses.Credential;
import marc.FamilyPhotos.mockClasses.TestConfig;

/**
 *
 * @author Marc
 */
public class AdvancedSearchRequestTest {
	private static Connection con;
	
	public AdvancedSearchRequestTest() {
	}
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		TestConfig config = TestConfig.getConfig();
		Credential dbCred = config.getTestDatabaseCredential();
		con = DriverManager.getConnection(config.getTestDatabaseURL(), dbCred.getUsername(), dbCred.getPassword());
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

	/**
	 * Test of buildQuery method of class AdvancedSearchRequest.
	 * @throws java.lang.Exception
	 */
	@Test
	public void testBuildQuery() throws Exception {
		System.out.println("buildQuery");
		Map<String, String[]> queryMap = new HashMap<>();
		//test case 1
		queryMap.put("tags", new String[]{"Animals", "Dogs"});
		queryMap.put("tagsBoolean", new String[]{"and"});
		SearchHttpRequest shr = new SearchHttpRequest(queryMap, Arrays.asList("family"), "");
		AdvancedSearchRequest instance = new AdvancedSearchRequest(shr);
		
		try (ResultSet result = instance.buildQuery(con).executeQuery();) {
			result.next();
			assertEquals(result.getString(1), "images/thumbnails/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg");
			assertFalse(result.next());
		}
		
		//test case 2
		queryMap.clear();
		queryMap.put("tags", new String[]{"Buildings"});
		queryMap.put("tagsBoolean", new String[]{"and"});
		queryMap.put("decades", new String[]{"1960s"});
		shr = new SearchHttpRequest(queryMap, Arrays.asList("family"), "");
		instance = new AdvancedSearchRequest(shr);
		try (ResultSet result = instance.buildQuery(con).executeQuery()) {
			result.next();
			assertEquals(result.getString(1), "images/thumbnails/folder2/sorasak-KxCJXXGsv9I-unsplash.jpg");
			assertFalse(result.next());
		}
	}

	/**
	 * Test the getTrimmedQueryString method of AdvancedSearchRequest
	 * @throws Exception
	 */
	@Test
	public void testGetTrimmedQueryString() throws Exception {
		System.out.println("getTrimmedQueryString");
		
		Map<String, String[]> queryMap = new HashMap<>();
		SearchHttpRequest shr = new SearchHttpRequest(queryMap, Arrays.asList("family"), 
				"tags=Animals&tags=Buildings&tagsBoolean=or&showImage=first&showPageNum=1");
		AdvancedSearchRequest instance = new AdvancedSearchRequest(shr);
		String expResult = "?tags=Animals&tags=Buildings&tagsBoolean=or";
		assertEquals(instance.getTrimmedQueryString(), expResult);
		
		shr = new SearchHttpRequest(queryMap, Arrays.asList("family"),
				"tags=Animals&showImage=asas&tags=Dogs&tagsBoolean=or&showImage=first&showPageNum=100");
		instance = new AdvancedSearchRequest(shr);
		expResult = "?tags=Animals&tags=Dogs&tagsBoolean=or";
		assertEquals(instance.getTrimmedQueryString(), expResult);
	}
}
