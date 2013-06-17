<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="bam" required="true" rtexprvalue="true" type="java.lang.Object" %>
<%@attribute name="interval" required="false" rtexprvalue="true" type="java.lang.String" %>
<c:choose>
<c:when test="${empty bam }">N/A</c:when>
<c:otherwise>
<c:url value="/ngsprojects/bam/${bam.id}" var="url">
<c:if test="${not empty interval}">
   <c:param name="interval" value="${interval}" />
</c:if>
</c:url>
<a href="<c:out value="${url}" escapeXml="true"/>">file://<c:out value="${bam.path}" escapeXml="true"/></a>
</c:otherwise>
</c:choose>
