package marc.FamilyPhotos;

import marc.FamilyPhotos.util.*;
import static marc.FamilyPhotos.util.Utils.*;
import java.io.*;
import java.util.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import java.sql.*;
import com.google.gson.*;

/**
 * Deals with operations that modify slide collections.
 * @author Marc
 */
public class CollectionsServlet extends HttpServlet {
	private DataSource searchSource, updateSource; 
	private Gson gson;
	
	@Override
	public void init() throws ServletException {
		gson = new Gson();
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

	/**
	 * Handles requests to view the collections (returns either JSON or 
	 * a page to modify them).
	 * @param request
	 * @param response 
	 * @throws javax.servlet.ServletException 
	 * @throws java.io.IOException 
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if (anyEmptyString(action)) {
			response.sendError(400, "Action attribute missing in request");
			return;
		}
		
		Collection<SlideCollection> collections;
		if (request.isUserInRole("family")) {
			collections = Utils.getCollections(searchSource);
		} else {
			collections = Utils.getLimitedCollections(searchSource);
		}
		
		switch(action) {
			case "getCollections":
				response.setContentType("application/json");
				response.getWriter().println(gson.toJson(collections));
				break;
			case "manageCollections":
				request.setAttribute("collections", collections);
				request.getRequestDispatcher("/WEB-INF/jsp/ManageCollections.jsp").forward(request, response);
				break;
			default:
				response.sendError(400, "invalid action");
		}
	}
	
	/**
	 * Handles requests to modify a given collection or add one
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		HttpMessage message;
		
		if (anyEmptyString(action)) {
			response.sendError(400, "Sent request to modify a photo collection"
					+ " but information is missing in request or it is invalid");
		} else if (action.equals("addCollection")) {
			message = addCollection(request.getParameter("collectionName"));
			response.setStatus(message.code);
			response.getWriter().println(message.message);
		} else if (action.equals("addPhoto")) {
			message = addPhotos(request.getParameterValues("photoIDs"), request.getParameter("collectionName"));
			response.setStatus(message.code);
			response.getWriter().println(message.message);
		} else if (action.equals("deleteCollection")) {
			message = deleteCollection(request.getParameter("collectionName"), request.getServletContext());
			response.setStatus(message.code);
			response.getWriter().println(message.message);
		} else {
			response.sendError(400, "Invalid request");
		}
	}
	
	/**
	 * Create a new collection if it doesn't exist.
	 * @param collectionName
	 * @return
	 * @throws ServletException 
	 */
	private HttpMessage addCollection(String collectionName)
			throws ServletException {
		HttpMessage out = new HttpMessage();
		
		//check missing parameters
		if (anyEmptyString(collectionName)) {
			out.code = 400;
			out.message = "Parameter missing";
			return out;
		}
		
		String query = "INSERT INTO collections (id, name) VALUES (UUID_TO_BIN(UUID()),?) ON DUPLICATE KEY UPDATE name=name;";
		try (Connection con = updateSource.getConnection();
				PreparedStatement statement = con.prepareStatement(query)) {
			statement.setString(1, collectionName);
			statement.executeUpdate();
			//Cache.updateAllCaches(con);
			out.message = "Collection added successfully";
		} catch (SQLException e) {
			out.code = 500;
			out.message = "An internal error has occurred";
			System.out.println(e.getMessage());
		}
		
		return out;
	}
	
	/**
	 * Delete a collection. The photos that were in the collection still exist
	 * but are not in the collection any more.
	 * @param collectionName
	 * @return
	 * @throws ServletException 
	 */
	private HttpMessage deleteCollection (String collectionName, ServletContext ctx)
			throws ServletException {
		HttpMessage out = new HttpMessage();

		String query = "DELETE FROM photocollections WHERE collectionID=(SELECT id FROM collections WHERE name=?);";
		String query2 = "DELETE FROM collections WHERE name=?;";
		try ( Connection con = updateSource.getConnection();
				PreparedStatement statement = con.prepareStatement(query);
				PreparedStatement statement2 = con.prepareStatement(query2)) {
			statement.setString(1, collectionName);
			ctx.log("deleting items from photocollections:" + statement.executeUpdate());
			statement2.setString(1, collectionName);
			ctx.log("deleting a collection:" + statement2.executeUpdate());
			//Cache.updateAllCaches(con);
			out.message = "Collection deleted successfully";
		} catch (SQLException e) {
			out.code = 500;
			out.message = "An internal error has occurred.";
		}

		return out;
	}
	
	/**
	 * Add photos to a collection.
	 * @param photoIDs The photo ids that will be added to a collection
	 * @param collectionName The name of the collection that will be added to
	 * @return
	 * @throws ServletException 
	 */
	private HttpMessage addPhotos(String[] photoIDs, String collectionName)
			throws ServletException {
		if (anyEmptyString(collectionName,photoIDs)) {
			return new HttpMessage(400, "Parameter missing");
		}
		
		//add collection if doesn't exist
		HttpMessage temp = addCollection(collectionName);
		if (temp.code != 200) {
			return temp;
		}
		
		HttpMessage out = new HttpMessage();
		String query = "INSERT INTO photocollections (photoID, collectionID) VALUES (UUID_TO_BIN(?), (SELECT id FROM collections WHERE name=?))";
		try ( Connection con = updateSource.getConnection();
				PreparedStatement addPhotos = con.prepareStatement(query)) {
			addPhotos.setString(2, collectionName);
			con.setAutoCommit(false);
			for (String photoID : photoIDs) {
				addPhotos.setString(1, photoID);
				addPhotos.executeUpdate();
			}
			con.commit();
			con.setAutoCommit(true);//this connection comes from a pool
			out.message = photoIDs.length + " photo(s) added to collection successfully";
			//Cache.updateAllCaches(con);
		} catch (SQLException e) {
			out.code = 500;
			out.message = "An error has occurred.";
		}
		
		return out;
	}
}

/**
 * Has a String message and int status code. Useful so functions can return
 * a message and code without having to be passed the HttpServletRequest.
 * @author Marc
 */
class HttpMessage {
	public String message;
	public int code = 200;
	public HttpMessage() {}
	public HttpMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}
}