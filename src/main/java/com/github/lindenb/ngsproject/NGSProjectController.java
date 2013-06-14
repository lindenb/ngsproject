package com.github.lindenb.ngsproject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.ngsproject.model.Model;
import com.github.lindenb.ngsproject.model.Project;
import com.github.lindenb.ngsproject.model.Reference;
import com.github.lindenb.ngsproject.model.Sample;
import com.github.lindenb.ngsproject.model.User;
import com.github.lindenb.ngsproject.model.VCF;
import com.github.lindenb.vizbam.SAMSequenceInterval;
import com.github.lindenb.vizbam.SAMSequencePosition;
import com.github.lindenb.vizbam.locparser.LocParser;

@SuppressWarnings("serial")
public class NGSProjectController extends HttpServlet
	{
	//private static final String PROJECT_FILE_PATH="project.file.path";
	/* "The JAXBContext class is thread safe" http://jaxb.java.net/guide/Performance_and_thread_safety.html */
	
	
	
	@Override
	protected void doPost(
			HttpServletRequest req,
			HttpServletResponse resp)
			throws ServletException, IOException
		{
		Model model=(Model)req.getServletContext().getAttribute("model");
		if(model==null) throw new ServletException("model is null");
		
		UserPref currentUser=(UserPref)req.getSession().getAttribute("user");
		List<Message> messages=new ArrayList<Message>();
		req.setAttribute("messages", messages);
		if(currentUser!=null && !currentUser.getMessages().isEmpty())
			{
			messages.addAll(currentUser.getMessages());
			currentUser.getMessages().clear();
			}
		

		
		String pathInfo=req.getPathInfo();
		if(pathInfo==null)pathInfo="";
		while(pathInfo.startsWith("/")) pathInfo=pathInfo.substring(1);
		String path[]=pathInfo.split("[/]+");
		String dispathPath="/WEB-INF/jsp/projects.jsp";

		if(path[0].equals("login"))
			{
			req.getSession().invalidate();
			dispathPath="/WEB-INF/jsp/login.jsp";
			}
		else if(path[0].equals("logout"))
			{
			req.getSession().invalidate();
			resp.sendRedirect(""+req.getAttribute("proxyBase")+"/ngsproject/ngsprojects");
			return;
			}
		else if(path[0].equals("validate"))
			{
			req.getSession().invalidate();
			
			String username=req.getParameter("username");
			if(username==null) username="";
			username=username.trim();
			String password=req.getParameter("password");
			if(password==null) password="";
			password=password.trim();
			password=toSHA1(password);
			User user=null;
			for(User u:model.getAllUsers())
				{
				
				if(u.getName().equals(username) && password.equals(u.getSha1Sum()))
					{
					user=u;
					break;
					}
				}
			if(user!=null)
				{
				currentUser=new UserPref();
				currentUser.setUser(user);
				currentUser.getMessages().add(new Message("logged as "+username,Message.MsgType.ok));
				
				req.getSession().setAttribute("user", currentUser);
				
				resp.sendRedirect(""+req.getAttribute("proxyBase")+"/ngsproject/ngsprojects");
				return;
				}
			else
				{
				messages.add(new Message("Cannot log as "+username,Message.MsgType.error));
				dispathPath="/WEB-INF/jsp/login.jsp";
				}
			}
		else if(path[0].equals("project") && path.length>1 && isULong(path[1]))
			{
			Project project= model.getProjectById(Long.parseLong(path[1]));
			if(project==null)
				{
				messages.add(new Message("Cannot get project "+path[1]));
				}
			else if(!Functions.visible(req, project))
				{
				messages.add(new Message("You're not granted to see project "+path[1]));
				}
			else
				{
				req.setAttribute("project",project);
				dispathPath="/WEB-INF/jsp/project.jsp";
				String posStr=req.getParameter("pos");
				if(posStr==null)
					{
					posStr="";
					}
				try
					{
					Reference reference=project.getReference();
					if(reference!=null)
						{
						SAMSequencePosition pos=LocParser.parseOne(
								reference.getIndexedFastaSequenceFile().getSequenceDictionary(),
								posStr,
								true
								);
						req.setAttribute("pos",pos);
						}
					}
				catch(Exception err)
					{
					messages.add(new Message(err));
					}
				}
			}
		else if(path[0].equals("references"))
			{
			dispathPath="/WEB-INF/jsp/references.jsp";
			req.setAttribute("references", model.getAllReferences());
			}
		else if(path[0].equals("reference") && path.length>1 && isULong(path[1]))
			{
			Reference ref=model.getReferenceById(Long.parseLong(path[1]));
			if(ref==null)
				{
				if(ref==null) throw new ServletException("Cannot find ref "+path[1]);
				}
			
			dispathPath="/WEB-INF/jsp/reference.jsp";
			req.setAttribute("reference",ref);
			}
		else if(path[0].equals("bams"))
			{
			dispathPath="/WEB-INF/jsp/bams.jsp";
			req.setAttribute("bams", model.getAllBams());
			}
		else if(path[0].equals("bam") && path.length>1 && isULong(path[1]))
			{
			Bam bam=model.getBamById(Long.parseLong(path[1]));
			if(bam==null)
				{
				messages.add(new Message("Cannot get Bam "+path[1]));
				}
			else if(!Functions.visible(req, bam))
				{
				messages.add(new Message("You're not granted to have access to Bam "+path[1]));
				}
			else
				{
				dispathPath="/WEB-INF/jsp/bam.jsp";
				req.setAttribute("bam", bam);
				}
			}
		else if(path[0].equals("users"))
			{
			dispathPath="/WEB-INF/jsp/users.jsp";
			req.setAttribute("users", model.getAllUsers());
			}
		else if(path[0].equals("groups"))
			{
			dispathPath="/WEB-INF/jsp/groups.jsp";
			req.setAttribute("groups", model.getAllGroups());
			}
		else if(path[0].equals("samples"))
			{
			dispathPath="/WEB-INF/jsp/samples.jsp";
			req.setAttribute("samples", model.getAllSamples());
			}
		else if(path[0].equals("sample") && path.length>1 && isULong(path[1]))
			{
			Sample sample=model.getSampleById(Long.parseLong(path[1]));
			if(sample==null)
				{
				if(sample==null) throw new ServletException("Cannot find sample "+path[1]);
				}
			
			dispathPath="/WEB-INF/jsp/sample.jsp";
			req.setAttribute("sample", sample);
			}
		else if(path[0].equals("vcfs"))
			{
			dispathPath="/WEB-INF/jsp/vcfs.jsp";
			req.setAttribute("vcfs", model.getAllVCFs());
			}
		else if(path[0].equals("vcf") && path.length>1 && isULong(path[1]))
			{
			VCF vcf=model.getVcfById(Long.parseLong(path[1]));
			if(vcf==null)
				{
				if(vcf==null) throw new ServletException("Cannot find vcf "+path[1]);
				}
			
			String intervalStr=req.getParameter("interval");
			if(intervalStr==null) intervalStr="";
			try
				{
				Reference reference=vcf.getReference();
				if(reference!=null)
					{
					SAMSequenceInterval interval=LocParser.parseInterval(
							reference.getIndexedFastaSequenceFile().getSequenceDictionary(),
							intervalStr,
							true
							);
					req.setAttribute("interval",interval);
					}
				}
			catch(Exception err)
				{
				
				}
			
			dispathPath="/WEB-INF/jsp/vcf.jsp";
			req.setAttribute("vcf", vcf);
			}
		else if(path[0].equals("search"))
			{
			req.setAttribute("vcfs", model.getAllVCFs());
			req.setAttribute("references", model.getAllReferences());
			dispathPath="/WEB-INF/jsp/search.jsp";
			}
		else
			{
			
			req.setAttribute("projects", model.getProjects(currentUser==null?null:currentUser.getUser()));
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
	
	
	
	}
