package marc.FamilyPhotos.Search;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import marc.FamilyPhotos.util.Tag;
import marc.FamilyPhotos.util.TagSet;


/**
 * Represents something that could be searched by in the database, like a date 
 * or tag. Has two SQL related methods: getSQLClause addToPreparedStatement.
 * getSQLClause must be called for all tokens in order, followed by calling
 * addToPreparedStatement to complete the prepared statement.
 * 
 * Subclasses might not override equals() or hashCode()
 * @author Marc
 */
interface SearchToken {
	
	/**
	 * Finds the distance between the input token and the given match data.
	 */
	public static DistanceResult<? extends SearchToken> findDistance(String token, 
			TagSet knownTags, List<String> decades, List<DateTimeFormatter> dateFormats,
			List<String> collections) {
		//If it matches a date format, return that.
		Optional<DistanceResult<DateToken>> dateDistance = DateToken.
				makeDistanceResult(token, dateFormats);
		if (dateDistance.isPresent()) {
			return dateDistance.get();
		}
		//If it doesn't match a date format, return the closest matches
		List<DistanceResult<SearchToken>> results = Arrays.asList(
				DistanceResult.ofStr(token, decades, DecadeToken::new),
				DistanceResult.ofStr(token, collections, CollectionToken::new),
				TagToken.makeDistanceResult(token, knownTags));
		return DistanceResult.reduce(results);
	}
	
	/**
	 * @return A where clause which should be appended to the SQL query. Will
	 * usually have ?s as they make prepared statements.
	 */
	public abstract String getSQLClause();
	
	/**
	 * Completes the prepared statement started with the getSQLClause.
	 * @param parameterIndex The parameter index which corresponds to this token.
	 * @param statement PreparedStatement in which to set the parameters.
	 * @return The index that the next token should use.
	 * @throws SQLException 
	 */
	public abstract int addToPreparedStatement(int parameterIndex, 
			PreparedStatement statement) throws SQLException;
	
	/**
	 * Get the length of this token.
	 * @return The 'length' of the data that this token has.
	 */
	public abstract int length();
}

class TagToken implements SearchToken {
	private Tag tag;
	
	public TagToken(Tag tag) {
		this.tag = tag;
	}
	
	public static DistanceResult<SearchToken> makeDistanceResult(String token, TagSet knownTags) {
		DistanceResult<Tag> strDistance = knownTags.getClosestTags(token);
		List<SearchToken> result = new ArrayList<>();
		for (Tag tag: strDistance) {
			result.add(new TagToken(tag));
		}
		return new DistanceResult<>(result, strDistance.getDistance());
	}
	
	@Override
	public String getSQLClause() {
		return "REGEXP_LIKE(tags, ?) > 0";
	}
	
	@Override
	public int addToPreparedStatement(int row, PreparedStatement statement)
			throws SQLException {
		statement.setString(row++, "(^|,)" + tag.tagName + "($|,)");
		return row;
	}
	
	@Override
	public int length() {
		return tag.tagName.length();
	}
}

class DecadeToken implements SearchToken {
	private String decade;

	public DecadeToken(String decade) {
		this.decade = decade;
	}
	
	@Override
	public String getSQLClause() {
		return "FIND_IN_SET(?,decade) > 0";
	}
	
	@Override
	public int addToPreparedStatement(int row, PreparedStatement statement)
			throws SQLException {
		statement.setString(row++, decade);
		return row;
	}
	
	@Override
	public int length() {
		return decade.length();
	}
}

class CollectionToken implements SearchToken {
	private String collection;

	public CollectionToken(String collection) {
		this.collection = collection;
	}
	
	@Override
	public String getSQLClause() {
		return " id IN (SELECT photoID FROM photocollections WHERE collectionID=(SELECT id FROM collections WHERE name=?)) ";
	}
	
	@Override
	public int addToPreparedStatement(int row, PreparedStatement statement)
			throws SQLException {
		statement.setString(row++, collection);
		return row;
	}
	
	@Override
	public int length() {
		return collection.length();
	}
}

class DateToken implements SearchToken {
	private TemporalAccessor date;

	public DateToken(TemporalAccessor date) {
		this.date = date;
	}
	
	public static Optional<DistanceResult<DateToken>> makeDistanceResult
		(String token, List<DateTimeFormatter> formats) {

		for (DateTimeFormatter format : formats) {
			try {
				TemporalAccessor date = format.parse(token);
				return Optional.of(new DistanceResult<>(Arrays.asList(new DateToken(date)), 0));
			} catch (DateTimeParseException e) {
			}
		}
		return Optional.empty();
	}
	
	@Override
	public String getSQLClause() {
		return "date BETWEEN ? and ?";
	}
	
	@Override
	public int addToPreparedStatement(int row, PreparedStatement statement) 
	throws SQLException {
		statement.setString(row++, DateTimeFormatter.ISO_DATE.format(date));
		statement.setString(row++, DateTimeFormatter.ISO_DATE.format(date));
		return row;
	}
	
	@Override
	public int length() {
		return DateTimeFormatter.ISO_DATE.format(date).length();
	}
}