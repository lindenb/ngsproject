package com.github.lindenb.ngsproject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.ngsproject.model.Model;
import com.github.lindenb.ngsproject.model.Project;
import com.github.lindenb.ngsproject.model.User;

public class NGSProjectController extends HttpServlet
	{
	private static final String PROJECT_FILE_PATH="project.file.path";
	/* "The JAXBContext class is thread safe" http://jaxb.java.net/guide/Performance_and_thread_safety.html */
	private static final String ERROR_CLASS="error";
	private static final String WARNING_CLASS="warning;";
	private static final String INFO_CLASS="info;";
	private static final String OK_CLASS="info;";
	
	
	
	@Override
	protected void doPost(
			HttpServletRequest req,
			HttpServletResponse resp)
			throws ServletException, IOException
		{
		Model model=(Model)req.getServletContext().getAttribute("model");
		if(model==null) throw new ServletException("model is null");
		User currentUser=null;
		HttpSession session=req.getSession(false);
		if(session!=null) currentUser=(User)session.getAttribute("user");
		
		List<Message> messages=new ArrayList<Message>();
		req.setAttribute("messages", messages);
		
	
		
		String pathInfo=req.getPathInfo();
		if(pathInfo==null)pathInfo="";
		while(pathInfo.startsWith("/")) pathInfo=pathInfo.substring(1);
		String path[]=pathInfo.split("[/]+");
		String dispathPath="/WEB-INF/jsp/projects.jsp";

		if(path[0].equals("project") && path.length>1 && isULong(path[1]))
			{
			Project project= model.getProjectById(Long.parseLong(path[1]));
			if(project==null) throw new ServletException("Cannot find project "+path[1]);
			req.setAttribute("project",project);
			dispathPath="/WEB-INF/jsp/project.jsp";
			String pos=req.getParameter("pos");
			if(pos==null) pos="";
			req.setAttribute("pos",pos);

			
			if(path.length>3 && path[2].equals("bam") &&  isULong(path[3]))
				{
				Bam bam=project.getBamById(Long.parseLong(path[3]));
				if(bam!=null)
					{
					dispathPath="/WEB-INF/jsp/bam.jsp";
					req.setAttribute("bam",bam);
					}
				}
			
			

			}
		else
			{
			req.setAttribute("projects", model.getAllProjects());
			}
		
		
		
		req.getRequestDispatcher(dispathPath).forward(req, resp);
		}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
		{
		this.doPost(req, resp);
		}

	
	public static String toSHA1(String convertme) throws ServletException
		{
	    MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA1");
	        byte sha1[]=md.digest(convertme.getBytes("UTF-8"));
	        String result = "";
	        for (int i=0; i < sha1.length; i++)
	        	{
	        	result +=
	                Integer.toString( ( sha1[i] & 0xff ) + 0x100, 16).substring( 1 );
	        	}
	        return result;
	    	}
	    catch(Exception e) {
	       throw new ServletException();
	    	} 
		}
	private boolean isULong(String s)
		{
		if(s==null || s.isEmpty()) return false;
		try {
			return Long.parseLong(s)>0;
		} catch (Exception e) {
			return false;
			}
		}
	
	public void destroy()
		{
		}
	
	public static class Message
		{
		String msg;
		String style;
		
		public Message(String msg)
			{
			this(msg,"");
			}
		
		public Message(String msg,String style)
			{
			this.msg=msg;
			this.style=style;
			}
		public String getStyle()
			{
			return style;
			}
		public String getMessage()
			{
			return msg;
			}
		@Override
		public String toString()
			{
			return getMessage();
			}
		}	
	
	}
