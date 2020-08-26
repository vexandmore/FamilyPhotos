/*
Copyright Marc Scattolin 2020
*/
package marc.FamilyPhotos;

import marc.FamilyPhotos.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import javax.naming.*;
/**
 * If a user is logged in as a limited user, only allows them to access certain
 * photos.
 * @author Marc
 */
public class PhotoFilter extends HttpFilter {
	
	private DataSource ds;
	
	public PhotoFilter() {
		System.out.println("In PhotoFilter constructor");
	}
	
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
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request.isUserInRole("limited")) {
			//gets photo path, but must remove the leading /FamilyPhotos/ in the URI and decode the spaces
			String photoPath = request.getRequestURI().replaceAll("%20", " ").replaceAll("/FamilyPhotos/", "");
			//check for nonexistent photo
			if (getServletContext().getResource("/" + photoPath) == null) {
				response.sendError(404);
				return;
			}
			
			try ( Connection con = ds.getConnection();
					Statement statement = con.createStatement();) {
				//TagSet tagWhitelist = Cache.getTagwhitelist(ds);
				String query = "SELECT COUNT(*) FROM photos WHERE (photoPath='" + photoPath + "' OR thumbnailPath='" + photoPath + "' ) AND ";
				/*for (TagList tagList : tagWhitelist) {
					for (Tag tag : tagList) {
						query += " Locate ('" + tag.tagName + "',tags) > 0 OR ";
					}
				}*/
				//prevent extra OR from causing an issue
				//query += " FALSE);";
				query += Utils.limitedUserQuery(ds);

				ResultSet result = statement.executeQuery(query);
				result.next();
				if (result.getInt(1) < 1) {
					//means the photo does not have the tags needed
					response.sendError(403, "You do not have permission to view this photo");
				} else {
					chain.doFilter(request, response); //invoke next filter and allow them to view photo
				}
			} catch (SQLException e) {
				throw new ServletException("Database error", e);
			}
		} else {
			chain.doFilter(request, response); // invokes next filter in the chain
		}
	}

	@Override
	public void destroy() {
	}
}
