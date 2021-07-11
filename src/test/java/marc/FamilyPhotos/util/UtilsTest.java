/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marc.FamilyPhotos.util;

import marc.FamilyPhotos.mockClasses.MockDataSource;
import java.sql.*;
import java.util.*;
import marc.FamilyPhotos.mockClasses.Credential;
import marc.FamilyPhotos.mockClasses.TestConfig;
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
	public static void setUpClass() throws Exception {
		TestConfig config = TestConfig.getConfig();
		Credential dbCred = config.getTestDatabaseCredential();
		source = new MockDataSource(config.getTestDatabaseURL(),
				dbCred.getUsername(), dbCred.getPassword());
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
	
	@Test
	public void testGetCollections() throws Exception {
		System.out.println("getCollections");
		Collection<SlideCollection> result = Utils.getCollections(source);
		assertTrue(result.contains(new SlideCollection("vertical", 2)));
		assertTrue(result.contains(new SlideCollection("circuit", 1)));
		assertEquals(2, result.size());
	}


	@Test
	public void testGetLimitedCollections() throws Exception {
		System.out.println("getLimitedCollections");
		Collection<SlideCollection> limitedCollections = Utils.getLimitedCollections(source);
		assertTrue(limitedCollections.contains(new SlideCollection("vertical", 2)));
		assertFalse(limitedCollections.contains(new SlideCollection("circuit", 1)));
		assertEquals(1, limitedCollections.size());
	}
	
	@Test
	public void testGetPaths() throws Exception {
		System.out.println("getPaths");
		SortedSet<String> paths = Utils.getPaths(source);
		assertTrue(paths.contains("folder1"));
		assertTrue(paths.contains("folder2"));
		assertTrue(paths.contains("folder2/subfolder1"));
		assertFalse(paths.contains("folder1/subfolder1"));
		assertFalse(paths.contains("obviously not a path"));
	}

	@Test
	public void testGetTags() throws Exception {
		System.out.println("getTags");
		TagSet result = Utils.getTags(source);
		assertEquals(5, result.numberTags());
		assertTrue(result.containsTag(new Tag("dogs", "Dogs")));
		assertTrue(result.containsTag(new Tag("animals", "Animals")));
		assertFalse(result.containsTag(new Tag("notatag", "notatag")));
	}
	
	@Test
	public void testGetTagwhitelist_DataSource() throws Exception {
		System.out.println("getTagwhitelist");
		TagSet expResult = new TagSet();
		expResult.addTag("Manmade", "buildings", "Buildings");
		TagSet result = Utils.getTagwhitelist(source);
		assertEquals(expResult, result);
	}

	@Test
	public void testGetAllTags() throws Exception {
		System.out.println("getAllTags");
		ArrayList<String> result = Utils.getAllTags(source);
		assertTrue(result.contains("animals"));
		assertTrue(result.contains("dogs"));
		assertTrue(result.contains("circuits"));
		assertTrue(result.contains("1960s"));
		assertTrue(result.contains("1970s"));
		assertTrue(result.contains("1980s"));
		assertTrue(result.contains("1950s"));
		assertEquals(9, result.size());
	}
}
