<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<meta name="description" content="<c:out value="${bam.path}" escapeXml="true"/>"/>

<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css?r=<%= java.lang.System.currentTimeMillis() %>"/>

<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet"></link>
<style>
  body {
    padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
  }
</style>
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-responsive.css" rel="stylesheet"></link>
<script src="${pageContext.request.contextPath}/script/jquery.js" language="JavaScript" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.js" language="JavaScript" type="text/javascript"></script>
