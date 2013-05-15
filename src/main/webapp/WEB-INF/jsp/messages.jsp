<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>

<c:if test="${not empty messages}">
<div>
	<c:forEach var="msg" items="${messages}">
	<div class="msg msg${msg.type}">
	<c:out value="${msg.message}" escapeXml="true"/>
	</div>
	</c:forEach>
</div>
</c:if>