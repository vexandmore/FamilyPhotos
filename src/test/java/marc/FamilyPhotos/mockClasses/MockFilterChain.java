package marc.FamilyPhotos.mockClasses;
import java.io.IOException;
import jakarta.servlet.*;
/**
 *
 * @author Marc
 */
public class MockFilterChain implements FilterChain {
	private boolean filterDone = false;
	
	public MockFilterChain(){}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		filterDone = true;
	}
	
	public boolean isFilterDone() {
		return filterDone;
	}
	
}
