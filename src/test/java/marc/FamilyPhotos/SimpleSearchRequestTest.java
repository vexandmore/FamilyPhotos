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
public class SimpleSearchRequestTest {
	private static Connection con;
	
	public SimpleSearchRequestTest() {
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
		Map<String, String[]> queryMap = new HashMap<>();
		queryMap.put("simpleSearchQuery", new String[]{"Quebec City"});
		SearchHttpRequest shr = new SearchHttpRequest(queryMap, Arrays.asList("family"), "");
		//test case 1
		try {
			TextSearchRequest instance = new TextSearchRequest(shr, con);
			try (ResultSet result = instance.buildQuery(con).executeQuery();) {
				result.next();
				assertEquals(result.getString(1), "images/thumbnails/mixed nfld & Quebec/imgICE055.jpg");
				result.next();
				assertEquals(result.getString(1), "images/thumbnails/Box/imgICE026.jpg");
				result.last();
				assertEquals(result.getString(1), "images/thumbnails/Family&Friends 1971&1973 (early)/C11/imgICE003.jpg");
			}
		} catch (InvalidRequest | ServletException | SQLException e) {
			fail();
		}
		//test case 2
		queryMap.put("simpleSearchQuery", new String[]{"Laurie or Carol"});
		try {
			TextSearchRequest instance = new TextSearchRequest(shr, con);
			try (ResultSet result = instance.buildQuery(con).executeQuery();) {
				result.next();
				assertEquals(result.getString(1), "images/thumbnails/Box/imgICE067.jpg");
				result.next();
				assertEquals(result.getString(1), "images/thumbnails/Box/imgICE069.jpg");
				result.last();
				assertEquals(result.getString(1), "images/thumbnails/Family&Friends 1971&1973 (early)/C11/imgICE020.jpg");
			}
		} catch (InvalidRequest | ServletException | SQLException e) {
			fail();
		}
	}
}
