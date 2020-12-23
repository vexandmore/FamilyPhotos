<%-- 
    Document   : Theme
    Created on : Dec. 21, 2020, 9:22:43 a.m.
    Author     : Marc
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page='/MetaTags.jsp' />
        <title>Change Theme</title>
    </head>
    <body>
		<jsp:include page='/Navbar.jsp' />
		<div class="bodyContainer">
			<h1>Set Theme</h1>
			<form>
				<input type="radio" name="theme" value="Normal" id="Normal">
				<label for="Normal">Normal</label>
				<input type="radio" name="theme" value="Holiday" id="Holiday">
				<label for="Holiday">Holiday</label>
				<button class="stdButton" type="button" onclick="setTheme()">Set</button>
			</form>
			<footer><jsp:include page='/Footer.jsp' /></footer>
		</div>
		<script>
			function setTheme() {
				if (document.getElementById('Holiday').checked) {
					document.cookie = "theme = Holiday; expires=Fri, 31 Dec 9999 23:59:59 GMT";
				} else {
					document.cookie = "theme = Normal; expires=Fri, 31 Dec 9999 23:59:59 GMT";
				}
				window.location.reload(false);
			}
		</script>
		<script>
			showCurrentTheme();
			function showCurrentTheme() {
				try {
					var themeValue = document.cookie.split('; ')
							.find(row=> row.startsWith('theme'))
							.split('=')[1];
					document.getElementById(themeValue).checked = true;
				} catch (err) {
					console.log(err);
					document.getElementById('Normal').checked = true;
				}
			}
		</script>
    </body>
</html>
