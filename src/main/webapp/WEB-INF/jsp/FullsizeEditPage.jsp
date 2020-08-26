<%-- 
    Document   : ViewPhotoJSP
    Created on : Jun. 15, 2020, 6:03:20 p.m.
    Author     : Marc
--%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="java.net.URLEncoder"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="marc.FamilyPhotos.*" %>
<%@page import="marc.FamilyPhotos.util.*" %>
<%@page import="java.util.*" %>
<!DOCTYPE html>
<!-- Copyright Marc Scattolin -->
<html>
    <head>
		<jsp:include page='/MetaTags.html' />
		
		<title>View Photo</title>
    </head>
    <body>
		<jsp:include page='/Navbar.jsp' />
		<div class='bodyContainer'>
			<h1>View Photo</h1>

			<%FamilyPhotoDetailed photo = (FamilyPhotoDetailed) request.getAttribute("photo");%>
			<a href='<%= photo.photoPath.replaceAll(" ", "%20")%>' class="imageBox" >
				<img class="fullsizePhoto" src="<%= photo.photoPath.replaceAll(" ", "%20")%>" />
			</a><!--https://www.talisman.org/~erlkonig/misc/lunatech%5Ewhat-every-webdev-must-know-about-url-encoding/-->

			<%if (photo.tags != null) {%>
			<p style='word-break: break-all;'><b>Tags:</b> <%= photo.tags%> </p>
			<%}
			if (photo.comment != null) {%>
			<p style='word-break: break-all;'><b>Comments:</b> <%= photo.comment%> </p>
			<%}
			if (photo.decade != null) {%>
			<p style='word-break: break-all;'><b>Decade:</b> <%= photo.decade%> </p>
			<%}
			if (photo.date != null) {%>
			<p style='word-break: break-all;'><b>Date:</b> <%= photo.date%> </p>
			<%}%>
			<p style='word-break: break-all;'><b>Path:</b> <%=photo.photoPath%> </p>


			<!--This is the form used to edit photo metadata-->
			<!-- This form sends empty strings instead of nulls.-->
			<%-- this is not going to be shown since the jpegs will be updated instead--%>
			<%--
			<%if (request.isUserInRole("editor")) {%>
			<button onclick="toggleEditPhotoData()">Show/hide edit pane</button>
			<div id='editPhotoData' style="display:none;">
				<form method='POST' action='View' target="_self">
					<p>Edit photo data. Know that what you enter here will <strong>replace</strong> the metadata on the photo</p>
					<fieldset>
						<legend>Tags</legend>
						<% TagSet tags = (TagSet)(request.getAttribute("tags")); %>
						<select id="tags" name="tags" multiple size=10 required>
							<%for (TagList tagList : tags) {%>
							<optgroup label=<%= tagList.category %>>
								<%for (Tag tag: tagList) {%>
								<option value= <%=tag.tagName %>
										<% if (photo.tags.matches(".*" + tag.tagName + ".*")) {%>
										selected  <% //Pre-select the items corresponding to the tags the image already has %>
										<%}%>
										>
									<%= tag.displayName %>
								</option>
								<%}%>
							</optgroup>
							<%}%>
						</select>
						<br/>
						Multiple can be selected. On a desktop, use <strong>ctrl</strong>.
						<br/>
					</fieldset>

				<fieldset>
					<legend>Comments</legend>
					<textarea name="comment" placeholder="enter comments"><%if (photo.comment != null) {%><%= photo.comment%><%}%></textarea>
				</fieldset>


				<fieldset>
					<legend>Dates</legend>
					<select name="decade">
						<option value=''>No decade</option>
						<%if (photo.decade == null) {
							photo.decade = "";
						}%> <!-- Ensure decade isn't null so the comparisons below work-->
						<option value="1950s" <%if (photo.decade.equals("1950s")) {%> selected <%}%> >1950s</option>
						<option value="1960s" <%if (photo.decade.equals("1960s")) {%> selected <%}%> >1960s</option>
						<option value="1970s" <%if (photo.decade.equals("1970s")) {%> selected <%}%> >1970s</option>
						<option value="1980s" <%if (photo.decade.equals("1980s")) {%> selected <%}%> >1980s</option>
					</select>
				</fieldset>
				<br/>
				<input type='submit' value='Set photo information' />
				<input type='hidden' name='UUID' value=<%= photo.UUID%> />
			</form>
			<%}%>
		</div>
			--%>
			<footer></footer>
		</div>
		<script>
			/*
			var editPhotoData = document.getElementById("editPhotoData");
			function toggleEditPhotoData() {
				if (editPhotoData.style.display === "none") {
				editPhotoData.style.display = "block";
				window.scrollBy(0,500);
				} else {
					editPhotoData.style.display = "none";
				}
			}*/
		</script>		
    </body>
</html>
