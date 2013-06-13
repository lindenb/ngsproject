<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ngs" uri="http://github.com/lindenb/ngsproject/tags"%>

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


<h2>Projects</h2>
<div class="box1">
<table>
<thead>
	<tr>
		<th>Projects</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="p" items="${ngs:filter(pageContext.request,sample.projects)}">
		<tr>
			<td><u:project-href project="${p}"/></td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>


<h2>BAMS</h2>
<div class="box1">
<table>
<thead>
	<tr>
		<th>Bam</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="b" items="${ngs:filter(pageContext.request,sample.bams)}">
		<tr>
			<td><u:bam-href bam="${b}"/></td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>

<h2>VCF</h2>
<div class="box1">
<table>
<thead>
	<tr>
		<th>VCF</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="v" items="${ngs:filter(pageContext.request,sample.vcfs)}">
		<tr>
			<td><u:vcf-href vcf="${v}"/></td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>


</body>
</html>