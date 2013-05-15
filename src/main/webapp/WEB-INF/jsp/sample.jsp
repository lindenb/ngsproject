<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title><c:out value="${sample.name}" escapeXml="true"/></title>
<u:head/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css?r=<%= java.lang.System.currentTimeMillis() %>"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>

<h1><c:out value="${sample.name}" escapeXml="true"/></h1>

<h2>Projects/Bams</h2>
<div class="box1">
<table>
<thead>
	<tr>
		<th>Project</th>
		<th>Bam</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="b" items="${sample.bams}">
	<c:forEach var="p" items="${b.projects}">
		<tr>
			<td><u:project-href project="${p}"/></td>
			<td><u:bam-href project="${p}" bam="${b}"/></td>
		</tr>
	</c:forEach>
	</c:forEach>
</tbody>
</table>
</div>

</body>
</html>