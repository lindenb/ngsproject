<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ngs" uri="http://github.com/lindenb/ngsproject/tags"%>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title>VCFs</title>
<u:head/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css?r=<%= java.lang.System.currentTimeMillis() %>"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>


<div class="box1"><h2>VCFs</h2>
<table>
<thead>
	<tr>
		<th>Path</th>
		<th>Description</th>
		<th>Reference</th>
		<th>Samples</th>
		<th>Projects</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="i" items="${ngs:filter(pageContext.request,vcfs)}">
		<tr>
			<td><u:vcf-href vcf="${i}"/></td>
			<td>
				<c:out value="${i.description}" escapeXml="true"/>
				<c:if test="${not i.indexedWithTabix}"> **NOT INDEXED WITH TABIX**</c:if>
			</td>
			<td><u:reference-href u:reference="${i.reference}"/></td>
			<td>
				<c:forEach var="j" items="${ngs:filter(pageContext.request,i.samples)}">
					<u:sample-href sample="${j}"/>
				</c:forEach>
			</td>
			<td>
				<c:forEach var="j" items="${ngs:filter(pageContext.request,i.projects)}">
					<u:project-href project="${j}"/>
				</c:forEach>
			</td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>

</body>
</html>