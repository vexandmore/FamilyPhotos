<%-- 
    Document   : LogoutJSP
    Created on : Jun. 17, 2020, 10:56:39 a.m.
    Author     : Marc
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page='/MetaTags.jsp' />
		
        <title>Logged out</title>
    </head>
    <body>
		<%String username = request.getRemoteUser();%>
		<%session.invalidate();%>
		<%-- Navbar has to be explicitly included since user still appears logged in --%>
		<header>
			<h1 class="logo">Williams Family Slides</h1>
			<input type="checkbox" id="nav-toggle" class="nav-toggle">
			<nav>
				<ul>
					<li> <a href="/">Cormack Talk</a></li>
					<li> <a href="/FamilyPhotos">Login</a></li>
				</ul>
			</nav>
			<label for="nav-toggle" class="nav-toggle-label">
				<span></span>
			</label>
		</header>
		
		<div class='bodyContainer'>
			<% if (username == null) { %>
			<p>Logged out</p>
			<%} else {%>
			<p>Logged out user <%=username%></p>
			<%}%>
		</div>
		
		<footer></footer>
    </body>
</html>
