/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marc.FamilyPhotos.util;

import marc.FamilyPhotos.Search.DistanceResult;
import java.util.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marc
 */
public class TagSetTest {
	private static TagSet testCase;
	
	public TagSetTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
		testCase = new TagSet();
		testCase.addTag("Other", "tag1", "Tag1");
		testCase.addTag("Other", "tag1", "Tag1");
		testCase.addTag("Category2", "tag1", "Tag1");
		testCase.addTag("Category2", "tag2", "Tag2");
		testCase.addTag("Category2", "tag3", "Tag3");
		testCase.addTag("Category3", "tag1", "Tag1");
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void testSize() {
		System.out.println("size");
		assertEquals(3, testCase.size());
	}
	
	@Test
	public void testNumberTags() {
		System.out.println("numberTags");
		assertEquals(6, testCase.numberTags());
	}

	@Test
	public void testNewSetEquals() {
		System.out.println("equals");
		TagSet other = new TagSet();
		
		other.addTag("Other", "tag1", "Tag1");
		other.addTag("Category2", "tag1", "Tag1");
		other.addTag("Category3", "tag1", "Tag1");
		other.addTag("Category2", "tag2", "Tag2");
		other.addTag("Category2", "tag3", "Tag3");
		other.addTag("Other", "tag1", "Tag1");
		
		assertTrue(other.equals(testCase));
		
		other = new TagSet();
		other.addTag("Other", "tag1", "Tag1");
		other.addTag("Other", "tag1", "Tag1");
		other.addTag("Category2", "tag1", "Tag1");
		other.addTag("Category2", "tag2", "Tag2");
		other.addTag("Category2", "tag3", "Tag3");
		other.addTag("Category3", "tag1", "Tag1");
		
		assertTrue(other.equals(testCase));
	}
	
	@Test
	public void testEmptySetEqualities() {
		assertFalse(new TagSet().equals(testCase));
		assertTrue(new TagSet().equals(new TagSet()));
	}
	
	@Test
	public void testGetFromDisplayName() {
		assertEquals(new Tag("tag3", "Tag3"), testCase.getFromDisplayName("Tag3").get());
		assertEquals(new Tag("tag1", "Tag1"), testCase.getFromDisplayName("Tag1").get());
	}
	
	@Test
	public void testIdentificationOfExactTagMatch() {
		TagSet set = new TagSet();
		set.addTag("Other", "Mexico", "Mexico City");
		set.addTag("Other", "Mc", "Mc");
		DistanceResult<Tag> result = set.getClosestTags("Mexico");
		assertEquals(1, result.size());
		assertEquals(new Tag("Mexico", "Mexico City"), result.result().get(0));
	}
	
	@Test
	public void testIdentificationOfApproxMatch() {
		TagSet set = new TagSet();
		set.addTag("Other", "Mexico", "Mexico City");
		set.addTag("Other", "Mc", "Mc");
		DistanceResult<Tag> result = set.getClosestTags("Meexico");
		assertEquals(1, result.size());
		assertEquals(new Tag("Mexico", "Mexico City"), result.result().get(0));
		assertTrue(result.getDistance() > 0);
	}
}
