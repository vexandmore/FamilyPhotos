<%-- 
    Document   : 400
    Created on : Jun. 23, 2020, 7:07:11 p.m.
    Author     : Marc
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="/FamilyPhotos/Other Resources/favicon.ico">
        <title>Error 400</title>
		<style>
			body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;}
			h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;}
			a {color:black;} .line {height:1px;background-color:#525D76;border:none;}
		</style>
    </head>
    <body>
        <h1>Error 400</h1>
		<hr />
		<p><b>Meaning</b> There is an error in your request. Go back and try searching again.</p>
		<p><b>Message</b> <%= ((java.lang.String)request.getAttribute("javax.servlet.error.message")) %></p>
    </body>
</html>
