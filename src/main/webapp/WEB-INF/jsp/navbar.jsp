<%@ page language="java"
	contentType="application/xhtml+xml; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags" %>

<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="brand" href="#">NGS Projects</a>
      <div class="nav-collapse collapse">
        <ul class="nav">
          
          
         	
          		<li class="dropdown"> <a href="#" class="dropdown-toggle" data-toggle="dropdown">Go<b class="caret"></b></a>
	          		<ul class="dropdown-menu">  
                    	<li><a href="${pageContext.request.contextPath}/ngsprojects/projects">Projects</a></li>
                    	<li><a href="${pageContext.request.contextPath}/ngsprojects/references">References</a></li>
                     	<li><a href="${pageContext.request.contextPath}/ngsprojects/vcfs">VCF</a></li>
                    	<c:if test="${sessionScope.user.admin}">
                    	<li><a href="${pageContext.request.contextPath}/ngsprojects/samples">Samples</a></li>
                    	<li><a href="${pageContext.request.contextPath}/ngsprojects/bams">Bams</a></li>
                    	<li><a href="${pageContext.request.contextPath}/ngsprojects/users">Users</a></li>
                    	<li><a href="${pageContext.request.contextPath}/ngsprojects/groups">Groups</a></li>
                    	</c:if>
				    </ul>
			  	</li>
          
          
          
          
          <c:choose>
          	<c:when test="${sessionScope.user.admin}">
          		<li class="dropdown"> <a href="#" class="dropdown-toggle" data-toggle="dropdown">Admin  <b class="caret"></b></a>
          		<ul class="dropdown-menu">  
					<li><a href="${pageContext.request.contextPath}/ngsprojects/admin/addsample">Add Sample</a></li>  
					<li><a href="${pageContext.request.contextPath}/ngsprojects/admin/adduser">Add User</a></li>  
					<li><a href="${pageContext.request.contextPath}/ngsprojects/admin/addgroup">Add Group</a></li>  
			    </ul>
			  	</li>
          	</c:when>
          </c:choose>
          
          <c:choose>
          	<c:when test="${sessionScope.user.logged}">
          		  <li><a href="${pageContext.request.contextPath}/ngsprojects/logout">Logout</a></li>
          	</c:when>
          	<c:otherwise>
          		<li><a href="${pageContext.request.contextPath}/ngsprojects/login">Login</a></li>
          	</c:otherwise>
          </c:choose>
          
        </ul>
      </div><!--/.nav-collapse -->
    </div>
  </div>
</div>