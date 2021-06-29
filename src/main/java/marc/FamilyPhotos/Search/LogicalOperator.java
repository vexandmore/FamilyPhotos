package marc.FamilyPhotos.Search;

import java.util.Optional;

/**
 * Represents a logical operator.
 * @author Marc
 */
public enum LogicalOperator {
	AND, OR;
	
	public static final LogicalOperator DEFAULT = AND;
	
	/**
	 * Returns the LogicalOperator represented by the token or an empty.
	 * @param token String to turn into a LogicalOperator
	 * @return Logical operator corresponding to the input token or an empty.
	 */
	public static Optional<LogicalOperator> valueOfIgnoreCase(String token) {
		for (LogicalOperator op: LogicalOperator.values()) {
			if (op.name().equalsIgnoreCase(token)) {
				return Optional.of(op);
			}
		}
		return Optional.empty();
	}
}
