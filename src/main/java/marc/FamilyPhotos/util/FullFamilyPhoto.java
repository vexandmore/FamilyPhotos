package marc.FamilyPhotos.util;

import com.drew.imaging.jpeg.*;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.Tag;
import java.io.*;
import java.util.*;
import jakarta.servlet.*;


import com.drew.metadata.*;

/**
 * Used by UpdateServlet to get the metadata on the files
 * @author Marc
 */
public class FullFamilyPhoto {
    public final String fullsizePath;
    public final String thumbnailPath;
    public String tags = "None";//tags cannot be null
	private String[] tagsArr;
    public String comment = null;
    public String date = null;
    public String decade = null;
	
	/**
	 * Creates FamilyPhoto and reads all the metadata from it.
	 * @param location Location of the fullsize photo
	 * @throws JpegProcessingException
	 * @throws IOException
	 */
	public FullFamilyPhoto(File location) throws JpegProcessingException, IOException {
		this(location.getPath(), new FileInputStream(location));
    }
	
	/**
	 * Creates FamilyPhoto with path and inputstream. Closes the stream once
	 * constructor finishes
	 * @param path Path to fullsize photo. Leading / removed
	 * @param stream Stream of fullsize photo. <i>Closed automatically.</i>
	 * @throws JpegProcessingException
	 * @throws IOException
	 */
	public FullFamilyPhoto (String path, InputStream stream) 
			throws JpegProcessingException, IOException {
		Metadata metadata = JpegMetadataReader.readMetadata(stream);
		
        ExifSubIFDDirectory directory2 = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (directory2 != null && !directory.isEmpty()) {
            for (Tag tag : directory.getTags()) {
                if (tag.getTagName().equals("Windows XP Keywords")) {
					tagsArr = tag.getDescription().split(";");
                    tags = getTagsAndSetDecade(tagsArr);
                }
                if (tag.getTagName().equals("Windows XP Comment")) {
                    comment = tag.getDescription();
                }
            }
        }
        if (directory != null && !directory2.isEmpty()) {
            for (Tag tag : directory2.getTags()) {
                if (tag.getTagName().equals("Date/Time Original")) {
                    date = tag.getDescription().substring(0, 10).replace(':', '-');
                }
            }
        }
        fullsizePath = path.replaceAll("^/", "");
        thumbnailPath = fullsizePath.replace("fullsize", "thumbnails");
		
		stream.close();
	}
	
    public String getPhotoPath() {
		return fullsizePath.replaceAll(".*images", "images").replace('\\', '/');
    }
    public String getThumbnailPath() {
        return thumbnailPath.replaceAll(".*images", "images").replace('\\', '/');
    }
	
	/**
	 * Finds all jpegs starting in the path.
	 * @param startPath Path to start from.
	 * @param context ServletContext. Used to get the paths and input streams.
	 * @return ArrayList of FullFamilyPhotos
	 * @throws JpegProcessingException
	 * @throws IOException
	 */
	public static ArrayList<FullFamilyPhoto> getFilesDirectly(String startPath, ServletContext context) 
			throws JpegProcessingException, IOException {
        ArrayList<FullFamilyPhoto> outFiles = new ArrayList<>();
		
		Set<String> contents = context.getResourcePaths(startPath);
		
		if (contents == null)
			throw new IOException("Error in input file");
        for (String path: contents) {
            if (path.matches(".*jpg")) {
                outFiles.add(new FullFamilyPhoto(path, context.getResourceAsStream(path)));
            } else if (path.matches(".*/")) {
                outFiles.addAll(getFilesDirectly(path, context));
            }
        }
        return outFiles;
    }
	
	public boolean tagsValid(ArrayList<String> knownTags) {
		for (String tag: tagsArr) {
			if (tag.equals("1950s") || tag.equals("1960s") || tag.equals("1970s") || tag.equals("1980s"))
				continue;
			boolean inKnownTags = false;
			for (String knownTag: knownTags) {
				if (tag.equalsIgnoreCase(knownTag)){
					inKnownTags = true;
					break;
				}
			}
			if (!inKnownTags) {
				return false;
			}
		}
		return true;
	}
	
