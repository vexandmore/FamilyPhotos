package marc.FamilyPhotos;

import static marc.FamilyPhotos.util.Utils.*;
import marc.FamilyPhotos.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import java.sql.*;
import javax.sql.DataSource;
import java.util.*;
import java.util.regex.*;

/**
 * This servlet initializes a jdbc connection in init(). When passed a GET
 * request (from index.jsp), it parses it into a sql query and passes the 
 * results to a JSP page, ResultsJSP.
 *
 * @author Marc
 */
public class SearchServlet extends HttpServlet {
	private DataSource searchSource;

	@Override
	public void init() throws ServletException {
		try {
			// Obtain our environment naming context
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// Look up our data source
			searchSource = (DataSource) envCtx.lookup("jdbc/photos/search");
		} catch (NamingException e) {
			throw new ServletException("error getting datasource", e);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		SearchRequest searchRequest;
		try {
			searchRequest = new SearchRequest(request);
		} catch (InvalidRequest e) {
			response.sendError(400, e.getMessage());
			return;
		}
		
		try (Connection con = searchSource.getConnection();
				PreparedStatement statement = searchRequest.buildQuery(con);
				ResultSet result = statement.executeQuery();) {
			//Special cases related to number of results and page number
			int numberResults = findNumberResults(result);
			if (numberResults == 0) {
				request.setAttribute("photos", new ArrayList<FamilyPhoto>());
				RequestDispatcher noResult = request.getRequestDispatcher("/WEB-INF/jsp/NoResultsPage.jsp");
				noResult.forward(request, response);
				return;
			}
			int numberPages = (int) Math.ceil(numberResults / 30.0);
			if (searchRequest.showPageNum > numberPages) {
				searchRequest.showPageNum = numberPages;
			}
			request.setAttribute("title", "Images page " + searchRequest.showPageNum + " out of " + numberPages);

			//add photos
			result.relative((searchRequest.showPageNum * 30) - 30);//set start point for the requested page number
			ArrayList<FamilyPhoto> photos = new ArrayList<>();
			for (int i = 0; i < 30 && result.next(); i++) {
				photos.add(new FamilyPhoto(result.getString(1), result.getString(2)));
			}
			request.setAttribute("photos", photos);
			//add links
			if (searchRequest.showPageNum == 1) {
				request.setAttribute("previousLink", "javascript:;");
			} else {
				request.setAttribute("previousLink", request.getRequestURI() + searchRequest.getTrimmedQueryString() + "&showPageNum=" + (searchRequest.showPageNum - 1));
			}
			if (searchRequest.showPageNum == numberPages) {
				request.setAttribute("nextLink", "javascript:;");
			} else {
				request.setAttribute("nextLink", request.getRequestURI() + searchRequest.getTrimmedQueryString() + "&showPageNum=" + (searchRequest.showPageNum + 1));
			}
			request.setAttribute("returnLink", request.getRequestURI().replace("Search", ""));
			
			RequestDispatcher resultDispatcher = request.getRequestDispatcher("/WEB-INF/jsp/ResultsPage.jsp");
			resultDispatcher.forward(request, response);
			return;
		} catch (SQLException e) {
			getServletContext().log("error in SearchServlet get ", e);
			response.sendError(500, "Database error"); //hide details of a SQLException from user
		}
	}

	@Override
	public void destroy() {
	}
}
/**
 * Representation of a search request. Takes an HttpServletRequest and turns
 * it into a PreparedStatement with the SQL query. Has other utility
 * methods for search requests also.
 */
class SearchRequest {
	int showPageNum;
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
	public SearchRequest(HttpServletRequest request) throws InvalidRequest {
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

	/**
	 * Makes a prepared statement that selects the thubnailPath and id from the
	 * photos with the WHERE clauses are given by the SearchRequest.
	 *
	 * @param con JDBC connection
	 * @return
	 * @throws SQLException
	 */
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
	/**
	 * Extracts the search parameters from the request and returns a query
	 * string (with a leading ?). Any parameters that could cause a problem
	 * (showImage and showPageNum) removed.
	 * @return String representing the trimmed query string
	 */
	public String getTrimmedQueryString() {
		String out = "?" + request.getQueryString();
		
		Matcher matchPageNum = pageNum.matcher(out);
		out = matchPageNum.replaceAll("");
		Matcher matchShowImage = showImage.matcher(out);
		out = matchShowImage.replaceAll("");
		return out;
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
