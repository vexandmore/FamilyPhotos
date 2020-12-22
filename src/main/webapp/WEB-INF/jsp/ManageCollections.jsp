<%-- 
    Document   : ManageCollections
    Created on : Jul. 26, 2020, 4:24:59 p.m.
    Author     : Marc
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="marc.FamilyPhotos.*" %>
<%@page import="marc.FamilyPhotos.util.*" %>
<%@page import="java.util.*" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
		<jsp:include page='/MetaTags.jsp' />
        <link rel="stylesheet" href="css/resultsStyles.css" />
        <title>Manage Collections</title>
    </head>
    <body>
		<jsp:include page='/Navbar.jsp' />
		<div class='bodyContainer'>

			<h1>Slide Collections</h1>
			<%-- <% Collection<SlideCollection> collections = (Collection<SlideCollection>) request.getAttribute("collections"); %> --%>
			<c:set var="collections" value='${requestScope["collections"]}' />
			<table>
				<tr>
					<th>Name</th>
					<th>Number of photos</th>
					<th>Action</th>
				</tr>
				<%--<% for (SlideCollection collection : collections) {%>--%>
				<c:forEach items = "${collections}" var="collection">
				<tr>
					<td class="column1"><c:out value="${collection.collectionName}"/></td>
					<td class="column2a"><c:out value="${collection.numberElements}" /></td>
					<td class="column3"><button type='button' onclick='deleteCollection("<c:out value="${collection.collectionName}"/>")' class='warnButton'>Delete</button></td>
				</tr>
				</c:forEach>
				<%--<%}%>--%>
			</table>
		</div>
		<script>
			function deleteCollection(collectionName) {
				if (!confirm('Really delete collection ' + collectionName + '? This action cannot be undone.'))
					return;
				var request = new XMLHttpRequest();
				request.onreadystatechange = function() {
					if (this.readyState === 4) {
						if (this.status === 200) {
							var loginTest = /<form/;
							if (loginTest.test(this.responseText)) {
								alert ('Error: you seem to be logged out. Try logging out and logging in again.');
							} else {
								alert(this.responseText);
								location.reload();
							}
						} else {
							alert(this.responseText);
						}
					}
				};
				request.open("POST", "/FamilyPhotos/Collections?action=deleteCollection&collectionName=" + collectionName);
				request.send();
			}
		</script>
    </body>
</html>
