<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="vcf" required="true" rtexprvalue="true" type="java.lang.Object" %>
<c:choose>
<c:when test="${empty vcf }">N/A</c:when>
<c:otherwise>
<c:url value="/ngsprojects/vcf/${vcf.id}" var="url">
</c:url>
<a href="<c:out value="${url}" escapeXml="true"/>"><c:out value="${vcf.name}" escapeXml="true"/></a>
</c:otherwise>
</c:choose>
