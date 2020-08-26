/*
 copyright Marc Scattolin
*/
package marc.FamilyPhotos;

import marc.FamilyPhotos.util.*;
import java.io.*;
import java.util.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import java.sql.*;
/**
 * This servlet gets the tags, photo paths and connections and forwards 
 * to the index page.
 * @author Marc
 */
public class IndexServlet extends HttpServlet {
	private DataSource ds;
	
	@Override
	public void init() throws ServletException {
		try {
			// Obtain our environment naming context
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// Look up our data source
			ds = (DataSource) envCtx.lookup("jdbc/photos/search");
		} catch (NamingException e) {
			throw new ServletException("error getting datasource", e);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.isUserInRole("family")) {
			request.setAttribute("tags", Utils.getTags(ds));
			request.setAttribute("collections", Utils.getCollections(ds));
		} else {
			request.setAttribute("tags", Utils.getTagwhitelist(ds));
			request.setAttribute("collections", Utils.getLimitedCollections(ds));
		}
		request.setAttribute("folders", Utils.getPaths(ds));

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/index.jsp");
		dispatcher.forward(request, response);
	}
}