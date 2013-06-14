<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<%    pageContext.setAttribute("samflags", com.github.lindenb.ngsproject.SamFlag.values()); %>
<head>
<title><c:out value="${bam.name}" escapeXml="true"/></title>
<u:head/>


</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>

<h1><c:out value="${bam.name}" escapeXml="true"/></h1>


<c:url value="/samtoolsview/project/${project.id}/bam/${bam.id}" var="url">
</c:url>


<form method="GET" action="<c:out value="${proxyBase}${url}" escapeXml="true"/>" target="bamframe">
<input type="hidden" name="bam-id" value="${bam.id}"/>
<div class="row-fuild">
<div class="span4">
 <fieldset>
 	<legend>Controls</legend>
 	 <fieldset>
 		<label>Region:</label>
		<input name="interval" placeholder="chrom:start-end"/>	
		<label class="checkbox">
		<input type="checkbox" name="unmapped" value="true" />Unmapped
		</label>
		<label>Quality:</label>
		<input name="qual" placeholder="quality" value="0"/>	
		
	</fieldset>
	 <input type="submit"  class="btn btn-primary"/>
	
 </fieldset>
 </div>

<div class="span4">
 <fieldset>
 	<legend>Filtering flags</legend>
 	<c:forEach var="f" items="${samflags}">
 	 <label class="checkbox">
		<input type="checkbox" name="filter" value="${f.flag}"  />${f.label}
	</label>
	</c:forEach>
 </fieldset>
 </div>
 
 <div class="span4">
 <fieldset>
 	<legend>Required flags</legend>
 	<c:forEach var="f" items="${samflags}">
 	 <label class="checkbox">
		<input type="checkbox" name="require" value="${f.flag}"  />${f.label}
	</label>
	</c:forEach>
 </fieldset>
 </div>
 
 </div>
</form>


<div class="row-fluid">
<div class="offset1 span10">
<iframe
	name="bamframe"
	id="bamframe"
	class="container well well-small span12"
	style="height:800px;"
	src="<c:out value="${url}" escapeXml="true"/>?bam-id=${bam.id}">
	</iframe> 
</div>
</div>

</body>
</html>