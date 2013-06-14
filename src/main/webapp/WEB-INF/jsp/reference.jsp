<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title><c:out value="${reference.name}" escapeXml="true"/></title>
<u:head/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>

<h1><c:out value="${reference.name}" escapeXml="true"/></h1>

<h2>Description</h2>
<div><c:out value="${reference.description}" escapeXml="true"/></div>
<h2>Path</h2>
<pre><c:out value="${reference.path}" escapeXml="true"/></pre>

</body>
</html>