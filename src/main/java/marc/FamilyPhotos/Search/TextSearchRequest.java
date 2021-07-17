package marc.FamilyPhotos.Search;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.sql.*;
import marc.FamilyPhotos.*;
import marc.FamilyPhotos.util.*;

/**
 * Representation of a search request (made by using a text search).
 * @author Marc
 */
public final class TextSearchRequest extends SearchRequest {
	private HttpServletRequest request;
	private List<SearchToken> validTokens = new ArrayList<>();//list of internal tag names
	private List<LogicalOperator> operators = new ArrayList<>();
	private List<String> unknownTokens = new ArrayList<>();
	private boolean isLimitedUser;
	
	/**
	 * Creates SearchRequest object from an HttpServletRequest.
	 *
	 * @param request The request being parsed
	 * @param con Database connection which will be used to get the tags. NOT
	 * closed.
	 * @throws InvalidRequest If request is ill formed
	 * @throws ServletException If an error occurs with the data source (ex 
	 * while getting the list of known tags.)
	 */
	public TextSearchRequest(HttpServletRequest request, Connection con) 
			throws InvalidRequest, ServletException{
		try {
			parseQuery(request, con);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
	
	/**
	 * Creates SearchRequest object from an HttpServletRequest.
	 *
	 * @param request The request being parsed
	 * @param ds DataSource used to get connection to get the known tags.
	 * @throws InvalidRequest If request is ill formed
	 * @throws ServletException If an error occurs with the data source (ex 
	 * while getting the list of known tags.)
	 */
	public TextSearchRequest(HttpServletRequest request, DataSource ds)
			throws InvalidRequest, ServletException {
		try (Connection con = ds.getConnection()) {
			parseQuery(request, con);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
		//System.out.println("found tags: " + tagNames);
	}
	
	private void parseQuery(HttpServletRequest request, Connection con) 
			throws SQLException, ServletException {
		this.isLimitedUser = request.isUserInRole("limited");
		this.request = request;
		
		
		ListIterator<String> tokens = Arrays.asList(request.getParameter("simpleSearchQuery").split(" ")).listIterator();
		
		while (tokens.hasNext()) {			
			if (addNextSearchToken(tokens, isLimitedUser, con)) {
				if (tokens.hasNext()) {
					LogicalOperator.valueOfIgnoreCase(tokens.next())
							.ifPresentOrElse(operators::add,
									() -> {
										operators.add(LogicalOperator.DEFAULT);
										tokens.previous();
									});
				}
			}
		}
		parsePageNum();
		
		//System.out.println("Tags: " + validTokens.toString());
		//System.out.println("Operators: " + operators.toString());
	}
	
	private static List<String> decades = Arrays.asList("1950s", "1960s", "1970s", "1980s");
	private static List<DateTimeFormatter> dateFormats = Arrays.asList(
			DateTimeFormatter.ISO_DATE, DateTimeFormatter.ofPattern("uuuu/MM/dd"),
			DateTimeFormatter.ofPattern("dd/MM/uuuu"));
	/**
	 * If the next token(s) are valid, add them to the list of tokens. 
	 * Otherwise (if it is not at all a tag or matches a logical operator), add 
	 * them to the list of unknown tags.
	 * @param tokens Iterator pointing to the next unseen token.
	 * @param knownTags Tags that photos have. Is used to match the input 
	 * against.
	 * @return If this was successful in adding a tag.
	 */
	private boolean addNextSearchToken(ListIterator<String> tokens, boolean isLimitedUser, 
			Connection con) throws ServletException {
		TagSet knownTags = isLimitedUser ? Utils.getTagwhitelist(con) : Utils.getTags(con);
		Collection<SlideCollection> collections = isLimitedUser ? Utils.getLimitedCollections(con) : Utils.getCollections(con);
		List<String> strCollections = collections.stream().map(collection -> collection.collectionName).collect(Collectors.toList());
			
		String token = tokens.next();
		if (!Utils.anyEmptyString(token)) {	
			DistanceResult<? extends SearchToken> closestMatches = SearchToken
					.findDistance(token, knownTags, decades, dateFormats, strCollections);
			
			if (closestMatches.getDistance() > token.length() || 
					closestMatches.getDistance() > averageElementLength(closestMatches)) {
				unknownTokens.add(token);
				return false;
			} else {
				//add more tokens until it doesn't make the match better
				String newToken = token;
				DistanceResult<? extends SearchToken> newClosestMatches = closestMatches;
				while (tokens.hasNext()) {
					newToken = token + " " + tokens.next();
					newClosestMatches = SearchToken.findDistance(newToken, knownTags, decades, dateFormats, strCollections);
					if (newClosestMatches.getDistance() - closestMatches.getDistance() >= 2) {
						tokens.previous();
						newToken = token;
						newClosestMatches = closestMatches;
						break;
					}
				}
				//If the search results are ambiguous treat it as unknown
				if (newClosestMatches.result().size() > 1) {
					unknownTokens.add(newToken);
					return false;
				} else {
					validTokens.add(newClosestMatches.result().get(0));
					return true;
				}
			}
		} else {
			return false;
		}
	}
	
	/**
	 * @param result The tokens to check
	 * @return The average length of the tags in the result, rounded to int.
	 */
	private int averageElementLength(DistanceResult<? extends SearchToken> result) {
		int totalLength = 0;
		for (SearchToken token: result.result()) {
			totalLength += token.length();
		}
		return totalLength / result.result().size();
	}
	
	/**
	 * Parses the page number from the http request.
	 */
	private void parsePageNum() {
		try {
			showPageNum = Integer.parseInt(request.getParameter("showPageNum"));
		} catch (Exception e) {
			showPageNum = 1;
		}
		if (showPageNum < 1) {
			showPageNum = 1;
		}
	}
	
	@Override
	public PreparedStatement buildQuery(Connection con) throws SQLException, ServletException {
		if (validTokens.isEmpty()) {
			return makeEmptyStatement(con);
		} else {
			return makeFullStatement(con);
		}
	}
	
	private PreparedStatement makeEmptyStatement(Connection con) throws SQLException, ServletException {
		return con.prepareStatement("SELECT 0 LIMIT 0",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}
	
	private PreparedStatement makeFullStatement(Connection con) throws SQLException, ServletException {
		String basicStatement = "SELECT thumbnailPath,BIN_TO_UUID(id) FROM photos WHERE ";

		for (int i = 0; i < validTokens.size() - 1; i++) {
			basicStatement += " " + validTokens.get(i).getSQLClause() + " " + operators.get(i).name();
		}
		//don't add operator after last one
		basicStatement += " " + validTokens.get(validTokens.size() - 1).getSQLClause() + " ";

		if (isLimitedUser) {
			basicStatement += " AND " + Utils.limitedUserQuery(con);
		}

		basicStatement += " ORDER BY ";
		List<TagToken> tags = validTokens.stream()
				.filter(tok -> TagToken.class.isInstance(tok))
				.map(tag -> (TagToken) tag).collect(Collectors.toList());
		for (int i = 0; i < tags.size() - 1; i++) {
			basicStatement += " REGEXP_LIKE(tags, ?)+ ";
		}
		//finish off ordering
		if (tags.size() > 0) {
			basicStatement += " REGEXP_LIKE(tags, ?) desc, ";
		}
		basicStatement += " date, decade, photoPath;";
		PreparedStatement statement = con.prepareStatement(basicStatement,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //http://tutorials.jenkov.com/jdbc/resultset.html
		int i = 1;
		for (SearchToken token : validTokens) {
			i = token.addToPreparedStatement(i, statement);
		}
		for (TagToken tag : tags) {
			i = tag.addToPreparedStatement(i, statement);
		}
		return statement;
	}
	
	private static Pattern pageNum = Pattern.compile("&?showPageNum=[^&]*");
	private static Pattern showImage = Pattern.compile("&?showImage=[^&]*");
	
	@Override
	public String getTrimmedQueryString() {
		String out = "?" + request.getQueryString();
		
		Matcher matchPageNum = pageNum.matcher(out);
		out = matchPageNum.replaceAll("");
		Matcher matchShowImage = showImage.matcher(out);
		out = matchShowImage.replaceAll("");
		return out;
	}
	
	@Override
	public Optional<String> getWarningMessage() {
		if (validTokens.isEmpty() && unknownTokens.isEmpty()) {
			return Optional.of("Search term was blank");
		} else if (unknownTokens.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of("These unknown or unexpected words were in the search: " + 
					unknownTokens.stream().map((str) -> "\"" + str + "\"")
					.collect(Collectors.joining(" and ")));
		}
	}
}
