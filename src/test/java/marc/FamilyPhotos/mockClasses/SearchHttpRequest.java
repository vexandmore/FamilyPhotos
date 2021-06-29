package marc.FamilyPhotos.mockClasses;
import java.util.*;

/**
 * Mock HttpServletRequest to test SearchServlet.
 * @author Marc
 */
public class SearchHttpRequest extends MockHttpRequest {
	private final Map<String, String[]> parameters;
	private final Collection<String> userRoles;
	private final String queryString;
	private final String requestURI;
	
	public SearchHttpRequest(Map<String, String[]> parameters, Collection<String> userRoles, String queryString) {
		this.parameters = parameters;
		this.userRoles = userRoles;
		this.queryString = queryString;
		this.requestURI = "";
	}
	
	public SearchHttpRequest(Collection<String> userRoles, String requestURI) {
		this.userRoles = userRoles;
		this.requestURI = requestURI;
		this.parameters = new HashMap<>();
		this.queryString = "";
	}
	
	@Override
	public String getRequestURI() {
		return requestURI;
	}
	
	@Override
	public String getParameter (String key) {
		String[] temp = parameters.get(key);
		if (temp == null)
			return null;
		else
			return temp[0];
	}
	
	@Override
	public String[] getParameterValues (String key) {
		return parameters.get(key);
	}
	
	@Override
	public boolean isUserInRole(String role) {
		return userRoles.contains(role);
	}
	
	@Override
	public String getQueryString() {
		return queryString;
	}
}
