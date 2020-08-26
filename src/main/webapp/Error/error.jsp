<%-- 
    Document   : error
    Created on : Jun. 24, 2020, 9:31:09 a.m.
    Author     : Marc
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="/FamilyPhotos/Other Resources/favicon.ico">
        <title>Error <%= (String)request.getParameter("javax.servlet.error.status_code") %></title>
		<style>
			body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;}
			h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;}
			a {color:black;} .line {height:1px;background-color:#525D76;border:none;}
		</style>
    </head>
    <body>
        <h1>Error <%= (String)request.getParameter("javax.servlet.error.status_code") %></h1>
		<hr/>
		<p><b>Meaning</b> An unusual error has occurred.</p>
    </body>
</html>
