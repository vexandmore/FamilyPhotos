<%@page import="java.util.concurrent.*" %>
<%! private static ConcurrentHashMap<String, Long> attempts = new ConcurrentHashMap<>(); %>
<%! private static long delayTime = 300; %>
<%! private static long maxtimeBetweenLogins = 500; %>
<%!
static {
	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	executor.scheduleAtFixedRate(() -> cleanupHashmap(), 0, 5, TimeUnit.SECONDS);
}
private static void cleanupHashmap() {
	System.out.println("in cleanup function " + attempts.size());
	if (attempts.size() > 100) {
		long currentTime = System.currentTimeMillis();
		attempts.forEach((ip, time) -> {
			if (currentTime - time > maxtimeBetweenLogins) {
				attempts.remove(ip);
			}
		});
	}
}
%>

<%
String address = request.getRemoteAddr();
Long value = attempts.get(address);
if (value != null) {
	if (System.currentTimeMillis() - value < maxtimeBetweenLogins) {
		TimeUnit.MILLISECONDS.sleep(delayTime);
	}
}
attempts.put(address, System.currentTimeMillis());

%>

<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
	<head>
		<jsp:include page='/MetaTags.jsp' />
		
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
