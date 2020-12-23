<%-- 
    Document   : index
    Created on : Jun. 15, 2020, 8:24:06 p.m.
    Author     : Marc
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<!-- Copyright Marc Scattolin -->
<html lang="en">
	<head>
		<title>Search for slides</title>
		<jsp:include page='/MetaTags.jsp' />
	</head>
	<body>
		<jsp:include page='/Navbar.jsp' />
		<div class='bodyContainer'>
			<form id='dummy' style='display:none;'></form>
			<div id='overlay'></div>
			<!-- Help panes -->
			<div class='Help' id='tagHelp'>
				<p>
					The slides were labeled with tags. The tags themselves are categorized into people,
					places, logging, subject, and other (special tags like untagged photo).
				</p>
				<button onclick='helpToggle("close","tagHelp")' type='button' class='stdButton'>Close</button>
			</div>
			<div class='Help' id='dateHelp'>
				<p>
					Slides that have a date printed on them have that information added to the scans.
					For slides that don't have a visible printed date, the decade the slide was 
					most likely taken in is added instead. </p>
				<p>The printed date on a slide may appear to contradict the contents of the photo 
					(like a photo clearly taken in winter with a date in July); I assume this is
					because that date corresponds to the date the film was developed.</p>
				<button onclick='helpToggle("close","dateHelp")' type='button' class='stdButton'>Close</button>
			</div>
			<div class='Help' id='boxHelp'>
				<p>
					The slides were organized by what container they were physically in; this allows
					you to include that in your search. Many of the slides were in unlabeled
					plastic boxes, so placeholder names (C1, C2, etc) were used in that case.</p>
				<button onclick='helpToggle("close","boxHelp")' type='button' class='stdButton'>Close</button>
			</div>
			<div class='Help' id='commentHelp'>
				<p>
					Some slides had handwritten comments on them; this option
					allows you to search by those.</p>
				<button onclick='helpToggle("close","commentHelp")' type='button' class='stdButton'>Close</button>
			</div>
			<div class='Help' id='sortHelp'>
				<p>Ordering by date means the slides will be, in order:</p>
				<ol>
					<li>Slides without date information</li>
					<li>Slides with only a decade, in chronological order</li>
					<li>Slides with a full date, in chronological order</li>
				</ol>
				<p>
					Ties are resolved based on the order the slide was scanned.</p>
				<p>
					Ordering by box means the slides will be in alphabetical order of 
					their filename. In practice this means that slides in the same
					box will be together in the order they were scanned.</p>
				<button onclick='helpToggle("close","sortHelp")' type='button' class='stdButton'>Close</button>
			</div>

			<h1>Search for slides</h1>

			<p>Select your search options below. Check a box to add a criteria; each of these 
				<strong>narrow</strong> the search, meaning that if they are all unchecked,
				you'll see all of the slides.
				Also know that it is possible to change their ordering.</p>
			<form action="Search" method="get" autocomplete='off'>
				<!--These checkboxes enable or disable one of the three fieldsets below.-->
				<p style='margin-bottom: 3px; margin-top: 3px'>In search, include...</p>
				<input type="checkbox" id="byTags" name="dummy" form="dummy" value="tags" onclick='toggleFieldset(this.checked, "tagSection")' checked/>
				<label for="byTags">Tags</label>
				<br/>
				<input type='checkbox' id='byCollection' name="dummy" form="dummy" value='' onclick='toggleFieldset(this.checked, "collectionSection")'/>
				<label for='byCollection'>A slide collection</label>
				<br/>
				<input type="checkbox" id="byDates" name="dummy" form="dummy" value="" onclick='toggleFieldset(this.checked, "dateSection")'/>
				<label for="byDates">Date range</label>
				<br/>
				<input type="checkbox" id="byComments" name="dummy" form="dummy" value="" onclick='toggleFieldset(this.checked, "commentSection")'/>
				<label for="byComments">Comments</label>
				<br/>
				<input type='checkbox' id='byBoxes' name="dummy" form="dummy" value='' onclick='toggleFieldset(this.checked, "boxSection")'/>
				<label for='byBoxes'>The slide's box</label>
				<br/>

				<fieldset id="tagSection">
					<legend>Tags <button onclick='helpToggle("open", "tagHelp")' type='button'>?</button></legend>
					
					
					<div style='display:flex; flex-wrap:wrap;'>
						<c:set var="tags" value='${requestScope["tags"]}' />
						<c:forEach items="${tags.iterator}" var="tagList">
							<div>
								<label> <b><c:out value="${tagList.category}"/></b>
									<br />
									<select id="<c:out value="${tagList.category}"/>" name="tags" multiple size="5">
										<c:forEach var="tag" items="${tagList.iterator}">
											<option value="<c:out value="${tag.tagName}"/>"><c:out value="${tag.displayName}"/></option>
										</c:forEach>
									</select>
								</label>
							</div>
						</c:forEach>
					</div>
					
					Multiple can be selected. On a desktop, use <strong>ctrl</strong>.
					<br/>
					<input id="tagsAnd" type="radio" name="tagsBoolean" value="and"/>
					<label for="tagsAnd">Photos must have <strong>all</strong></label>
					<br/>
					<input id="tagsOr" type="radio" name="tagsBoolean" value="or" checked/>
					<label for="tagsOr">Photos must have <strong>some</strong></label>
				</fieldset>

				<fieldset id='collectionSection'>
					<legend>Collection</legend>
					<select name='collections' required>
						<c:set var="collections" value='${requestScope["collections"]}' />
						<c:forEach items="${collections}" var="collection">
							<option value='<c:out value="${collection.collectionName}"/>'> <c:out value="${collection.collectionName}"/> </option>
						</c:forEach>
					</select>
				</fieldset>


				<fieldset id="dateSection">
					<legend>Dates <button onclick='helpToggle("open", "dateHelp")' type='button'>?</button></legend>

					<!-- These radio buttons are used by JS so no value need be sent (although they must have a matching name for them to be radio buttons) -->
					<input  type="radio" id="byDecade" name="dateType" value="" onclick="toggleDateInput()" checked/>
					<label for="byDecade">Search by decade</label>
					<input  type="radio" id="byMonth" name="dateType" value="" onclick="toggleDateInput()"/>
					<label for="byMonth">Search by month</label>
					<br/>
					<span id="monthSection">
						<label for="startdate">Start date</label>
						<input type="date" id="startDate" name="startdate" min="1960-01-01" max="1985-12-01" value="1960-01-01" required/>
						<label for="enddate">End date</label>
						<input type="date" id="endDate" name="enddate" min="1960-01-01" max="1985-12-01" value="1985-12-01" required/>
					</span>
					<span id="decadeSection">
						<select id="decades" name="decades" multiple size=4 required>
							<option value="1950s">1950s</option>
							<option value="1960s">1960s</option>
							<option value="1970s">1970s</option>
							<option value="1980s">1980s</option>
						</select>
					</span>
				</fieldset>

				<fieldset id="commentSection">
					<legend>Comments <button onclick='helpToggle("open", "commentHelp")' type='button'>?</button></legend>
					<textarea name="comments" placeholder="enter space-separated keywords" required></textarea>
					<br/><input id="commentsAnd" type="radio" name="commentsBoolean" value="and"/>
					<label for="commentsAnd">Photos must have <strong>all</strong></label>
					<br/>
					<input id="commentsOr" type="radio" name="commentsBoolean" value="or" checked/>
					<label for="commentsOr">Photos must have <strong>some</strong></label>
				</fieldset>

				<fieldset id='boxSection'>
					<legend>Box <button onclick='helpToggle("open", "boxHelp")' type='button'>?</button></legend>
					<select name='box'>
						<c:set var="folders" value='&{requestScope["folders"]}' />
						<c:forEach var="folder" items="${folders}">
							<option value="<c:out value="${folder}"/>"><c:out value="${folder}"/></option>
						</c:forEach>
					</select>
				</fieldset>

				<fieldset>
					<legend>Ordering <button onclick='helpToggle("open", "sortHelp")' type='button'>?</button></legend>
					<select name="sorting">
						<option value="date" checked >By date</option>
						<option value="alphabetical">By box</option>
					</select>
				</fieldset>

				<input type="submit" value="Search"/>
			</form>

			<footer><jsp:include page='/Footer.jsp' /></footer>
		</div>
		<script src="Javascript/Scroll.js"></script>
		<script>
			//run at page load to ensure proper sections are shown
			toggleFieldset(document.getElementById("byTags").checked, "tagSection");
			toggleFieldset(document.getElementById("byComments").checked, "commentSection");
			toggleFieldset(document.getElementById("byDates").checked, "dateSection");
			toggleFieldset(document.getElementById("byBoxes").checked, "boxSection");
			toggleFieldset(document.getElementById("byCollection").checked, "collectionSection");
			toggleDateInput();
			
			var overlay = document.getElementById("overlay");
			
			function helpToggle(action, helpID) {
				var helptext = document.getElementById(helpID);
				if (action === "open") {
					scroll.freeze();
					overlay.style.display = "block";
					helptext.style.display = "block";
				} else {
					overlay.style.display = "none";
					helptext.style.display = "none";
					scroll.unfreeze();
				}
			}
			function toggleFieldset(boxChecked, fieldsetID) {
				var fieldset = document.getElementById(fieldsetID);
				if (!boxChecked) {
					fieldset.setAttribute("disabled", "");
					fieldset.style.display = "none";
				} else {
					fieldset.removeAttribute("disabled");
					fieldset.style.display = "block";
				}
			}
			function toggleDateInput() {
				var decadeRadio = document.getElementById("byDecade");
				var monthRadio = document.getElementById("byMonth");
				var monthSection = document.getElementById("monthSection");
				var decadeSection = document.getElementById("decadeSection");
				
				var startDate = document.getElementById("startDate");
				var endDate = document.getElementById("endDate");
				var decades = document.getElementById("decades");
				if (monthRadio.checked) {
					decades.setAttribute("disabled", "");
					decadeSection.style.display = "none";
					startDate.removeAttribute("disabled", "");
					endDate.removeAttribute("disabled", "");
					monthSection.style.display = "inline";
				}
				if (decadeRadio.checked) {
					monthSection.style.display = "none";
					startDate.setAttribute("disabled", "");
					endDate.setAttribute("disabled", "");
					decades.removeAttribute("disabled");
					decadeSection.style.display = "inline";
				}
			}
		</script>
		<!--https://www.w3schools.com/jsref/met_element_setattribute.asp-->
		<!--https://stackoverflow.com/questions/11984879/how-can-i-make-a-multiple-required-select-option-in-html5-->
	</body>
</html>