package marc.FamilyPhotos.util;
import java.util.*;
/**
 * Represents a list of Tags along with the category they belong to.
 * @author Marc
 */
public class TagList implements Iterable<Tag> {
	public final String category;
	private final ArrayList<Tag> tags = new ArrayList<>();
	
	public TagList(String category) {
		this.category = category;
	}
	public void addTag(String tagName, String displayName) {
		tags.add(new Tag(tagName, displayName));
	}
	//methods for JSTL
	public int getSize() {
		return tags.size();
	}
	public String getCategory() {
		return category;
	}
	
	@Override
	public Iterator<Tag> iterator() {
		return tags.iterator();
	}
	/**
	 * JSTL getter for the iterator
	 * @return the iterator for this TagList
	 */
	public Iterator<Tag> getIterator() {
		return iterator();
	}
	
	@Override
	public String toString() {
		return "TagList category:" + category + " tags: " + tags;
	}
	@Override
	public boolean equals (Object other) {
		if (other == null || !(other instanceof TagList)) {
			return false;
		} else {
			TagList otherTagList = (TagList) other;
			return category.equals(otherTagList.category) && tags.equals(otherTagList.tags);
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + Objects.hashCode(this.category);
		hash = 71 * hash + Objects.hashCode(this.tags);
		return hash;
	}
}
