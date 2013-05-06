<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title>TabixViz</title>
<u:head/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css?r=<%= java.lang.System.currentTimeMillis() %>"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />

<div class="box1"><h2>Projects</h2>
<table>
<thead>
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="project" items="${projects}">
		<tr>
			<td><a href="${pageContext.request.contextPath}/ngsprojects/project/${project.id}"><c:out value="${project.name}" escapeXml="true"/></a></td>
			<td><c:out value="${project.description}" escapeXml="true"/></td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>

</body>
</html>