<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<jsp:include page="/WEB-INF/jsp/xhtml-header.jsp"/><head>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<title>Login</title>
</head>
<body>
<h1>Login</h1>

<div style="border: thin solid darkgray; margin: 10px; padding: 10px; background-color:#D0D0D0; color: black; font-size:200%;">
<form method="POST" action="${pageContext.request.contextPath}/authenticate">
<input type="hidden" name="action" value="validate"/>
<fieldset>
<legend>Insert Login/Password</legend>
<label style="width:300px; float: left; text-align:right;" for="login">Login:</label> <input id="login" name="login" value="${requestScope['login']}"><br>
<label style="width:300px; float: left; text-align:right;" for="password">Password:</label> <input id="password" name="password" type="password"><br>
<input type="submit" style="color:red;">
</fieldset>
</form>
</div>

</body>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body/>