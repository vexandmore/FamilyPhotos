package marc.FamilyPhotos;

import marc.FamilyPhotos.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.sql.DataSource;
import com.google.gson.*;
import java.sql.*;

/**
 * Once passed a Get request with a URL with the UUID of an image, it gets
 * the metadata of the image and passes it to FullsizeEditPage. When passed
 * a POST, it edits the photo metadata in the database. (deprecated for now
 * since the JPEG is not updated).
 *
 * @author Marc
 */
public class FullsizeEditPhotoServlet extends HttpServlet {
	private DataSource searchSource, updateSource;
	
	@Override
    public void init() throws ServletException {
		try {
			// Obtain our environment naming context
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// Look up our data source
			searchSource = (DataSource) envCtx.lookup("jdbc/photos/search");
			updateSource = (DataSource) envCtx.lookup("jdbc/photos/full");
		} catch (NamingException e) {
			throw new ServletException("error getting datasource", e);
		}
    }

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String UUID = request.getParameter("UUID");
		try (Connection readCon = searchSource.getConnection();
				PreparedStatement findStatement = readCon.prepareStatement("SELECT photoPath, tags, decade, date, comment, BIN_TO_UUID(id) FROM photos WHERE BIN_TO_UUID(id)=?");){
			findStatement.setString(1, UUID);//this can throw a SQLException so it must go in a try block
			try (ResultSet result = findStatement.executeQuery();) {
				if (!result.next()) {
					response.sendError(404, "Fullsize photo not found.");
					return;
				} else {
					response.setCharacterEncoding("UTF-8");
					//ensure that the text on the page is always fresh (even when an editor uses the back button)
					response.addHeader("Cache-Control", "no-store");
					
					FamilyPhotoDetailed photo = new FamilyPhotoDetailed(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6));
					
					String jsonParam = request.getParameter("Json");
					if (jsonParam != null && jsonParam.equalsIgnoreCase("true")) {
						response.setContentType("application/json");
						String photoJsonString = new Gson().toJson(photo);
						PrintWriter out = response.getWriter();
						out.print(photoJsonString);
					} else {

						request.setAttribute("photo", photo);

						TagSet tags = Utils.getTags(searchSource);
						request.setAttribute("tags", tags);
						RequestDispatcher viewDispatcher = request.getRequestDispatcher("/WEB-INF/jsp/FullsizeEditPage.jsp");
						viewDispatcher.forward(request, response);
					}
				}
			}
		} catch (SQLException e) {
			getServletContext().log("error in ViewEdit get ", e);
			response.sendError(500, "Database error");
		}
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!request.isUserInRole("editor")) {
			response.sendError(403, "You do not have the permission to edit photo data");
			return;
		}
		
		try (Connection updateCon = updateSource.getConnection();
			PreparedStatement updateStatement = updateCon.prepareStatement("UPDATE photos SET tags = ?, decade = ?, comment = ? WHERE BIN_TO_UUID(id) = ?");){
			String tags = arrayToSet(request.getParameterValues("tags"));
			String decade = request.getParameter("decade");
			if (decade.equals(""))
				decade = null;
			String comment = request.getParameter("comment");
			if (comment.equals(""))
				comment = null;
			String UUID = request.getParameter("UUID");
			updateStatement.setString(1, tags);
			updateStatement.setString(2, decade);
			updateStatement.setString(3, comment);
			updateStatement.setString(4, UUID);
			
			updateStatement.execute();
		} catch(SQLException e) {
			getServletContext().log("Error in ViewEdit post ", e);
			response.sendError(500, "Error updating photo data");
			return;
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<h1>Successfully set photo information</h1>");
		//out.println(updateStatement);
	}
	
	@Override
	public void destroy() {
	}
	
	/**
	 * Turns an array of strings into one string, where each element is comma-separated.
	 * Designed to an array of parameters become a string that can be set as a MySQL set.
	 * @param strs
	 * @return 
	 */
	private String arrayToSet(String[] strs) {
		String out = "";
		if (strs == null)
			return null;
		for (String string: strs) {
			out += string + ",";
		}
		out = out.substring(0, out.length()-1);
		return out;
	}
}
