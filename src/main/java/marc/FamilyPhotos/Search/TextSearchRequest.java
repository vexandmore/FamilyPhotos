package marc.FamilyPhotos.Search;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.*;
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
	private List<String> tagNames = new ArrayList<>();//list of internal tag names
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
		TagSet knownTags;
		if (isLimitedUser) {
			knownTags = Utils.getTagwhitelist(con);
		} else {
			knownTags = Utils.getTags(con);
		}
		
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
		
		//System.out.println("Tags: " + tagNames.toString());
		//System.out.println("Operators: " + operators.toString());
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

			if (closestMatches.getDistance() > token.length() || 
					closestMatches.getDistance() > averageTagLength(closestMatches)) {
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
	 * @param result The tags to check
	 * @return The average length of the tags in the result, rounded to int.
	 */
	private int averageTagLength(DistanceResult<Tag> result) {
		int totalLength = 0;
		for (Tag tag: result.result()) {
			totalLength += tag.tagName.length();
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
			
			if (isLimitedUser) {
				basicStatement += " AND " + Utils.limitedUserQuery(con);
			}
			
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
