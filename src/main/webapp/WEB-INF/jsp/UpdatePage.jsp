<%-- 
    Document   : UpdatePage
    Created on : Jun. 28, 2020, 11:40:39 a.m.
    Author     : Marc
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<!-- Copyright Marc Scattolin -->
<html>
    <head>
		<jsp:include page='/MetaTags.jsp' />
        <title>Update database</title>
    </head>
    <body>	
		<jsp:include page='/Navbar.jsp' />
		<div class='bodyContainer'>
			<h1>Update</h1>
			<hr />
			<div class="leftIndented">
				<button type="button" onclick="updateDB()" class='stdButton'>Update Database</button>
				<pre style="font-size: 16px;" id="response"> </pre>
				<br />

				<%--<button type="button" onclick="updateCache()" class='stdButton'>Update Cache</button>
				<br /> --%>

				<c:set var="tags" value='${requestScope["tags"]}'/><%--This is a TagSet--%>
				<form method='POST' style="margin-left:0; display:inline-block; margin-bottom: 8px;" autocomplete="off">
					<h3>Add Tag To Those Visible To Limited User</h3>
					<input type='hidden' name='type' value='addTagToWhitelist'/>
					<select name='tagName' required>
						<option disabled selected value>--Select a tag--</option>
						<c:forEach var="tagList" items="${tags.iterator}">
							<optgroup label='<c:out value="${tagList.category}"/>'>
								<c:forEach var="tag" items="${tagList.iterator}">
									<option><c:out value="${tag.tagName}"/></option>
								</c:forEach>
							</optgroup>
						</c:forEach>
					</select>
					<input type='submit' value='Submit'>
				</form>

				<form method='POST' style="margin-left:0; margin-bottom: 8px;" autocomplete="off">
					<h3>Add Tag</h3>
					<input type='hidden' name='type' value='addTag'/>
					<label for='tagName'>Internal Tag Name</label>
					<input name="tagName" id='tagName' required>
					<label for='displayName'>Displayed Name</label>
					<input name="displayName" id='displayName' required>
					<label for='category'>Category</label>
					<select name="category" id='category' required>
						<option disabled selected value>--Select a category--</option>
						<option>People</option>
						<option>Places</option>
						<option>Logging</option>
						<option>Other</option>
						<option>Subject</option>
					</select>
					<br/>
					<input type='submit' value='Submit'>
				</form>
			</div>



			<h1>View</h1>
			<hr />
			<div class="leftIndented">
				<h2>Slide Collections</h2>
				<c:set var="collections" value='${requestScope["collections"]}'/><%--This is a Collection<SlideCollection>--%>
				<table>
					<tr>
						<th>Name</th>
						<th>Number of photos</th>
						<th>Action</th>
					</tr>
					<c:forEach var="collection" items="${collections}">
						<tr>
							<td class="column1"><c:out value="${collection.collectionName}"/></td>
							<td class="column2a"><c:out value="${collection.numberElements}"/></td>
							<td class="column3"><button type='button' onclick='deleteCollection("<c:out value="${collection.collectionName}"/>")' class='warnButton'>Delete</button></td>
						</tr>
					</c:forEach>
				</table>

				<h2>Tags Visible To Limited User</h2>
				<c:set var="tagsWhitelist" value='${requestScope["tagsWhitelist"]}'/><%--This is a TagSet--%>
				<table>
					<tr>
						<th>Internal Tag Name</th>
						<th>Displayed Name</th>
						<th>Category</th>
					</tr>
					<c:forEach var="tagList" items="${tagsWhitelist.iterator}">
						<c:forEach var="tag" items="${tagList.iterator}">
							<tr>
								<td class="column1"><c:out value="${tag.tagName}"/></td>
								<td class="column2"><c:out value="${tag.displayName}"/></td>
								<td class="column3"><c:out value="${tagList.category}"/></td>
							</tr>
						</c:forEach>
					</c:forEach>
				</table>
				

				<h2>All Tags</h2>
				<table>
					<tr>
						<th>Internal Tag Name</th>
						<th>Displayed Name</th>
						<th>Category</th>
					</tr>
					<c:forEach var="tagList" items="${tags.iterator}">
						<c:forEach var="tag" items="${tagList.iterator}">
							<tr>
								<td class="column1"><c:out value="${tag.tagName}"/></td>
								<td class="column2"><c:out value="${tag.displayName}"/></td>
								<td class="column3"><c:out value="${tagList.category}"/></td>
							</tr>
						</c:forEach>
					</c:forEach>
				</table>
			</div>

			<footer><jsp:include page='/Footer.jsp' /></footer>
		</div>
		<script>
			function updateDB() {
				let responseTag = document.getElementById('response');
				responseTag.style.display = "inline-block";
				responseTag.style.width = "100%";
				responseTag.innerHTML = "Please wait...";
				
				var request = new XMLHttpRequest();
				request.onreadystatechange = function () {
					if (this.readyState === 4 && this.status === 200) {
						var loginTest = /<form/;
						if (loginTest.test(this.responseText)) {
							responseTag.innerHTML = "Error: you seem to be logged out. Try refreshing the page.";
						} else {
							responseTag.innerHTML = this.responseText;	
						}
					} else if (this.status >= 500) {
						responseTag.innerHTML = "Server error";
					} else if (this.status >= 400) {
						responseTag.innerHTML = "An error has occurred. Try logging out and logging in again.";
					}
				};
				request.open("POST", "/FamilyPhotos/Update?type=updateDB", true);
				request.send();
			}
			
			<%--function updateCache() {
				var request = new XMLHttpRequest();
				request.onreadystatechange = function() {
					if (this.readyState === 4) {
						if (this.status == 200) {
							var loginTest = /<form/;
							if (loginTest.test(this.responseText)) {
								alert ('Error: you seem to be logged out. Try logging out and logging in again.');
							} else {
								alert(this.responseText);
								location.reload();
							}
						} else if (this.status >= 400) {
							alert('Cache update failed');
						}
					}
				};
				request.open("POST", "/FamilyPhotos/Update?type=updateCache");
				request.send();
			}--%>
			
			function deleteCollection(collectionName) {
				if (!confirm('Really delete collection ' + collectionName + '?'))
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
