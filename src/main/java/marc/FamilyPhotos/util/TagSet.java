package marc.FamilyPhotos.util;
import java.util.*;
/**
 * Represents a list of TagLists.
 * @author Marc
 */
public class TagSet implements Iterable<TagList> {
	private final ArrayList<TagList> tags = new ArrayList<TagList>();
	
	
	public TagSet() {}
	
	/**
	 * Add a tag with the given parameters. Does not check for duplicates.
	 * @param category
	 * @param tagName
	 * @param displayName 
	 */
	public void addTag(String category, String tagName, String displayName) {
		//optimization for when all tags in a particular category are together
		if (tags.size() > 0) {
			TagList lastTagList = tags.get(tags.size() - 1);
			if (lastTagList.category.equals(category)) {
				lastTagList.addTag(tagName, displayName);
				return;
			}
		}
		
		for (TagList tagList: tags) {
			if (tagList.category.equals(category)) {
				tagList.addTag(tagName, displayName);
				return;
			}
		}
		TagList newTagList = new TagList(category);
		newTagList.addTag(tagName, displayName);
		tags.add(newTagList);
	}
	
	@Override
	public Iterator<TagList> iterator() {
		return tags.iterator();
	}
	
	/**
	 * Returns number of TagLists
	 * @return number of TagLists
	 */
	public int size() {
		return tags.size();
	}
	
	public int numberTags() {
		int n = 0;
		for (TagList tagList: tags) {
			n += tagList.size();
		}
		return n;
	}
	
	public boolean containsTag(Tag otherTag) {
		for (TagList tagList: tags) {
			for (Tag tag: tagList) {
				if (otherTag.equals(tag))
					return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String out = "TagSet\n";
		for (TagList tagList: this) {
			out += tagList + "\n";
		}
		return out;
	}
	/**
	 * Checks if this TagSet is equal with another TagSet. For that to be true,
	 * all the TagLists have to have the same contents in the same order.
	 * @param other
	 * @return 
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof TagSet)) {
			return false;
		} else {
			TagSet otherTagSet = (TagSet) other;
			return tags.equals(otherTagSet.tags);
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.tags);
		return hash;
	}
}
