package marc.FamilyPhotos;

import static marc.FamilyPhotos.util.Utils.*;
import marc.FamilyPhotos.util.*;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import javax.naming.*;
import java.sql.*;
import javax.sql.DataSource;
import java.util.*;


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
			searchRequest = makeSearchRequest(request);
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
	
	private SearchRequest makeSearchRequest(HttpServletRequest request) 
			throws InvalidRequest, ServletException {
		if (request.getParameter("simpleSearchQuery") != null) {
			return new TextSearchRequest(request, searchSource);
		} else {
			return new AdvancedSearchRequest(request);
		}
	}
}