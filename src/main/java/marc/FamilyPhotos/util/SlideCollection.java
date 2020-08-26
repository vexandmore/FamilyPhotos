package marc.FamilyPhotos.util;

import java.util.Objects;

/**
 * Immutable class representing a slide collection
 * @author Marc
 */
public class SlideCollection {
	public final String collectionName;
	public final int numberElements;

	public SlideCollection(String collectionName, int numberElements) {
		this.collectionName = collectionName;
		this.numberElements = numberElements;
	}
	
	@Override
	public String toString() {
		return collectionName + ":" + numberElements;
	}
	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null)
			return false;
		if (other instanceof SlideCollection) {
			SlideCollection otherSC = (SlideCollection) other;
			return this.collectionName.equals(otherSC.collectionName) && 
					this.numberElements == otherSC.numberElements;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this.collectionName);
		hash = 29 * hash + this.numberElements;
		return hash;
	}
}
