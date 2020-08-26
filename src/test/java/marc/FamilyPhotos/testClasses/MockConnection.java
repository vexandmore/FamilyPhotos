/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marc.FamilyPhotos.testClasses;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
/**
 * Wrapper around a connection that doesn't close the connection when close()
 * is called; exists for MockDataSource.
 * @author Marc
 */
public class MockConnection implements Connection {
	protected Connection con;
	
	public MockConnection(Connection con) {
		this.con = con;
	}
	
	@Override
	public void close() throws SQLException {
	}
	
	@Override
	public Statement createStatement() throws SQLException {
		return con.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return con.prepareCall(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		con.setAutoCommit(autoCommit);
	}
	

	@Override
	public void commit() throws SQLException {
		con.commit();
	}
	
	@Override
	public boolean getAutoCommit() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public void rollback() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public boolean isClosed() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getCatalog() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Clob createClob() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Blob createBlob() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public NClob createNClob() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getSchema() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException("Not supported by mock class."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
