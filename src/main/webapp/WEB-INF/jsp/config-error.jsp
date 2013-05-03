<%@page import="java.io.PrintWriter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><html><head>
<title>Configuration Error.</title>
</head>
<body>
<h1>Configuration Error</h1>
The was an error in the configuration file <a href="#">${initParam['tabix.config']}</a>:
<pre style="background-color:black; color:white; margin:10px; padding: 10px;">
<% 
Throwable err=(Throwable)request.getAttribute("exception");
PrintWriter w=new PrintWriter(out);
if(err!=null) err.printStackTrace(new PrintWriter(out));
w.flush();
%>
</pre>
<hr/>
Deploy time: ${applicationScope["deploy.time"]}
</body>
</html>