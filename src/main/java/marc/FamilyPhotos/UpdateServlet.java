package marc.FamilyPhotos;

import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import javax.naming.*;
import javax.sql.DataSource;
import com.drew.imaging.jpeg.*;
import java.util.*;
import marc.FamilyPhotos.util.*;

/**
 * Servlet for the database update page
 * @author Marc
 */
public class UpdateServlet extends HttpServlet {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		TagSet tags = Utils.getTags(searchSource);
		TagSet tagsWhitelist = Utils.getTagwhitelist(searchSource);
		Collection<SlideCollection> collections = Utils.getCollections(searchSource);
		request.setAttribute("tags", tags);
		request.setAttribute("tagsWhitelist", tagsWhitelist);
		request.setAttribute("collections", collections);
		RequestDispatcher d = request.getRequestDispatcher("/WEB-INF/jsp/UpdatePage.jsp");
		d.forward(request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		
		switch (request.getParameter("type")) {
			case "addTag":
				addTag(request);
				response.getWriter().println("tag successfully added!");
				break;
			case "addTagToWhitelist":
				addTagToWhitelist(request);
				response.getWriter().println("tag successfully added to list!");
				break;
			case "updateDB":
				//updating the database
				String updateResponse = "";
				try ( Connection con = updateSource.getConnection()) {
					//Cache.updateAllCaches(searchSource);
					ArrayList<String> knownTags = Utils.getAllTags(searchSource);
					updateResponse = addUpdate("/images/fullsize", con, knownTags);
					//Cache.updateAllCaches(searchSource);
				} catch (SQLException | JpegProcessingException e) {
					response.sendError(500, "Error occurred when updating database: " + e.getMessage());
				}
				response.getWriter().print(updateResponse);
				break;
			case "updateCache":
				//fallthrough
			default:
				/*Cache.updateAllCaches(searchSource);
				response.getWriter().println("Cache updated!");*/
				response.sendError(400, "No valid action specified");
		}
	}
	
	private void addTagToWhitelist(HttpServletRequest request) throws ServletException {
		String basicStatement = "INSERT INTO tagwhitelist (id) VALUES ((SELECT id FROM tags WHERE tagName=?))";
		
		String tagName = request.getParameter("tagName");
		try (Connection con = updateSource.getConnection();
				PreparedStatement statement = con.prepareStatement(basicStatement)) {
			statement.setString(1, tagName);
			statement.execute();
			//Cache.updateAllCaches(con);
		} catch (SQLException e) {
			throw new ServletException("Error adding tag", e);
		}
	}
	
	private void addTag(HttpServletRequest request) throws ServletException {
		String basicStatement = "INSERT INTO tags (tagName, displayName, category, id) VALUES (?, ?, ?, UUID_TO_BIN(UUID()))";
		
		String tagName = request.getParameter("tagName");
		String displayName = request.getParameter("displayName");
		String category = request.getParameter("category");
		if (tagName == null || displayName == null || category == null) {
			throw new ServletException ("all fields must be filled out to add a tag");
		}
		
		try (Connection con = updateSource.getConnection();
				PreparedStatement statement = con.prepareStatement(basicStatement)) {
			statement.setString(1, tagName);
			statement.setString(2, displayName);
			statement.setString(3, category);
			statement.execute();
			//Cache.updateAllCaches(con);
		} catch (SQLException e) {
			throw new ServletException("error adding tag", e);
		}
	}
	
	/**
	 * Function called to update the database's information on the photos
	 * @param startPath Where to find the photos (usually /images/fullsize)
	 * @param con
	 * @param knownTags
	 * @return String representing the response.
	 * @throws SQLException
	 * @throws JpegProcessingException
	 * @throws IOException
	 * @throws ServletException 
	 */
	private String addUpdate(String startPath, Connection con, ArrayList<String> knownTags)
			throws SQLException, JpegProcessingException, IOException, ServletException {
		ArrayList<FullFamilyPhoto> photos = FullFamilyPhoto.getFilesDirectly(startPath, getServletContext());
		String response = photos.size() + " photos found.\n";
		
		try (PreparedStatement add = con.prepareStatement("INSERT INTO photos (id, thumbnailPath, photoPath, tags, decade, date, comment) VALUES (UUID_TO_BIN(UUID()), ?, ?, ?, ?, ?, ?)");
				PreparedStatement update = con.prepareStatement("UPDATE photos SET tags = ?, decade = ?, date = ?, comment = ? WHERE photoPath = ?");
				PreparedStatement find = con.prepareStatement("SELECT photoPath, thumbnailPath, tags, comment, date, decade FROM photos WHERE photoPath = ?");
				Statement getTags = con.createStatement();
				ResultSet temp = getTags.executeQuery("SELECT tagName FROM tags");) {
			con.setAutoCommit(false);
			response += updateDatabase(con, photos, add, update, find, knownTags);
		}
		return response;
	}
	
	private static String updateDatabase(Connection con, ArrayList<FullFamilyPhoto> photos, PreparedStatement add, PreparedStatement update, PreparedStatement find, ArrayList<String> knownTags)
			throws SQLException {
		int numUpdated = 0, numSkipped = 0, numAdded = 0;
		String response = "";
		for (FullFamilyPhoto photo : photos) {
			find.setString(1, photo.getPhotoPath());
			ResultSet result = find.executeQuery();
			if (result.next()) {
				//photo is in db
				if (photo.equals(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6))) {
					//photo data in db is identical
					numSkipped++;
					continue;
				}
				if (photo.tagsValid(knownTags)) {
					update.setString(1, photo.tags);
					update.setString(2, photo.decade);
					update.setString(3, photo.date);
					update.setString(4, photo.comment);
					update.setString(5, photo.getPhotoPath());
					try {
						update.execute();
						numUpdated++;
					} catch (SQLException e) {
						response += "Error when updating: " + e.getMessage() + update + "\n";
					}
				} else {
					response += "Could not add photo " + photo.getPhotoPath() + " because tags " + photo.tags + " are invalid.\n";
				}
			} else {
				if (photo.tagsValid(knownTags)) {
					add.setString(1, photo.getThumbnailPath());
					add.setString(2, photo.getPhotoPath());
					add.setString(3, photo.tags);
					add.setString(4, photo.decade);
					add.setString(5, photo.date);
					add.setString(6, photo.comment);
					try {
						add.execute();
						numAdded++;
					} catch (SQLException e) {
						response += "Error when adding: " + e.getMessage() + add + "\n";
					}
				} else {
					response += "Could not add photo " + photo.getPhotoPath() + " because tags " + photo.tags + " are invalid.\n";
				}

			}
		}
		con.commit();
		response += "num added " + numAdded + " numSkipped " + numSkipped + " numUpdated " + numUpdated;
		return response;
	}
}
