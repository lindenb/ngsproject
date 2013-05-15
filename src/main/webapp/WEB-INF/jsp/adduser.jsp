<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title>Add User</title>
<u:head/>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css?r=<%= java.lang.System.currentTimeMillis() %>"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>

    <form class="form-horizontal"
    	action="${pageContext.request.contextPath}/ngsprojects/admin/adduser"
    	method="POST" >
    <div class="control-group">
    	<label class="control-label" for="username">Name</label>
	    <div class="controls">
	    	<input type="text" id="username" placeholder="login" name="name" value="<c:out value="${param.name}" escapeXml="true"/>"/>
	    </div>
    </div>
    <div class="control-group">
	    <label class="control-label" for="inputPassword">Password</label>
	    <div class="controls">
	    	<input type="password" id="inputPassword" name="password" placeholder="Password"/>
	    </div>
    </div>
    <div class="control-group">
	    <label class="control-label" for="groups">Group</label>
	    <div class="controls">
	    	<select  id="groups" name="group" placeholder="Password" multiple="yes">
	    		<c:forEach var="g" items="${groups}">
	    			<option><c:out value="${g.name}" escapeXml="true"/></option>
	    		</c:forEach>
	    	</select>
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