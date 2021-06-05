package marc.FamilyPhotos;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import marc.FamilyPhotos.util.*;

public abstract class SearchRequest {
	public int showPageNum;
	/**
	 * Makes a prepared statement that will create a PreparedStatement which
	 * will search for the photos that correspond to the input (from the 
	 * HttpServletRequest passed in the constructor).
	 *
	 * @param con JDBC connection
	 * @return
	 * @throws SQLException
	 */
	abstract PreparedStatement buildQuery(Connection con) throws SQLException, ServletException;
	
	/**
	 * Extracts the search parameters from the request and returns a query
	 * string (with a leading ?). Any parameters that could cause a problem
	 * (showImage and showPageNum) removed.
	 * @return String representing the trimmed query string
	 */
	abstract String getTrimmedQueryString();
	
	/**
	 * Returns any warning message associated with the given search request.
	 * @return A warning message or an empty optional.
	 */
	abstract Optional<String> getWarningMessage();
}

/**
 * Representation of a search request (made by using a text search).
 * @author Marc
 */
final class TextSearchRequest extends SearchRequest {
	private HttpServletRequest request;
	private List<String> tagNames = new ArrayList<>();//list of internal tag names
	private List<LogicalOperator> operators = new ArrayList<>();
	private List<String> unknownTokens = new ArrayList<>();
	
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
		this.request = request;
		TagSet knownTags = Utils.getTags(con);
		
		ListIterator<String> tokens = Arrays.asList(request.getParameter("simpleSearchQuery").split(" ")).listIterator();
		