    private String getTagsAndSetDecade(String[] tags) {
        String out = "";
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].equals("1950s") || tags[i].equals("1960s") || tags[i].equals("1970s") || tags[i].equals("1980s")) {
                decade = tags[i];
                continue;
            }
            out += (tags[i] + ",");
        }
        if (out.length() > 0) {
            out = out.substring(0,out.length()-1);//strip end comma
        }
		if (out == "") {//prevent null tags
			return "None";
		}
        return out;
    }
	
	/**
	 * Compare FullFamilyPhoto with a series of Strings describing one.
	 * Intended to be used with database output.
	 * @param photoPath
	 * @param thumbnailPath
	 * @param tags
	 * @param comment
	 * @param date
	 * @param decade
	 * @return 
	 */
	public boolean equals(String photoPath, String thumbnailPath, String tags, String comment, String date, String decade) {
		if (!photoPath.equals(this.getPhotoPath()))
			return false;
		if (!thumbnailPath.equals(this.getThumbnailPath()))
			return false;
		if (!equalTags(tags, this.tags))
			return false;
		if (!equals(comment, this.comment))
			return false;
		if (!equals(date, this.date))
			return false;
		if (!equals(decade, this.decade))
			return false;
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof FullFamilyPhoto))
			return false;
		FullFamilyPhoto otherPhoto = (FullFamilyPhoto) other;
		return this.equals(otherPhoto.getPhotoPath(), otherPhoto.getThumbnailPath(), otherPhoto.tags, otherPhoto.comment, otherPhoto.date, otherPhoto.decade);
	}

	/**
	 * auto-generated method
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + Objects.hashCode(this.fullsizePath);
		hash = 97 * hash + Objects.hashCode(this.thumbnailPath);
		hash = 97 * hash + Objects.hashCode(this.tags);
		hash = 97 * hash + Objects.hashCode(this.comment);
		hash = 97 * hash + Objects.hashCode(this.date);
		hash = 97 * hash + Objects.hashCode(this.decade);
		return hash;
	}
	
	/**
	 * Custom compare function for two strings. Null strings and empty strings are considered equal
	 * @param s1
	 * @param s2
	 * @return 
	 */
	private static boolean equals(String s1, String s2) {
		boolean s1Empty = s1 == null || s1.equals("");
		boolean s2Empty = s2 == null || s2.equals("");
		
		if (s1Empty && s2Empty)
			return true;
		if (s1Empty != s2Empty)
			return false;
		if (s1.equals(s2))//if execution reaches here, then it means both strings aren't empty, to equals can be called
			return true;
		return false;
	}
	
	private static boolean equalTags(String tags1, String tags2) {
		if (equals(tags1, tags2))
			return true;
		String[] tags1Separated = tags1.split(",");
		String[] tags2Separated = tags2.split(",");
		if (tags1Separated.length != tags2Separated.length)
			return false;
		for (String tag: tags1Separated) {
			if (!includes(tags2Separated, tag))
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if the string array contains a String, ignoring case.
	 *
	 * @param arg The array to check
	 * @paran key The key being searched for
	 * @return Whether the array contains the key
	 */
	private static boolean includes(String[] arg, String key) throws NullPointerException {
		if (arg == null) {
			throw new NullPointerException("array empty");
		}
		for (String s : arg) {
			if (s.equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}
	
	/*public void printAllTags() {
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(fullSize);
			Iterable<Directory> it = metadata.getDirectories();
			for(Directory dir: it) {
				for (Tag tag: dir.getTagsAndSetDecade()) {
					System.out.println(tag);
				}
			}
		} catch (JpegProcessingException ex) {
			System.err.println(ex.getMessage());
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}*/
}
