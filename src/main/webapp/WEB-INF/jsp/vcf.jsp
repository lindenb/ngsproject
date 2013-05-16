<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ngs" uri="http://github.com/lindenb/ngsproject/tags"%>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title><c:out value="${vcf.name}" escapeXml="true"/></title>
<u:head/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>

<h1><c:out value="${vcf.name}" escapeXml="true"/></h1>


<div class="box1">
<h2>Projects</h2>
<table>
<thead>
	<tr>
		<th>Project</th>
		<th>Description</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="p" items="${ngs:filter(pageContext.request,vcf.projects)}">
		<tr>
			<td><u:project-href project="${p}"/></td>
			<td><c:out value="${p.description}" escapeXml="true"/></td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>

<div class="box1">
<h2>Samples</h2>
<table>
<thead>
	<tr>
		<th>Name</th>
	</tr>
	</thead>
<tbody>
	<c:forEach var="p" items="${ngs:filter(pageContext.request,vcf.samples)}">
		<tr>
			<td><u:sample-href project="${p}"/></td>
		</tr>
	</c:forEach>
</tbody>
</table>
</div>



<div>
    <form class="form-search"
    	action="${pageContext.request.contextPath}/vcfview"
    	method="GET"
    	 target="vcfframe"
    	>
    	<input type="hidden" name="vcf-id" value="{vcf.id}"/>
    	<label class="control-label" for="pos">Jump to</label>
    	<input type="text"
    		placeholder="chrom:start-end"
    		name="interval"
    		class="input-medium search-query"
    		value="<c:out value="${param.interval}" escapeXml="true"/>"
    		/>
	   	<button type="submit" class="btn btn-primary">Go</button>
    </form>
</div>

<div class="row-fluid">
<div class="offset1 span10">
<iframe
	name="vcfframe"
	id="bamframe"
	class="container well well-small span12"
	style="height:800px;"
	src="${pageContext.request.contextPath}/vcfview?vcf-id=${vcf.id}">
	</iframe> 
</div>
</div>



</body>
</html>