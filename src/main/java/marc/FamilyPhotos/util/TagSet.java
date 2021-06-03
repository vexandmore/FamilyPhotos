package marc.FamilyPhotos.util;
import java.util.*;
import org.apache.commons.text.similarity.*;
/**
 * Represents a list of TagLists.
 * @author Marc
 */
public class TagSet implements Iterable<TagList> {
	private final ArrayList<TagList> tags = new ArrayList<TagList>();
	private static final LevenshteinDistance distCalculator = LevenshteinDistance.getDefaultInstance();
	
	public TagSet() {}
	
	/**
	 * Add a tag with the given parameters. Does not check for duplicates.
	 * @param category
	 * @param tagName
	 * @param displayName 
	 */
	public void addTag(String category, String tagName, String displayName) {
		//optimization for when all tags in a particular category are added together
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
	 * Getter for JSTL
	 * @return iterator for this TagSet
	 */
	public Iterator<TagList> getIterator() {
		return iterator();
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
			n += tagList.getSize();
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
	
	/**
	 * Returns the Tag which has the display name given. If multiple tags have
	 * the same display name, returns an arbitrary one.
	 * @param displayName Display name you want to search for.
	 * @return An Optional with the tag which corresponds to that display name,
	 * or an empty Optional.
	 */
	public Optional<Tag> getFromDisplayName(String displayName) {
		for (TagList tagList: tags) {
			for (Tag tag: tagList) {
				if (tag.getDisplayName().equals(displayName)) {
					return Optional.of(tag);
				}
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Finds the tag(s) which have an internal or display name which is closest
	 * to the given token.
	 * @param token String which will be matched against.
	 * @return A DistanceResult containing the tags which are all the same 
	 * distance from the given token (0 is equal, a larger number means a 
	 * greater distance). Never null or empty.
	 */
	public DistanceResult<Tag> getClosestTags(String token) {
		int closestDistance = Integer.MAX_VALUE;
		List<Tag> closestTags = new ArrayList<>();
		
		for (TagList tagList: tags) {
			for (Tag tag: tagList) {
				int distanceDisplay = distCalculator.apply(token, tag.displayName);
				int distanceInternal = distCalculator.apply(token, tag.tagName);
				if (Math.min(distanceDisplay, distanceInternal) < closestDistance) {
					closestDistance = Math.min(distanceDisplay, distanceInternal);
					closestTags.clear();
					closestTags.add(tag);
				} else if (Math.min(distanceDisplay, distanceInternal) == closestDistance) {
					closestTags.add(tag);
				}
			}
		}
		return new DistanceResult<>(closestTags, closestDistance);
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
