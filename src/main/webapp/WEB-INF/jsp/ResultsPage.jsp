<%-- 
    Document   : ResultsJSP
    Created on : Jun. 15, 2020, 6:32:26 p.m.
    Author     : Marc
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="marc.FamilyPhotos.*" %>
<%@page import="marc.FamilyPhotos.util.*" %>
<%@page import="java.util.*" %>
<!DOCTYPE html>
<!-- Copyright Marc Scattolin -->
<html>
    <head>
		<link rel="stylesheet" href="css/resultsStyles.css" />
        <jsp:include page='/MetaTags.html' />
		
		<script src="Javascript/url.min.js" defer></script>
		<script src="Javascript/Scroll.js" defer></script>
		<script src="/FamilyPhotos/Javascript/ResultsPage.js" defer></script>
        <title><%=request.getAttribute("title")%></title>
    </head>
    <body>
		<div id="overlay"></div>

		<div id='frameHolder'>
			<div id="topControls">
				<button type='button' onclick='imageFrame.navigateFullPage();' class='stdButton' id='fullsizeButton'></button>
				<button type='button' onclick='imageFrame.frameClose();' class='warnButton' id='closeButton'></button>
			</div>
			<div id="main">
				<!--<button type='button' onclick='alert("an error has occurred")' class='invisible leftButton' id='leftInvisibleButton'></button>-->
				<button type='button' onclick='imageFrame.previousPhoto()' class='stdButton2 leftButton' id='leftArrow'>&larr;</button>
				<button type='button' onclick='imageFrame.navigatePreviousPage()' class='warnButton leftButton' id='leftPageArrow'>&lsaquo;</button>
				<h1 id="error" style="display:none;"> </h1>
				<h1 id="loading" style="display:none;">Loading...</h1>
				<img id="image" src="" onclick="" style="cursor: pointer;"/>
				<div id='infoPane'>
					<p id="tags"><b>Tags: </b> </p>
					<p id="comment"><b>Comments: </b> </p>
					<p id="decade"><b>Decade: </b> </p>
					<p id="date"><b>Date: </b> </p>
					<p id="path"><b>Path: </b> </p>
					<% boolean isEditor = request.isUserInRole("editor"); %>
					<button type='button' onclick='addToCollection.open([imageFrame.currentPhotoId])' class='stdButton2'>Add to collection</button>
				</div>
				<!--<button type='button' onclick='alert("an error has occurred")' class='invisible rightButton' id='rightInvisibleButton'></button>-->
				<button type='button' onclick='imageFrame.nextPhoto()' class='stdButton2 rightButton' id='rightArrow'>&rarr;</button>
				<button type='button' onclick='imageFrame.navigateNextPage()' class='warnButton rightButton' id='rightPageArrow'>&rsaquo;</button>
			</div>
		</div>

					
					
		<form id='addToCollection' action='Collections' method='POST' autocomplete='off' target="response">
			<p>Select a collection in which to add the <span id='numberPhotos'></span> photo(s):</p>
			
			<span id="collectionRadioButtons"></span>

			<label> <input type="radio" value="" id="userCollectionName" name="collectionName"></label>
			<input type='text' onchange='document.getElementById("userCollectionName").value=this.value'
				   onclick='document.getElementById("userCollectionName").checked = "true"'
				   name='collectionName' placeholder="Create a new collection">
			
			<input type='hidden' name='action' value='addPhoto' />
			<span style="display:none;" id="photoIDs"></span>
			<br />
			<iframe name="response" id='response' src='/FamilyPhotos/MakeSelection.html'></iframe>
			<button type='button' onclick='addToCollection.close()' class='warnButton'>Close</button>
			<button type='submit' class='stdButton'>Submit</button>
		</form>


		<jsp:include page='/Navbar.jsp' />
		<div class='bodyContainer'>

			<h1><%=request.getAttribute("title")%></h1>
			<div class="navbar">
				<a href='<%=request.getAttribute("returnLink")%>' id="search">&lsaquo; Search</a>
				<a href='<%= request.getAttribute("previousLink")%>' id='previous' >&larr;Previous</a>
				<a href='<%= request.getAttribute("nextLink")%>' id='next' >Next&rarr;</a>
			</div>

			<button type='button' onclick='addToCollection.open(addToCollection.checkedPhotos())' class='stdButton'>Add checked to a collection</button>
			<button type='button' onclick='addToCollection.checkAll()' class='stdButton'>Check all</button>
			<button type='button' onclick='addToCollection.uncheckAll()' class='stdButton'>Uncheck all</button>
			<p>Click on a photo for the fullsize version and more details. Slides can be added to a collection by clicking their checkboxes and clicking
			"Add checked to a collection"</p>
			
			<div id="thumbnailContainer">	
				<% ArrayList<FamilyPhoto> photos; %>
				<%photos = (ArrayList<FamilyPhoto>) request.getAttribute("photos");%>
				<%for (FamilyPhoto photo : photos) {%>
				<div class='thumbnailDiv' id="<%=photo.UUID%>">
					<img src='<%=photo.getEncodedThumbnailPath()%>' class='thumbnail' onclick='imageFrame.showFullsize("<%=photo.UUID%>")' />
					<br />
					<input type='checkbox' onclick='this.parentElement.classList.toggle("checked");'/>
				</div>
				<%}%>
			</div>
			<br/>
			<footer>Some icons made by <a href="https://icon54.com/" title="Pixel perfect">Pixel perfect</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
			</footer>
		</div>
	</body>
</html>
