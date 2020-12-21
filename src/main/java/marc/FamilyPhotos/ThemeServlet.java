/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marc.FamilyPhotos;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
/**
 *
 * @author Marc
 */
public class ThemeServlet extends HttpServlet {
	@Override
	public void init() throws ServletException {
	}
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		RequestDispatcher resultDispatcher = request.getRequestDispatcher("/WEB-INF/jsp/Theme.jsp");
		resultDispatcher.forward(request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String theme = request.getParameter("theme");
		Cookie themeCookie = new Cookie("theme", theme);
		themeCookie.setMaxAge(100000);
		themeCookie.setPath("/");
		response.addCookie(themeCookie);
		//cause the theme page to refresh
		RequestDispatcher resultDispatcher = request.getRequestDispatcher("/WEB-INF/jsp/Theme.jsp");
		resultDispatcher.forward(request, response);
	}
}
