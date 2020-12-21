package marc.FamilyPhotos.util;

import java.util.Objects;

/**
 * Represents a tag (its display and internal name)
 * @author Marc
 */
public class Tag  {
	public final String tagName, displayName;
	public Tag(String tagName, String displayName) {
		this.tagName = tagName;
		this.displayName = displayName;
	}
	
	//methods for JSTL
	public String getTagName() {
		return tagName;
	}
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public String toString() {
		return "Tag " + tagName + ":" + displayName;
	}
	@Override
	public boolean equals (Object other) {
		if (other == null || !(other instanceof Tag))
			return false;
		else {
			Tag otherTag = (Tag) other;
			return tagName.equals(otherTag.tagName) && displayName.equals(otherTag.displayName);
		}
	}
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.tagName);
		hash = 89 * hash + Objects.hashCode(this.displayName);
		return hash;
	}
}
