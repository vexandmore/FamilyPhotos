package marc.FamilyPhotos.Search;
import java.util.*;

/**
 * Represents a set of search results where each of the results is a "distance"
 * away from the search term, and where the results are all the same "distance".
 * Immutable by design (the given result elements are copied).
 * @author Marc
 */
public final class DistanceResult <E> implements Iterable<E> {
	private List<E> resultElements;
	private int distance;

	public DistanceResult(List<E> resultElements, int distance) {
		this.resultElements = List.copyOf(resultElements);
		this.distance = distance;
	}
	
	@Override
	public Iterator<E> iterator() {
		return resultElements.iterator();
	}
	
	/**
	 * Returns the number of result elements.
	 * @return The number of result elements.
	 */
	public int size() {
		return resultElements.size();
	}
	
	/**
	 * @return The result elements. Unmodifiable list.
	 */
	public List<E> result() {
		return resultElements;
	}
	
	public int getDistance() {
		return distance;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		else if (other == this)
			return true;
		if (other instanceof DistanceResult) {
			DistanceResult otherResult = (DistanceResult) other;
			return otherResult.distance == this.distance &&
					otherResult.resultElements.equals(this.resultElements);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.resultElements);
		hash = 97 * hash + this.distance;
		return hash;
	}
}
