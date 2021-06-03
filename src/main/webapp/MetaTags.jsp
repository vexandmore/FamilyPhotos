<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<c:choose>
	<c:when test="${cookie.theme.value == 'Holiday'}">
		<link rel="stylesheet" href="/FamilyPhotos/css/HolidayStyles.css" />
		<link rel="stylesheet" href="/FamilyPhotos/css/holidayResultsStyles.css" />
	</c:when>
	<c:otherwise>
		<link rel="stylesheet" href="/FamilyPhotos/css/styles.css" />
		<link rel="stylesheet" href="/FamilyPhotos/css/resultsStyles.css" />
	</c:otherwise>
</c:choose>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="apple-touch-icon" sizes="180x180" href="/FamilyPhotos/OtherResources/apple-touch-icon.png">
<link rel="icon" type="image/png" sizes="32x32" href="/FamilyPhotos/OtherResources/favicon-32x32.png">
<link rel="icon" type="image/png" sizes="16x16" href="/FamilyPhotos/OtherResources/favicon-16x16.png">
<link rel="manifest" href="/FamilyPhotos/OtherResources/site.webmanifest">
<link rel="mask-icon" href="/FamilyPhotos/OtherResources/safari-pinned-tab.svg" color="#5bbad5">
<link rel="shortcut icon" href="/FamilyPhotos/OtherResources/favicon.ico">
<meta name="msapplication-TileColor" content="#da532c">
<meta name="msapplication-config" content="/FamilyPhotos/OtherResources/browserconfig.xml">
<meta name="theme-color" content="#ffffff">