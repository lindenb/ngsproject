<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title>Add Sample</title>
<u:head/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css?r=<%= java.lang.System.currentTimeMillis() %>"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>

    <form class="form-horizontal"
    	action="${pageContext.request.contextPath}/ngsprojects/admin/addsample"
    	method="POST" >
    <div class="control-group">
    	<label class="control-label" for="name">Name</label>
	    <div class="controls">
	    	<textarea placeholder="Sample Names" name="names"><c:out value="${param.name}" escapeXml="true"/></textarea>
	    </div>
    </div>
    <div class="control-group">
	    <div class="controls">
	   		<button type="submit" class="btn btn-primary">Create</button>
	    </div>
    </div>
    </form>
    
    
    </body>
</html>