package marc.FamilyPhotos.util;

/**
 * Class used by ViewEditPhotoServlet to pass the photo's information to ViewPhotoJSP.
 * Expected to be constructed with raw database output so nulls are possible
 * @author Marc
 */
public class FamilyPhotoDetailed {

	public String photoPath, tags, comment, decade, date, UUID;

	public FamilyPhotoDetailed(String photoPath, String tags, String decade, String date, String comment, String UUID) {
		this.photoPath = photoPath;
		this.tags = tags;
		this.decade = decade;
		this.date = date;
		this.comment = comment;
		this.UUID = UUID;
	}
}
