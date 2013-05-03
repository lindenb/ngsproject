<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title>TabixViz</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css?r=<%= java.lang.System.currentTimeMillis() %>"/>

</head>
<body>
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