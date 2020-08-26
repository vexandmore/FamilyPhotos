/*
 * Copyright Marc Scarrolin
 */
package marc.FamilyPhotos.util;

import java.sql.*;
import javax.sql.*;
import javax.servlet.*;
import java.util.*;
/**
 * Simple utility functions and common SQL queries.
 * @author Marc
 */
public class Utils {
	/**
	 * Returns the paths of the images (i.e. without the filenames)
	 * @param ds Cached Set containing the unique paths
	 * @return
	 * @throws ServletException Wraps a SQLException
	 */
	public static SortedSet<String> getPaths(DataSource ds) throws ServletException {
		String query = "SELECT photoPath FROM photos";
		SortedSet<String> paths = new TreeSet<>();
		try (Connection con = ds.getConnection();
				Statement statement = con.createStatement();
				ResultSet result = statement.executeQuery(query)) {
			while (result.next()) {
				//remove the last part of the path (the filename) and the
				//beginning image/fullsize to match what SearchServlet expects
				paths.add(result.getString(1).replaceAll("/[^/]*$", "").replaceAll("images/fullsize/", ""));
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
		return paths;
	}
	
	/**
	 * Gets all the cached tags into a String list. Adds 1950s, 1960s, etc
	 * since they appear in jpeg tags
	 * @param ds DataSource for MySQL database
	 * @return
	 * @throws ServletException 
	 */
	public static ArrayList<String> getAllTags(DataSource ds)
			throws ServletException {
		TagSet tags = getTags(ds);
		ArrayList<String> out = new ArrayList<>();
		for (TagList tagList: tags) {
			for (Tag tag: tagList) {
				out.add(tag.tagName);
			}
		}
		out.add("1950s");
		out.add("1960s");
		out.add("1970s");
		out.add("1980s");
		return out;
	}
	
	/**
	 * Get the tags in the database
	 * @param ds
	 * @return
	 * @throws ServletException wraps a SQLException
	 */
	public static TagSet getTags (DataSource ds) throws ServletException {
		String query = "SELECT tagName, displayName, category FROM tags ORDER BY category,displayName;";
		TagSet tags = new TagSet();
		try (Connection con = ds.getConnection();
				Statement getTags = con.createStatement();
				ResultSet result = getTags.executeQuery(query);) {
			while (result.next()) {
				tags.addTag(result.getString(3), result.getString(1), result.getString(2));
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
		return tags;
	}
	
	public static TagSet getTagwhitelist (DataSource ds) throws ServletException {
		try (Connection con = ds.getConnection()) {
			return getTagwhitelist(con);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
	
	public static TagSet getTagwhitelist (Connection con) throws ServletException {
		String query = "SELECT tagName, displayName, category FROM tags WHERE id IN (SELECT id FROM tagwhitelist) ORDER BY category,displayName;";
		TagSet tags = new TagSet();
		try (Statement getTags = con.createStatement();
				ResultSet result = getTags.executeQuery(query);) {
			while (result.next()) {
				tags.addTag(result.getString(3), result.getString(1), result.getString(2));
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
		return tags;
	}
	
	/**
	 * Returns only collections that contain photos visible to a limited user.
	 * @param ds Datasource for a MySQL database.
	 * @return
	 * @throws ServletException Wraps a SQLException
	 */
	public static Collection<SlideCollection> getLimitedCollections (DataSource ds)
			throws ServletException {
		String limitedPhotosQuery = Utils.limitedUserQuery(ds);
		Set<String> allCollectionNames = new HashSet<>();
		Collection<SlideCollection> out = new ArrayList<>(getCollections(ds));
		String query = "SELECT name FROM collections WHERE id IN (" + 
				"SELECT collectionID from photocollections WHERE photoID IN (" + 
				"SELECT id FROM photos WHERE " + limitedPhotosQuery + "))";
		
		try (Connection con = ds.getConnection();
				ResultSet result = con.createStatement().executeQuery(query);) {
			while (result.next()) {
				allCollectionNames.add(result.getString(1));
			}
			out.removeIf((collection) -> {return !allCollectionNames.contains(collection.collectionName);});
		} catch (SQLException e) {
			throw new ServletException("Error getting collections", e);
		}
		return out;
	}
	
	/**
	 * Get all slide collections that exist
	 * @param ds DataSource for MySQL database
	 * @return List containing the collections
	 * @throws ServletException wraps a SQLException
	 */
	public static Collection<SlideCollection> getCollections (DataSource ds) 
			throws ServletException {
		Collection<SlideCollection> collections = new ArrayList<>();
		String query = "SELECT COUNT(DISTINCT(photoID)),(SELECT name FROM collections WHERE id=collectionID) as collectionName"
				+ " FROM photocollections GROUP BY collectionID ORDER BY collectionName";
		
		try (Connection con = ds.getConnection();
				Statement statement = con.createStatement();
				ResultSet result = statement.executeQuery(query)) {
			collections = new ArrayList<>();
			while (result.next()) {
				collections.add(new SlideCollection(result.getString(2), result.getInt(1)));
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
		return collections;
	}
	
	
	
	/**
	 * Creates a String consisting of the query to be added to only see photos visible to limited user.
	 * The string is intended to be put as a WHERE clause and has starting and ending parentheses.
	 * @param ds
	 * @return 
	 * @throws javax.servlet.ServletException 
	 */
	public static String limitedUserQuery(DataSource ds) throws ServletException {
		try (Connection con = ds.getConnection()) {
			return limitedUserQuery(con);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
	/**
	 * Creates a String consisting of the query to be added to only see photos visible to limited user.
	 * The string is intended to be put as a WHERE clause and has starting and ending parentheses.
	 * @param con
	 * @return
	 * @throws ServletException 
	 */
	public static String limitedUserQuery(Connection con) throws ServletException {
		String basicStatement = "(";
		TagSet tagWhitelist = getTagwhitelist(con);
		for (TagList tagList : tagWhitelist) {
			for (Tag tag : tagList) {
				basicStatement += " REGEXP_LIKE(tags,'" + tag.tagName + "') > 0 OR ";
			}
		}
		//special case for the last element
		basicStatement += " FALSE )";
		return basicStatement;
	}
	
	/**
	 * Checks if any string is null or equal to "".
	 * @param strs The strings
	 * @return 
	 */
	public static boolean anyEmptyString (String... strs) {
		if (strs == null)
			return true;
		for (String str: strs) {
			if (str == null || str.equals(""))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if any string is null or equal to "".
	 * @param str1
	 * @param strings
	 * @return 
	 */
	public static boolean anyEmptyString(String str1, String... strings) {
		if (str1 == null || str1.equals("")) {
			return true;
		}
		if (strings == null) {
			return true;
		} else {
			for (String string : strings) {
				if (string == null || string.equals("")) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if the string is null or equal to "".
	 * @param str
	 * @return 
	 */
	public static boolean anyEmptyString(String str) {
		return (str == null || str.equals(""));
	}
	
	/**
	 * Finds the number of results in a ResultSet and resets the ResultSet to
	 * before the first element, like default
	 *
	 * @param result A sql ResultSet
	 * @return The number of results
	 * @throws SQLException
	 */
	public static int findNumberResults(ResultSet result) throws SQLException {
		result.last();
		int numberResults = result.getRow();
		result.beforeFirst();
		return numberResults;
	}
}
