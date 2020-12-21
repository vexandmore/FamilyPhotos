<%-- 
    Document   : Theme
    Created on : Dec. 21, 2020, 9:22:43 a.m.
    Author     : Marc
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page='/MetaTags.html' />
        <title>Change Theme</title>
    </head>
    <body>
		<jsp:include page='/Navbar.jsp' />
        <h1>Set Your Theme</h1>
		<form action="Theme" method="post">
			<input type="radio" name="theme" value="Normal" id="Normal">
			<label for="Normal">Normal</label>
			<input type="radio" name="theme" value="Holiday" id="Holiday">
			<label for="Holiday">Holiday</label>
			<button>Set</button>
		</form>
    </body>
</html>
