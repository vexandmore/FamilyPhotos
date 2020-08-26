package marc.FamilyPhotos.util;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;
import com.drew.imaging.jpeg.*;
import org.junit.*;
/**
 *
 * @author Marc
 */
public class FullFamilyPhotoTest {

	public FullFamilyPhotoTest() {
	}

	private static FullFamilyPhoto photo1;
	private static FullFamilyPhoto photo2;
	private static FullFamilyPhoto photo3;
	private static FullFamilyPhoto photo4;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			photo1 = new FullFamilyPhoto(new File(".\\src\\test\\images\\fullsize\\imgICE001.jpg"));
			photo2 = new FullFamilyPhoto(new File(".\\src\\test\\images\\fullsize\\imgICE002.jpg"));
			photo3 = new FullFamilyPhoto(new File(".\\src\\test\\images\\fullsize\\imgICE003.jpg"));
			photo4 = new FullFamilyPhoto(new File(".\\src\\test\\images\\fullsize\\imgICE020.jpg"));
		} catch (JpegProcessingException | IOException e) {
			throw new RuntimeException(e);
		}
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
	 * Test of getPhotoPath method, of class FullFamilyPhoto.
	 */
	@Test
	public void testGetPhotoPath() {
		System.out.println("getPhotoPath");

		FullFamilyPhoto instance = photo1;
		String expResult = "images/fullsize/imgICE001.jpg";
		String result = instance.getPhotoPath();
		assertEquals(expResult, result);

	}

	/**
	 * Test of getThumbnailPath method, of class FullFamilyPhoto.
	 */
	@Test
	public void testGetThumbnailPath() {
		System.out.println("getThumbnailPath");

		FullFamilyPhoto instance = photo1;
		String expResult = "images/thumbnails/imgICE001.jpg";
		String result = instance.getThumbnailPath();
		assertEquals(expResult, result);

	}

	/**
	 * Test the tag extraction of FullFamilyPhoto
	 */
	@Test
	public void testTags() {
		System.out.println("tag extraction");

		FullFamilyPhoto instance = photo1;
		String expResult = "NL,Family,Laurie,GrandFalls";
		String result = instance.tags;
		assertEquals(expResult, result);

		instance = photo2;
		expResult = "Family,Carol,Gail,NL,Millertown";
		result = instance.tags;
		assertEquals(expResult, result);

		instance = photo3;
		expResult = "Family,Gail,Millertown,NL";
		result = instance.tags;
		assertEquals(expResult, result);

	}

	/**
	 * Test of tagsValid method, of class FullFamilyPhoto.
	 */
	@Test
	public void testTagsValid() {
		System.out.println("tagsValid");

		ArrayList<String> knownTags = new ArrayList<>(Arrays.asList("Family", "Gail", "Millertown", "NL", "Carol", "GrandFalls", "1950s"));

		FullFamilyPhoto instance = photo1;
		boolean expResult = false;
		boolean result = instance.tagsValid(knownTags);
		assertEquals(expResult, result);

		instance = photo2;
		expResult = true;
		result = instance.tagsValid(knownTags);
		assertEquals(expResult, result);

		instance = photo3;
		expResult = true;
		result = instance.tagsValid(knownTags);
		assertEquals(expResult, result);

	}

	/**
	 * Test of equals method, of class FullFamilyPhoto.
	 */
	@Test
	public void testEquals() {
		System.out.println("equals");

		FullFamilyPhoto instance = photo1;
		String photoPath = "images/fullsize/imgICE001.jpg";
		String thumbnailPath = "images/thumbnails/imgICE001.jpg";
		String tags = "Family,Laurie,GrandFalls,NL"; //tags rearranged
		String comment = "";
		String date = "1969-09-01";
		String decade = "1960s";
		assertTrue(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));

		instance = photo1;
		photoPath = "images/fullsize/imgICE001.jpg";
		thumbnailPath = "images/thumbnails/imgICE001.jpg";
		tags = "Family,Laurie,GrandFalls,NL";
		comment = "";
		date = "1969-09-02";//false date
		decade = "1960s";
		assertFalse(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));

		instance = photo2;
		photoPath = "images/fullsize/imgICE002.jpg";
		thumbnailPath = "images/thumbnails/imgICE002.jpg";
		tags = "Family,Carol,Gail,NL,Millertown";
		comment = "";
		date = "";
		decade = "1950s";
		assertTrue(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));

		instance = photo3;
		photoPath = "images/fullsize/imgICE003.jpg";
		thumbnailPath = "images/thumbnails/imgICE003.jpg";
		tags = "Family,Gail,Millertown,NL";
		comment = "";
		date = "1961-07-02";
		decade = "1960s";
		assertFalse(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));

	}

	@Test
	public void testComment() {
		System.out.println("comment extraction");
		
		FullFamilyPhoto instance = photo4;
		String expectedResult = "Corner Br., 1961 Kuke of Devonshire in center";
		String result = instance.comment;
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testDecade() {
		System.out.println("decade extraction");
		
		FullFamilyPhoto instance = photo1;
		String expectedResult = "1960s";
		String result = instance.decade;
		assertEquals(expectedResult, result);
		
		instance = photo2;
		expectedResult = "1950s";
		result = instance.decade;
		assertEquals(expectedResult, result);
		
		instance = photo3;
		expectedResult = "1960s";
		result = instance.decade;
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testEquals2() {
		try {
			FullFamilyPhoto instance = new FullFamilyPhoto(new File(".\\src\\test\\images\\fullsize\\imgICE001.jpg"));
			assertTrue(photo1.equals(instance));
			assertTrue(instance.equals(photo1));
		} catch (JpegProcessingException | IOException e) {
			fail("exception occurred " + e);
		}
		assertTrue(photo1.equals(photo1));
		assertFalse(photo1.equals(photo2));
		assertFalse(photo2.equals(photo4));
		assertFalse(photo3.equals(photo1));
	}

}
