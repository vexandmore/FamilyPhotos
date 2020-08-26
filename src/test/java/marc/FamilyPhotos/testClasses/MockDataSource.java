/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marc.FamilyPhotos.testClasses;
import java.io.PrintWriter;
import javax.sql.*;
import java.sql.*;
import java.util.logging.Logger;
/**
 * Implementation of DataSource that holds one db connection. Not suitable for
 * multi-threaded environment (obviously). 
 * @author Marc
 */
public class MockDataSource implements DataSource {
	private String dbLocation, username, password;
	private MockConnection cachedConnection;
	
	public MockDataSource(String url, String username, String password)
			throws SQLException {
		cachedConnection = new MockConnection(DriverManager.getConnection(url, username, password));
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return cachedConnection;
	}

	public void closeConnection() throws SQLException {
		cachedConnection.close();
	}	
	
	
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); 
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class.");
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class.");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class.");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); 
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException("Not supported by mock class."); 
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class.");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class.");
	}
	
}
