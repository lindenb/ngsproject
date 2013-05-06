<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title><c:out value="${project.name}" escapeXml="true"/></title>
<u:head/>

<c:set var="stylenames" value="alignstyle1,alignstyle2,alignqual,aligntt,aligncigar,alignorient,alignxinpair,alignmapped"/>
<c:forTokens items="${stylenames}" delims="," var="csssel">
<link rel="stylesheet" type="text/css" title="${csssel}" disabled="true" href="${pageContext.request.contextPath}/style/${csssel}.css"/>
</c:forTokens>
<script src="${pageContext.request.contextPath}/script/ngsproject.js" language="JavaScript" type="text/javascript"></script>
</head>


<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />


<div class="box1">
<h1><c:out value="${project.name}" escapeXml="true"/></h1>
<h2><c:out value="${pos}" escapeXml="true"/></h2>

<p><c:out value="${project.description}" escapeXml="true"/></p>

	<c:forEach var="bam" items="${project.bams}">
	<div>
		<a name="bam${bam.id}"/>
		<div><c:forEach var="bam2" items="${project.bams}"> <a href="#bam${bam2.id}">[<c:out value="${bam2.name}" escapeXml="true"/>]</a> </c:forEach></div>
		<h3><c:out value="${bam.name}" escapeXml="true"/></h3>
		<div><u:bam-href project="${project}" bam="${bam}"/></div>
		<div>
		<c:forTokens items="${stylenames}" delims="," var="csssel">
		<a href="#" onclick="NGSProject.switch_style('${csssel}');return false;"> [${csssel}] </a>
		</c:forTokens>
		</div>
		
		<u:tview bam="${bam}" pos="${pos}"/>
		
	</div>
	</c:forEach>

</div>

</body>
</html>