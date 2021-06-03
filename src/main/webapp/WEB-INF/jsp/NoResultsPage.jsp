<%-- 
    Document   : NoResultsPage
    Created on : Jul. 2, 2020, 12:17:22 p.m.
    Author     : Marc
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<!-- Copyright Marc Scattolin -->
<html>
    <head>
        <jsp:include page='/MetaTags.jsp' />
		
        <title>No results</title>
    </head>
    <body>
		<jsp:include page='/Navbar.jsp' />
		<div class='bodyContainer'>
			<h1>No results</h1>
			<c:if test="${requestScope['searchWarning'] != null}">
				<div class="warning"><p><c:out value="${requestScope['searchWarning']}"></c:out></p></div>
			</c:if>
			<footer><jsp:include page='/Footer.jsp' /></footer>
		</div>
    </body>
</html>