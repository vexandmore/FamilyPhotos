<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
	<head>
		<jsp:include page='/MetaTags.html' />
		
		<title>Login</title>
	</head>
	<body>
		<jsp:include page='/Navbar.jsp' />

		<div class='bodyContainer'>
			<form style='max-width: 800px;' method="POST" action='j_security_check'>
				<h1>Login</h1>
				<label for='username'><b>Username</b></label>
				<input type='text' class='login' id='username' name='j_username'/>
				<label for='password'><b>Password</b></label>
				<input type='password' class='login' id='password' name='j_password'/>
				<input type='submit' value='Login'/>
			</form>
			<footer></footer>
		</div>
	</body>
</html>
