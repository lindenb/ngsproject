<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>

<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/>
<head>
<title>Login</title>
<u:head/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/navbar.jsp" />
<jsp:include page="/WEB-INF/jsp/messages.jsp"/>

    <form class="form-horizontal"
    	action="${pageContext.request.contextPath}/ngsprojects/validate"
    	method="POST" >
    <div class="control-group">
    <label class="control-label" for="username">Login</label>
    <div class="controls">
    <input type="text" id="username" placeholder="login" name="username" value="<c:out value="${param.username}" escapeXml="true"/>"/>
    </div>
    </div>
    <div class="control-group">
    <label class="control-label" for="inputPassword">Password</label>
    <div class="controls">
    <input type="password" id="inputPassword" name="password" placeholder="Password"/>
    </div>
    </div>
    <div class="control-group">
    <div class="controls">
    <button type="submit" class="btn btn-primary">Sign in</button>
    </div>
    </div>
    </form>
    
    
    </body>
</html>
