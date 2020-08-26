package marc.FamilyPhotos.util;

/**
 * Class used to represent an image when passed to ResultsPage.
 * @author Marc
 */
public class FamilyPhoto {
    //these strings are the relative path of the photo (ie they start at images/fullsize/etc)
    public final String UUID;
    public final String thumbnail;
	
	/**
	 * Designed to take parameters directly from the database
	 * @param thumbnail
	 * @param UUID 
	 */
    public FamilyPhoto(String thumbnail, String UUID) {
        this.UUID = UUID;
        this.thumbnail = thumbnail;
    }
	
	
    /**
	 * Deprecated
     * Returns the path to request a page allowing the user to view an image and
     * edit its metadata
     */
    public String getEditPath(String contextPath) {
        return contextPath + "/View?UUID=" + UUID + "&Embed=true";
    }
	
    public String getEncodedThumbnailPath() {
        return thumbnail.replace(" ", "%20");
    }
}
