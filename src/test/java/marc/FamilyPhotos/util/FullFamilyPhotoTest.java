package marc.FamilyPhotos.util;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;
import com.drew.imaging.jpeg.*;
import org.junit.*;
import java.nio.file.*;
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
	private static Path fullsizePath;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			fullsizePath = Paths.get(".", "src", "test", "images", "fullsize");
			//has comment
			photo1 = new FullFamilyPhoto(fullsizePath.resolve("folder1")
					.resolve("laura-college-K_Na5gCmh38-unsplash.jpg").toFile());
			//has 2 tags
			photo2 = new FullFamilyPhoto(fullsizePath.resolve("folder2").resolve("subfolder1")
					.resolve("marliese-streefland-2l0CWTpcChI-unsplash.jpg").toFile());
			//has decade
			photo3 = new FullFamilyPhoto(fullsizePath.resolve("folder2")
					.resolve("sorasak-KxCJXXGsv9I-unsplash.jpg").toFile());
			//has date
			photo4 = new FullFamilyPhoto(fullsizePath.resolve("folder1")
					.resolve("lily-banse--YHSwy6uqvk-unsplash.jpg").toFile());
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
		String expResult = "images/fullsize/folder1/laura-college-K_Na5gCmh38-unsplash.jpg";
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
		String expResult = "images/thumbnails/folder1/laura-college-K_Na5gCmh38-unsplash.jpg";
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
		assertEquals("animals", instance.tags);
		
		instance = photo2;
		assertEquals("animals,dogs", instance.tags);

		instance = photo3;
		assertEquals("buildings", instance.tags);

	}

	/**
	 * Test of tagsValid method, of class FullFamilyPhoto.
	 */
	@Test
	public void testTagsValid() {
		System.out.println("tagsValid");

		ArrayList<String> knownTags = new ArrayList<>(Arrays.asList("animals", "buildings", "1950s"));
		assertTrue(photo1.tagsValid(knownTags));
		assertFalse(photo2.tagsValid(knownTags));
		assertTrue(photo3.tagsValid(knownTags));
	}

	/**
	 * Test of equals method, of class FullFamilyPhoto.
	 */
	@Test
	public void testEquals() {
		System.out.println("equals");

		FullFamilyPhoto instance = photo1;
		String photoPath = "images/fullsize/folder1/laura-college-K_Na5gCmh38-unsplash.jpg";
		String thumbnailPath = "images/thumbnails/folder1/laura-college-K_Na5gCmh38-unsplash.jpg";
		String tags = "animals";
		String comment = "this is a comment";
		String date = "";
		String decade = "1980s";
		assertTrue(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));
		
		decade = "1990s";//false decade
		assertFalse(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));
		
		instance = photo2;
		photoPath = "images/fullsize/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg";
		thumbnailPath = "images/thumbnails/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg";
		tags = "dogs,animals";//tags rearranged
		comment = "";
		date = "";
		decade = "";
		System.out.println(photo1);
		System.out.println(photo2);
		assertTrue(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));

		instance = photo4;
		photoPath = "images/fullsize/folder1/lily-banse--YHSwy6uqvk-unsplash.jpg";
		thumbnailPath = "images/thumbnails/folder1/lily-banse--YHSwy6uqvk-unsplash.jpg";
		tags = "food";
		comment = "";
		date = "1975-01-01";//right date
		decade = "";
		assertTrue(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));
		
		date = "1975-01-02";//wrong date
		assertFalse(instance.equals(photoPath, thumbnailPath, tags, comment, date, decade));
	}

	@Test
	public void testComment() {
		System.out.println("comment extraction");
		
		assertEquals("this is a comment", photo1.comment);
	}
	
	@Test
	public void testDecade() {
		System.out.println("decade extraction");
		
		assertEquals("1960s", photo3.decade);
		assertEquals("1980s", photo1.decade);
	}
	
	@Test
	public void testEquals2() {
		try {
			FullFamilyPhoto instance = new FullFamilyPhoto(fullsizePath.resolve("folder1")
					.resolve("laura-college-K_Na5gCmh38-unsplash.jpg").toFile());
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