		while (tokens.hasNext()) {			
			if (addNextTag(tokens, knownTags)) {
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
		
		System.out.println("Tags: " + tagNames.toString());
		System.out.println("Operators: " + operators.toString());
	}
	
	/**
	 * If the next token(s) are valid tag, add them to the list of tokens. 
	 * Otherwise (if it is not at all a tag or matches a logical operator), add 
	 * them to the list of unknown tags.
	 * @param tokens Iterator pointing to the next unseen token.
	 * @param knownTags Tags that photos have. Is used to match the input 
	 * against.
	 * @return If this was successful in adding a tag.
	 */
	private boolean addNextTag(ListIterator<String> tokens, TagSet knownTags) {
		String token = tokens.next();
		if (!Utils.anyEmptyString(token)) {
			DistanceResult<Tag> closestMatches = knownTags.getClosestTags(token);

			if (closestMatches.getDistance() > token.length()) {
				unknownTokens.add(token);
				return false;
			} else {
				//add more tokens until it doesn't make the match better
				String newToken = token;
				DistanceResult<Tag> newClosestMatches = closestMatches;
				while (tokens.hasNext()) {
					newToken = token + " " + tokens.next();
					newClosestMatches = knownTags.getClosestTags(newToken);
					if (newClosestMatches.getDistance() - closestMatches.getDistance() >= 2) {
						tokens.previous();
						newToken = token;
						newClosestMatches = closestMatches;
						break;
					}
				}
				if (newClosestMatches.result().size() > 1) {
					unknownTokens.add(newToken);
					return false;
				} else {
					tagNames.add(newClosestMatches.result().get(0).tagName);
					return true;
				}
			}
		} else {
			return false;
		}
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
		String basicStatement;
		if (tagNames.isEmpty()) {
			basicStatement = "SELECT 0 LIMIT 0;";//searching for no photos
			PreparedStatement statement = con.prepareStatement(basicStatement, 
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			return statement;
		} else {
			basicStatement = "SELECT thumbnailPath,BIN_TO_UUID(id) FROM photos WHERE ";
			
			for (int i = 0; i < tagNames.size() - 1; i++) {
				basicStatement += " REGEXP_LIKE(tags, ?)>0 " + operators.get(i).name();
			}
			//don't add operator after last one
			basicStatement += " REGEXP_LIKE(tags, ?)>0 ";
			
			basicStatement += " ORDER BY ";
			for (int i = 0; i < tagNames.size() - 1; i++) {
				basicStatement += " REGEXP_LIKE(tags, ?)+ ";
			}
			//finish off ordering
			basicStatement += " REGEXP_LIKE(tags, ?) desc, date, decade, photoPath;";
			PreparedStatement statement = con.prepareStatement(basicStatement, 
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //http://tutorials.jenkov.com/jdbc/resultset.html
			int i = 1;
			for (String tag: tagNames) {
				statement.setString(i++, "(^|,)" + tag + "($|,)");
			}
			for (String tag: tagNames) {
				statement.setString(i++, "(^|,)" + tag + "($|,)");
			}
			return statement;
		}
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
		if (tagNames.isEmpty() && unknownTokens.isEmpty()) {
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

/**
 * Representation of a search request (made from the form which allows a user to
 * select tags). Takes an HttpServletRequest and turns
 * it into a PreparedStatement with the SQL query. Has other utility
 * methods for search requests also.
 */
final class AdvancedSearchRequest extends SearchRequest {
	//metadata about request. If one of the using fields is true, then the 
	//corresponding data will not be null.
	//If one of these booleans is false, then the data has an abitrary value
	private final boolean usingTags, usingComments, usingDates, 
			usingDecades, usingBoxes, usingCollections;
	
	private final String[] collections;
	private final String[] tags;
	private final String[] comments;
	private final String startDate;
	private final String endDate;
	private final String[] decades;
	private final String sorting;
	private final String box;
	private final String tagsBoolean, commentsBoolean;

	private final HttpServletRequest request;
	private final boolean isLimitedUser;
	/**
	 * Creates SearchRequest object from an HttpServletRequest.
	 *
	 * @param request The request being parsed
	 * @throws InvalidRequest If request is ill formed
	 */
	public AdvancedSearchRequest(HttpServletRequest request) throws InvalidRequest {
		this.request = request;		
		tags = request.getParameterValues("tags");
		tagsBoolean = request.getParameter("tagsBoolean");
		usingTags = tags != null;
		if (usingTags) {
			if (tagsBoolean == null) {
				throw new InvalidRequest("tagsBoolean not present");
			}
			if (!tagsBoolean.equals("and") && !tagsBoolean.equals("or")) {
				throw new InvalidRequest("tag boolean must be and or or");
			}
		}
		
		String tempComments = request.getParameter("comments");
		usingComments = tempComments != null;
		if (usingComments) {
			comments = tempComments.split(" ");
			commentsBoolean = request.getParameter("commentsBoolean");
			if (commentsBoolean == null)
				throw new InvalidRequest("comments boolean not present");
			if (!commentsBoolean.equals("and") && !commentsBoolean.equals("or")) {
				throw new InvalidRequest("comment boolean must be and or or");
			}
		} else {
			comments = null;
			commentsBoolean = null;
		}


		startDate = request.getParameter("startdate");
		endDate = request.getParameter("enddate");
		decades = request.getParameterValues("decades");
		usingDates = (startDate != null && endDate != null);
		if (usingDates) {
			//if startDate, endDate, and decades are specified, then the decades are ignored
			usingDecades = false;
		} else {
			usingDecades = decades != null;
		}
		
		box = request.getParameter("box");
		usingBoxes = box != null;
		

		if (request.getParameter("showPageNum") != null) {
			try {
				showPageNum = Integer.parseInt(request.getParameter("showPageNum"));
			} catch (NumberFormatException e) {
				showPageNum = 1;
			}
			if (showPageNum < 1) {
				showPageNum = 1;
			}
		} else {
			showPageNum = 1;
		}

		String tempSorting = request.getParameter("sorting");
		if (tempSorting == null || (!tempSorting.equals("date") && !tempSorting.equals("alphabetical"))) {
			sorting = "date";
		} else {
			sorting = tempSorting;
		}
		
		collections = request.getParameterValues("collections");
		usingCollections = (collections != null && collections.length > 0);
		
		isLimitedUser = request.isUserInRole("limited");
	}
	@Override
	public PreparedStatement buildQuery(Connection con) throws SQLException, ServletException {
		String basicStatement;
		if (!usingTags && !usingComments && !usingDates && !usingDecades && !usingBoxes && !isLimitedUser && !usingCollections) {
			basicStatement = "SELECT thumbnailPath,BIN_TO_UUID(id) FROM photos "; //searching for all photos
		} else {
			basicStatement = "SELECT thumbnailPath,BIN_TO_UUID(id) FROM photos WHERE ";
		}

		if (usingBoxes) {
			basicStatement += " photoPath LIKE ? ";
			if (usingTags || usingComments || usingDates || usingDecades || isLimitedUser || usingCollections) {
				basicStatement += " AND ";
			}
		}

		/*add tags to statement*/
		if (usingTags) {
			basicStatement += "(";
			for (int i = 0; i < tags.length - 1; i++) {
				//the regex is required so that when someone searches for Nan, it doesn't match Nancy, for instance.
				basicStatement += " REGEXP_LIKE(tags, ?) > 0 " + tagsBoolean;
			}
			basicStatement += " REGEXP_LIKE(tags, ?) > 0 )";//last LOCATE call
			if (usingComments || usingDates || usingDecades || isLimitedUser || usingCollections) {
				basicStatement += " AND";
			}
		}

		/*add comments to statement*/
		if (usingComments) {
			basicStatement += "(";
			for (int i = 0; i < comments.length - 1; i++) {
				basicStatement += " comment LIKE ? " + commentsBoolean;
			}
			basicStatement += " comment LIKE ?) ";

			if (usingDates || usingDecades || isLimitedUser || usingCollections) {
				basicStatement += " AND ";
			}
		}

		/*Add date or decades to statement*/
		if (usingDates) {
			basicStatement += " date BETWEEN ? and ? ";
		} else if (usingDecades) {
			basicStatement += " (";
			for (int i = 0; i < decades.length - 1; i++) {
				basicStatement += " FIND_IN_SET(?,decade) > 0 OR ";
			}
			basicStatement += " FIND_IN_SET(?,decade) > 0 )";//last FIND_IN_SET
			if (isLimitedUser || usingCollections) {
				basicStatement += " AND ";
			}
		}
		
		/*Limit tags if user is limited*/
		if (isLimitedUser) {
			basicStatement += Utils.limitedUserQuery(con);
			if (usingCollections) {
				basicStatement += " AND ";
			}
		}
		
		/*add collections*/
		if (usingCollections) {
			basicStatement += "(";
			for (int i = 0; i < collections.length; i++) {
				basicStatement += " id IN (SELECT photoID FROM photocollections WHERE collectionID=(SELECT id FROM collections WHERE name=?)) OR ";
			}
			//special case for last element
			basicStatement += " FALSE )";
		}

		//end statement with ordering
		if (sorting.equals("date")) {
			basicStatement += " ORDER BY date,decade,photoPath;";
		} else {
			basicStatement += " ORDER BY photoPath;";
		}
		
		/*Add user input to the prepared statement*/
		PreparedStatement statement = con.prepareStatement(basicStatement, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //http://tutorials.jenkov.com/jdbc/resultset.html
		int i = 1;
		if (usingBoxes) {
			statement.setString(i++, "images_fullsize_" + box + "%");
		}
		if (usingTags) {
			for (String tag : tags) {
				statement.setString(i++, "(^|,)" + tag + "($|,)");
			}
		}
		if (usingComments) {
			for (String comment : comments) {
				statement.setString(i++, "%" + comment + "%");
			}
		}
		if (usingDates) {
			statement.setString(i++, startDate);
			statement.setString(i++, endDate);
		}
		if (usingDecades) {
			for (String decade : decades) {
				statement.setString(i++, decade);
			}
		}
		if (usingCollections) {
			for (String collection: collections) {
				statement.setString(i++, collection);
			}
		}
		//System.out.println(statement);
		return statement;
	}
	
	Pattern pageNum = Pattern.compile("&?showPageNum=[^&]*");
	Pattern showImage = Pattern.compile("&?showImage=[^&]*");
	
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
		return Optional.empty();
	}
}


/**
 * Exception representing an invalid search request
 * @author Marc
 */
class InvalidRequest extends Exception {
	public InvalidRequest(String message) {
		super(message);
	}
}
