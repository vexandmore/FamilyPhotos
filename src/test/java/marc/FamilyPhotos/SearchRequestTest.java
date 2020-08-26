package marc.FamilyPhotos;

import marc.FamilyPhotos.testClasses.SearchHttpRequest;
import java.sql.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import marc.FamilyPhotos.util.*;

/**
 *
 * @author Marc
 */
public class SearchRequestTest {
	private static Connection con;
	
	public SearchRequestTest() {
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

	/**
	 * Test of buildQuery method of class SearchRequest.
	 * @throws java.lang.Exception
	 */
	@Test
	public void testBuildQuery() throws Exception {
		System.out.println("buildQuery");
		Map<String, String[]> queryMap = new HashMap<>();
		//test case 1
		queryMap.put("tags", new String[]{"Laurie", "Nan"});
		queryMap.put("tagsBoolean", new String[]{"and"});
		SearchHttpRequest shr = new SearchHttpRequest(queryMap, Arrays.asList("family"), "");
		SearchRequest instance = new SearchRequest(shr);
		
		try (ResultSet result = instance.buildQuery(con).executeQuery();) {
			result.next();
			assertEquals(result.getString(2), "d6b58ff3-b944-11ea-9383-0026833c1a98");
			result.next();
			assertEquals(result.getString(1), "images/thumbnails/Box/imgICE074.jpg");
			result.last();
			assertEquals(result.getString(1), "images/thumbnails/Box/imgICE051DR.jpg");
		}
		
		//test case 2
		queryMap.clear();
		queryMap.put("tags", new String[]{"Grace","Family","Laurie"});
		queryMap.put("tagsBoolean", new String[]{"and"});
		queryMap.put("decades", new String[]{"1960s"});
		shr = new SearchHttpRequest(queryMap, Arrays.asList("family"), "");
		instance = new SearchRequest(shr);
		try (ResultSet result = instance.buildQuery(con).executeQuery()) {
			result.next();
			assertEquals(result.getString(2), "d6b6a167-b944-11ea-9383-0026833c1a98");
			result.next();
			assertEquals(result.getString(1), "images/thumbnails/Box/imgICE082.jpg");
			result.last();
			assertEquals(result.getString(1), "images/thumbnails/Loose containers/C2/imgICE020.jpg");
		}
	}

	/**
	 * Test the getTrimmedQueryString method of SearchRequest
	 * @throws Exception
	 */
	@Test
	public void testGetTrimmedQueryString() throws Exception {
		System.out.println("getTrimmedQueryString");
		
		Map<String, String[]> queryMap = new HashMap<>();
		SearchHttpRequest shr = new SearchHttpRequest(queryMap, Arrays.asList("family"), "tags=Laurie&tags=Family&tagsBoolean=or&showImage=first&showPageNum=1");
		SearchRequest instance = new SearchRequest(shr);
		String expResult = "?tags=Laurie&tags=Family&tagsBoolean=or";
		assertEquals(instance.getTrimmedQueryString(), expResult);
		
		shr = new SearchHttpRequest(queryMap, Arrays.asList("family"),"tags=Laurie&showImage=asas&tags=Family&tagsBoolean=or&showImage=first&showPageNum=100");
		instance = new SearchRequest(shr);
		expResult = "?tags=Laurie&tags=Family&tagsBoolean=or";
		assertEquals(instance.getTrimmedQueryString(), expResult);
	}
}
