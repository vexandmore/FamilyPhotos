package marc.FamilyPhotos;

import jakarta.servlet.ServletException;
import java.sql.*;
import java.util.*;

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
	public abstract PreparedStatement buildQuery(Connection con) throws SQLException, ServletException;
	
	/**
	 * Extracts the search parameters from the request and returns a query
	 * string (with a leading ?). Any parameters that could cause a problem
	 * (showImage and showPageNum) removed.
	 * @return String representing the trimmed query string
	 */
	public abstract String getTrimmedQueryString();
	
	/**
	 * Returns any warning message associated with the given search request.
	 * @return A warning message or an empty optional.
	 */
	public abstract Optional<String> getWarningMessage();
}


