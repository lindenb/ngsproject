<%@page import="com.github.lindenb.ngsproject.model.Variation"%>
<%@page import="net.sf.picard.util.Interval"%>
<%@page import="com.github.lindenb.ngsproject.model.Linkage"%>
<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@page import="com.github.lindenb.ngsproject.model.Linkage"%>
<%@page import="com.github.lindenb.ngsproject.model.Sample"%>
<%@page import="com.github.lindenb.ngsproject.model.Project"%>
<%@page import="com.github.lindenb.vizbam.SAMSequencePosition"%>
<%@page import="net.sf.picard.util.Interval"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ngs" uri="http://github.com/lindenb/ngsproject/tags"%>
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
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>

<div class="box1">
<h1><c:out value="${project.name}" escapeXml="true"/></h1>
<h2><c:out value="${pos}" escapeXml="true"/></h2>

<p><c:out value="${project.description}" escapeXml="true"/></p>

<div>
    <form class="form-search"
    	action="${pageContext.request.contextPath}/ngsprojects/project/${project.id}"
    	method="GET" >
    	<label class="control-label" for="pos">Jump to</label>
	    	<input type="text"
	    		placeholder="chrom:pos"
	    		name="pos"
	    		class="input-medium search-query"
	    		value="<c:out value="${param.pos}" escapeXml="true"/>"
	    		/>
	   	<button type="submit" class="btn btn-primary">Go</button>
    </form>
</div>

<c:if test="${not empty pos }">
<%

SAMSequencePosition loc1=(SAMSequencePosition)request.getAttribute("pos");
Project p1=(Project)request.getAttribute("project");
Interval interval= new Interval(
		loc1.getName(),
		loc1.getPosition(),
		loc1.getPosition()+80
		);
pageContext.setAttribute("interval",interval);
Linkage l1 =p1.getGenotypes(interval);
pageContext.setAttribute("linkage",l1);
%>
<table>
	<tr>
		<th>Sample</th>
		<c:forEach var="i" items="${linkage.variations}">
			<th>${i}</th>
		</c:forEach>
	</tr>
	<c:forEach var="j" items="${linkage.samples}">
		<tr>
		<th><u:sample-href sample="${j}"/></th>
		<c:forEach var="i" items="${linkage.variations}">
			<th>
			<%
			Sample S2=(Sample)pageContext.getAttribute("j");
			Variation V2=(Variation)pageContext.getAttribute("i");
			pageContext.setAttribute("genotypes", l1.getGenotypes(V2,S2));
			%>
			<c:forEach var="k" items="${genotypes}">
			<c:out value="${k}" escapeXml="true"/>
			</c:forEach>
			</th>
		</c:forEach>
		</tr>
	</c:forEach>
	
	
</table>

</c:if>
<h2>BAM</h2>
	<c:forEach var="bam" items="${project.bams}">
	<div>
		<a name="bam${bam.id}"/>
		<div><c:forEach var="bam2" items="${project.bams}"> <a href="#bam${bam2.id}">[<c:out value="${bam2.name}" escapeXml="true"/>]</a> </c:forEach></div>
		
		<h3><c:out value="${bam.name}" escapeXml="true"/></h3>
		<div>Sample: <u:sample-href sample="${bam.sample}" /></div>
		<div><u:bam-href bam="${bam}" interval="${param.pos}"/></div>
		
		<div>
		<c:forTokens items="${stylenames}" delims="," var="csssel">
		  <a href="#" onclick="NGSProject.switch_style('${csssel}');return false;"> [${csssel}] </a>
		  </c:forTokens>
		</div>
		
		<u:tview bam="${bam}" pos="${pos}"/>
		
	</div>
	</c:forEach>
</div>
<h2>VCFs</h2>

	<c:forEach var="vcf" items="${project.vcfs}">
	<div>
		<a name="vcf${vcf.id}"/>
		<div><u:vcf-href vcf="${vcf}"/></div>
		<pre><ngs:viewvcf vcf="${vcf}" 
			interval="${interval.sequence}:${interval.start}-${interval.end}"
			escapeXml="true"
			/></pre>
	</div>
	</c:forEach>


</body>
</html>