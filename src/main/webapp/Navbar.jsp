<header>
	<h1 class="logo">Williams Family Slides</h1>
	<input type="checkbox" id="nav-toggle" class="nav-toggle">
	<nav>
		<ul>
			<li> <a href="/">Cormack Talk</a></li>
			<% if (request.getUserPrincipal() == null) { %>
				<li> <a href="/FamilyPhotos">Login</a></li>
			<%} else { %>
				<li> <a href="/FamilyPhotos">Search for slides</a></li>
				<% if (request.isUserInRole("editor")) { %>
					<li> <a href='/FamilyPhotos/Update'>Update database</a> </li>
				<%} else {%>
					<li> <a href='/FamilyPhotos/Collections?action=manageCollections'>Manage Collections</a> </li>
				<%}%>
				<li><a href='/FamilyPhotos/Theme'>Theme</a></li>
				<li> <a href='/FamilyPhotos/Login/LogoutJSP.jsp'>Logout</a></li>
			<%}%>
		</ul>
	</nav>
	<label for="nav-toggle" class="nav-toggle-label">
		<span></span>
	</label>
</header>