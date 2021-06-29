/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marc.FamilyPhotos.util;

import marc.FamilyPhotos.mockClasses.MockDataSource;
import java.sql.*;
import java.util.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marc
 */
public class UtilsTest {
	private static MockDataSource source;
	public UtilsTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
		try {
			source = new MockDataSource(
					"jdbc:mysql://localhost:3306/photostest", "root", 
					"clevercombeliottwin");
		} catch (SQLException e) {
			fail("could not get db connection");
		}
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println("Post UtilsTest");
		try {
			source.closeConnection();
		} catch (SQLException e) {
			fail("could not close connection");
		}
	}

	/**
	 * Test of getCollections method, of class Utils.
	 */
	@Test
	public void testGetCollections() throws Exception {
		System.out.println("getCollections");
		Collection<SlideCollection> result = Utils.getCollections(source);
		assertTrue(result.contains(new SlideCollection("Delete test", 2)));
		assertTrue(result.contains(new SlideCollection("New photos", 172)));
		assertTrue(result.contains(new SlideCollection("test", 6)));
		assertTrue(result.size() == 3);
	}


	@Test
	public void testGetLimitedCollections() throws Exception {
		System.out.println("getLimitedCollections");
		Collection<SlideCollection> limitedCollections = Utils.getLimitedCollections(source);
		assertTrue(limitedCollections.contains(new SlideCollection("Delete test", 2)));
		assertTrue(limitedCollections.contains(new SlideCollection("New photos", 172)));
		assertFalse(limitedCollections.contains(new SlideCollection("test", 6)));
		assertTrue(limitedCollections.size() == 2);
	}

	/**
	 * Test of getPaths method, of class Utils.
	 */
	@Test
	public void testGetPaths() throws Exception {
		System.out.println("getPaths");
		SortedSet<String> paths = Utils.getPaths(source);
		assertTrue(paths.contains("2016 neg_scan_cormack_1"));
		assertTrue(paths.contains("Loose Carousels/C3"));
		assertTrue(paths.contains("Loose Carousels/C1"));
		assertTrue(paths.contains("Loose containers/C1"));
		assertTrue(paths.contains("Family&Friends 1971&1973 (early)/Wildlife"));
		assertTrue(paths.contains("Family&Friends 1973 (includes 1973 Barrens-Tenting Trip)"));
		assertFalse(paths.contains("obviously not a path"));
	}

	/**
	 * Test of getTags method, of class Utils.
	 */
	@Test
	public void testGetTags() throws Exception {
		System.out.println("getTags");
		TagSet result = Utils.getTags(source);
		assertTrue(result.numberTags() == 76);
		assertTrue(result.containsTag(new Tag("64", "64")));
		assertTrue(result.containsTag(new Tag("Equipment", "Logging machinery")));
	}

	/**
	 * Test of getTagwhitelist method, of class Utils.
	 */
	@Test
	public void testGetTagwhitelist_DataSource() throws Exception {
		System.out.println("getTagwhitelist");
		TagSet expResult = new TagSet();
		expResult.addTag("Logging", "Logging", "Logging");
		expResult.addTag("Logging", "Equipment", "Logging machinery");
		TagSet result = Utils.getTagwhitelist(source);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getAllTags method, of class Cache.
	 */
	@Test
	public void testGetAllTags() throws Exception {
		System.out.println("getAllTags");
		ArrayList<String> result = Utils.getAllTags(source);
		assertTrue(result.contains("Equipment"));
		assertTrue(result.contains("lake"));
		assertTrue(result.contains("Fix"));
		assertTrue(result.contains("64"));
		assertTrue(result.contains("1960s"));
		assertTrue(result.contains("1970s"));
		assertTrue(result.contains("1980s"));
		assertTrue(result.contains("1950s"));
		assertTrue(result.size() == 80);
	}
}
