package com.github.lindenb.ngsproject;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.ngsproject.model.Group;
import com.github.lindenb.ngsproject.model.Model;
import com.github.lindenb.ngsproject.model.Project;
import com.github.lindenb.ngsproject.model.User;

public class NGSProjectController extends HttpServlet
	{
	private static final String PROJECT_FILE_PATH="project.file.path";
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
			resp.sendRedirect("/ngsproject/ngsprojects");
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
				resp.sendRedirect("/ngsproject/ngsprojects");
				return;
				}
			else
				{
				messages.add(new Message("Cannot log as "+username,Message.MsgType.error));
				dispathPath="/WEB-INF/jsp/login.jsp";
				}
			}
		else if(path[0].equals("admin") && path.length>1 && currentUser!=null && currentUser.isAdmin())
			{
			Connection con=null;
			PreparedStatement pstmt=null;
			try
				{
				for(;;)
					{
					if(path[1].equals("addgroup"))
						{
						req.setAttribute("users", model.getAllUsers());

						String name=req.getParameter("name");
						if(model.getGroupByName(name)!=null)
							{
							messages.add(new Message("Group already exists"));
							break;
							}
					
						dispathPath="/WEB-INF/jsp/addgroup.jsp";
						if(name==null || name.trim().isEmpty())
							{
							break;
							}
						con=model.getDataSource().getConnection();
						con.setAutoCommit(false);
						pstmt=con.prepareStatement("insert into USERGROUP(name,is_public) values(?,?)"
								,PreparedStatement.RETURN_GENERATED_KEYS);
						pstmt.setString(1,name.trim());
						pstmt.setInt(2, ("1".equals(req.getParameter("public"))?1:0));
						pstmt.executeUpdate();
						
						
						Long groupId=null;
						ResultSet row=pstmt.getGeneratedKeys();
						if(row==null) throw new NullPointerException("??");
						while(row.next())
							{
							groupId=row.getLong(1);
							}
						row.close();
						pstmt.close();
						pstmt=null;
						if(groupId==null) throw new SQLException("id ??");
						String users[]=req.getParameterValues("users");
						if(users!=null && users.length>0)
							{
							pstmt=con.prepareStatement(
									"insert into USER2GROUP(user_id,group_id) values(?,?)");
							for(String u:users)
								{
								User u1=model.getUserByName(u);
								if(u1==null) continue;
								pstmt.setLong(1,u1.getId());
								pstmt.setLong(2,groupId);
								pstmt.executeUpdate();
								}
							
							}
						
						con.commit();
						messages.add(new Message("Group inserted"));
						break;
						}
					else if(path[1].equals("adduser"))
						{
						req.setAttribute("groups", model.getAllGroups());
						String name=req.getParameter("name");
						String password=req.getParameter("password");
						dispathPath="/WEB-INF/jsp/adduser.jsp";

						if(name==null || password==null)
							{
							break;
							}
						
						if(model.getUserByName(name)!=null)
							{
							messages.add(new Message("User already exists"));
							break;
							}
						if(!name.matches("[a-zA-Z][a-zA-Z0-9]*") || name.length()>10)
							{
							messages.add(new Message("Bad user name"));
							break;
							}
						password=toSHA1(password.trim());
						
						con=model.getDataSource().getConnection();
						con.setAutoCommit(false);
						pstmt=con.prepareStatement(
								"insert into USERS(name,sha1sum,is_admin) values(?,?,?)",
								PreparedStatement.RETURN_GENERATED_KEYS);
						pstmt.setString(1,name.trim());
						pstmt.setString(2,password);
						pstmt.setInt(3, ("1".equals(req.getParameter("pubic"))?1:0));
						pstmt.executeUpdate();
						
						Long userId=null;
						ResultSet row=pstmt.getGeneratedKeys();
						while(row.next())
							{
							userId=row.getLong(1);
							}
						row.close();
						pstmt.close();
						pstmt=null;
						if(userId==null) throw new SQLException("id ??");
						String groups[]=req.getParameterValues("group");
						if(groups!=null && groups.length>0)
							{
							pstmt=con.prepareStatement("insert into USER2GROUP(user_id,group_id) values(?,?)");
							for(String g:groups)
								{
								Group g1=model.getGroupByName(g);
								if(g1==null) continue;
								pstmt.setLong(1,userId);
								pstmt.setLong(2,g1.getId());
								pstmt.executeUpdate();
								}
							
							}
						
						con.commit();
						messages.add(new Message("User inserted"));
						break;
						}
					else if(path[1].equals("addsample"))
						{
						String names=req.getParameter("names");
						dispathPath="/WEB-INF/jsp/addsample.jsp";
						
						if(names==null || names.trim().isEmpty())
							{
							break;
							}
						Set<String> set=new LinkedHashSet<String>();
						for(String name:names.split("[\n ,\r\t]+"))
							{
							if(name.isEmpty()) continue;
							set.add(name);
							}
						if(set.isEmpty())
							{
							break;
							}
						messages.add(new Message(set.toString()));
						con=model.getDataSource().getConnection();
						con.setAutoCommit(false);
						pstmt=con.prepareStatement(
								"insert into NGSPROJECTS.SAMPLE(name) values(?)");

						for(String name:set)
							{
							messages.add(new Message(name));
							if(model.getSampleByName(name)!=null)
								{
								messages.add(new Message("Sample "+name+" already exists"));
								continue;
								}
							if(!name.matches("[a-zA-Z][a-zA-Z0-9]*") || name.length()>40)
								{
								messages.add(new Message("Bad Sample name "+name));
								continue;
								}
							
							pstmt.setString(1,name.trim());
							pstmt.executeUpdate();
							}
						con.commit();
						messages.add(new Message("Sample inserted"));
						break;
						}
					break;
					}
				}
			catch(Exception err)
				{
				messages.add(new Message(err));
				}
			finally
				{
				try{ if(con!=null) con.rollback();} catch(Exception err2){}
				try{ if(pstmt!=null) pstmt.close();} catch(Exception err2){}
				try{ if(con!=null) con.close();} catch(Exception err2){}
				}
			}
		else if(path[0].equals("project") && path.length>1 && isULong(path[1]))
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
