package marc.FamilyPhotos.Search;
import java.util.*;
import java.util.function.Function;
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * Represents a set of search results where each of the results is a "distance"
 * away from the search term, and where the results are all the same "distance".
 * The "distance" is intended to be 0 when the result matches exactly the search
 * term, positive otherwise.
 * The resultElements must not be an empty list.
 * Immutable by design (the given result elements are copied).
 * @author Marc
 */
public final class DistanceResult <E> implements Iterable<E> {
	private List<E> resultElements;
	private int distance;

	/**
	 * Creates DistanceResult from the given result elements and distance.
	 * @param resultElements List of result elements. Copied.
	 * @param distance Distance from the result elements.
	 */
	public DistanceResult(List<E> resultElements, int distance) {
		this.resultElements = List.copyOf(resultElements);
		this.distance = distance;
	}
	
	public DistanceResult(E resultElement, int distance) {
		this.resultElements = List.copyOf(resultElements);
		this.distance = distance;
	}
	
	/**
	 * Reduces the list of DistanceResults to one DistanceResult which has all 
	 * the elements among the input results which have the minimum distance.
	 * @param distanceResults Elements to reduce.
	 */
	public static <T> DistanceResult<T> reduce(List<DistanceResult<T>> distanceResults) {
		List<T> newResultElements = new ArrayList<>();
		int minDistance = distanceResults.stream()
				.mapToInt(DistanceResult::getDistance).min().orElseThrow();
		for (DistanceResult distanceResult: distanceResults) {
			if (distanceResult.getDistance() == minDistance) {
				newResultElements.addAll(distanceResult.resultElements);
			}
		}
		return new DistanceResult<>(newResultElements, minDistance);
	}
	
	private static final LevenshteinDistance distCalculator = LevenshteinDistance.getDefaultInstance();
	/**
	 * Convenience method to find the DistanceResult between the given token and 
	 * the matchTokens, and convert the closest tokens to another type.
	 * @param token Input token which will be matched against the matchTokens.
	 * @param matchTokens Tokens the token will be matched against.
	 * @param ctor Function that will take a string and turn it into the 
	 * appropriate type.
	 */
	public static <T> DistanceResult<T> ofStr(String token, List<String> matchTokens,
			Function<String, T> ctor) {
		int closestDistance = Integer.MAX_VALUE;
		List<T> closestTokens = new ArrayList<>();
		
		for (String matchToken: matchTokens) {
				int distance = distCalculator.apply(token, matchToken);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestTokens.clear();
					closestTokens.add(ctor.apply(matchToken));
				} else if (distance == closestDistance) {
					closestTokens.add(ctor.apply(matchToken));
				}
		}
		return new DistanceResult<>(closestTokens, closestDistance);
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
