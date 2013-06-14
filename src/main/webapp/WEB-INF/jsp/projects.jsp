<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ngs" uri="http://github.com/lindenb/ngsproject/tags"%>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title>Projects</title>
<u:head/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>


<div class="box1"><h2>Projects</h2>
<table>
<thead>
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="project" items="${ngs:filter(pageContext.request,projects)}">
		<tr>
			<td><u:project-href project="${project}"/></td>
			<td><c:out value="${project.description}" escapeXml="true"/></td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>

</body>
</html>
