<%@tag language="java" pageEncoding="UTF-8"%>
<%@tag import="com.github.lindenb.ngsproject.model.Bam"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>
<%@attribute name="bam" required="true" rtexprvalue="true" type="com.github.lindenb.ngsproject.model.Bam"%>
<%@attribute name="pos" required="false" rtexprvalue="true" type="java.lang.String"%>
<div><pre class="align"><%

com.github.lindenb.ngsproject.CallTview tview=new com.github.lindenb.ngsproject.CallTview();
tview.display(out,bam,pos);
%></pre></div>
