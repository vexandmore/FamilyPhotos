<header>
	<h1 class="logo">Williams Family Slides</h1>
	<input type="checkbox" id="nav-toggle" class="nav-toggle">
	<nav>
		<ul>
			<li> <a href="/">Cormack Talk</a></li>
			<% if (request.getUserPrincipal() == null) { %>
				<li> <a href="/FamilyPhotos" id="navLogin">Login</a></li>
			<%} else { %>
				<li> <a href="/FamilyPhotos" id="navSearch">Search for slides</a></li>
				<% if (request.isUserInRole("editor")) { %>
					<li> <a href='/FamilyPhotos/Update' id="navUpdate">Update database</a> </li>
					<li> <a href='/FamilyPhotos/Collections?action=manageCollections' id="navManage">Manage Collections</a> </li>
				<%} else {%>
					<li> <a href='/FamilyPhotos/Collections?action=manageCollections' id="navManage">Manage Collections</a> </li>
				<%}%>
				<li><a href='/FamilyPhotos/Theme.jsp' id="navTheme">Theme</a></li>
				<li> <a href='/FamilyPhotos/Login/LogoutJSP.jsp' id="navLogout">Logout</a></li>
			<%}%>
		</ul>
	</nav>
	<label for="nav-toggle" class="nav-toggle-label">
		<span></span>
	</label>
</header>