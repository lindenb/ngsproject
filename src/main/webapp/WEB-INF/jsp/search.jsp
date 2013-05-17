<%@page import="com.github.lindenb.vizbam.locparser.LocParser"%>
<%@page import="com.github.lindenb.vizbam.SAMSequenceInterval"%>
<%@page import="com.github.lindenb.ngsproject.model.*"%>
<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ngs" uri="http://github.com/lindenb/ngsproject/tags"%>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title>Search <c:out value="${param.interval}" escapeXml="true"/></<title>>
<u:head/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css?r=<%= java.lang.System.currentTimeMillis() %>"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>
<h1>Search <c:out value="${param.interval}" escapeXml="true"/></h1>


<div>
    <form class="form-search"
    	action="${pageContext.request.contextPath}/ngsprojects/search"
    	method="GET" >
    	<label class="control-label" for="reference">Ref</label>
    	<select id="reference" name="referenceid">
			<c:forEach var="ref" items="${references }">
				<option value="${ref.id}">
					<c:out value="${ref.name}" escapeXml="true"/>
				</option>
			</c:forEach>
    	</select>
    	<label class="control-label" for="interval">Region</label>
	    	<input type="text"
	    		id="interval"
	    		placeholder="chrom:start-end"
	    		name="interval"
	    		class="input-medium search-query"
	    		value="<c:out value="${param.interval}" escapeXml="true"/>"
	    		/>
	   	<button type="submit" class="btn btn-primary">Go</button>
    </form>
</div>


<c:if test="${not empty  param.interval and not empty  param.referenceid}">
<c:forEach var="vcf" items="${ngs:filter(pageContext.request,vcfs)}">
<c:if test="${vcf.reference.id eq param.referenceid }">
<div><c:choose>
<c:when test="${ngs:visible(pageContext.request,vcf)}">
<u:vcf-href vcf="${vcf}"/>
</c:when>
<c:otherwise>Private VCF ID.${vcf.id}</c:otherwise>
</c:choose></div>
<pre><ngs:viewvcf interval="${param.interval}" vcf="${vcf}" escapeXml="${true}" meta="${false}" /></pre>
</c:if>
</c:forEach>
</c:if>

</body>
</html>