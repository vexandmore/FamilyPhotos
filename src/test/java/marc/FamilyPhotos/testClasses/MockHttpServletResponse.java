/*
 * Copyright Marc Scattolin
 */
package marc.FamilyPhotos.testClasses;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
/**
 *
 * @author Marc
 */
public class MockHttpServletResponse implements HttpServletResponse {
	private int statusCode;
	private String errorMsg;
	
	public MockHttpServletResponse(){}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getErrorMessage() {
		return errorMsg;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		this.statusCode = sc;
		this.errorMsg = msg;
	}

	@Override
	public void sendError(int sc) throws IOException {
		this.statusCode = sc;
	}
	
	
	
	
	
	@Override
	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public String encodeURL(String url) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public String encodeUrl(String url) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setStatus(int sc) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setStatus(int sc, String sm) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public int getStatus() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public String getHeader(String name) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public Collection<String> getHeaders(String name) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public Collection<String> getHeaderNames() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setCharacterEncoding(String charset) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setContentLength(int len) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setContentLengthLong(long len) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setContentType(String type) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setBufferSize(int size) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException("Not supported by mock class");
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException("Not supported by mock class");
	}
	
}
