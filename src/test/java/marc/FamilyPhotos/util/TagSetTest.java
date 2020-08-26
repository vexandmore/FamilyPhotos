/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marc.FamilyPhotos.util;

import java.util.Iterator;
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

	/**
	 * Test of size method of class TagSet.
	 */
	@Test
	public void testSize() {
		System.out.println("size");
		assertEquals(3, testCase.size());
	}

	/**
	 * Test of numberTags method of class TagSet.
	 */
	@Test
	public void testNumberTags() {
		System.out.println("numberTags");
		assertEquals(6, testCase.numberTags());
	}

	/**
	 * Test of equals method of class TagSet.
	 */
	@Test
	public void testEquals() {
		System.out.println("equals");
		TagSet other = new TagSet();
		other.addTag("Other", "tag1", "Tag1");
		other.addTag("Category2", "tag1", "Tag1");
		other.addTag("Category3", "tag1", "Tag1");
		other.addTag("Category2", "tag2", "Tag2");
		other.addTag("Category2", "tag3", "Tag3");
		other.addTag("Other", "tag1", "Tag1");
		
		assertEquals(true, other.equals(testCase));
		
		other = new TagSet();
		other.addTag("Other", "tag1", "Tag1");
		other.addTag("Other", "tag1", "Tag1");
		other.addTag("Category2", "tag1", "Tag1");
		other.addTag("Category2", "tag2", "Tag2");
		other.addTag("Category2", "tag3", "Tag3");
		other.addTag("Category3", "tag1", "Tag1");
		
		assertEquals(true, other.equals(testCase));
		
		assertEquals(true, new TagSet().equals(new TagSet()));
	}
}
