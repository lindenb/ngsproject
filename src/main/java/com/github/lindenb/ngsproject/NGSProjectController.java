package com.github.lindenb.ngsproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
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

import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.ngsproject.model.Group;
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
					/** ADD GROUP ****************************************************************/
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
						resp.sendRedirect("/ngsproject/ngsprojects/groups");
						return;
						}
					/** ADD USER ****************************************************************/
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
						resp.sendRedirect("/ngsproject/ngsprojects/users");
						return;
						}
					/** ADD SAMPLE ****************************************************************/
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
						resp.sendRedirect("/ngsproject/ngsprojects/samples");
						return;
						}
					/** ADD REFERENCE ****************************************************************/
					else if(path[1].equals("addreference"))
						{
						dispathPath="/WEB-INF/jsp/addreference.jsp";
						String name=req.getParameter("name");
						String description=req.getParameter("description");
						String filepath=req.getParameter("path");

						if(name==null || filepath==null)
							{
							break;
							}
						
						if(description==null || description.trim().isEmpty())
							{
							description=name;
							}
						
						if(model.getUserByName(name)!=null)
							{
							messages.add(new Message("Reference already exists"));
							break;
							}
						if(!name.matches("[a-zA-Z][a-zA-Z0-9]*") || name.length()>10)
							{
							messages.add(new Message("Bad ref name"));
							break;
							}
						if(!filepath.trim().isEmpty())
							{
							messages.add(new Message("Bad file "+filepath));
							break;
							}
						File fasta=new File(filepath.trim());
						if(!(fasta.isFile() && fasta.isAbsolute() && fasta.exists() && fasta.canRead()))
							{
							messages.add(new Message("Bad file "+filepath+" must be absolute/exists/readeable"));
							break;
							}
						
						con=model.getDataSource().getConnection();
						con.setAutoCommit(false);
						pstmt=con.prepareStatement(
								"insert into REFERENCE(path,name,description) values(?,?,?)");
						pstmt.setString(1,filepath.trim());
						pstmt.setString(2,name.trim());
						pstmt.setString(3,description);
						pstmt.executeUpdate();
						con.commit();
						messages.add(new Message("Reference inserted"));
						resp.sendRedirect("/ngsproject/ngsprojects/references");
						return;
						}
					/** ADD BAM ****************************************************************/
					else if(path[1].equals("addbam"))
						{
						String bams=req.getParameter("bams");
						dispathPath="/WEB-INF/jsp/addbam.jsp";
						
						if(bams==null || bams.trim().isEmpty())
							{
							break;
							}
						BufferedReader br=new BufferedReader(new StringReader(bams));
						String line=null;
						con=model.getDataSource().getConnection();
						con.setAutoCommit(false);

						pstmt=con.prepareStatement(
								"insert into NGSPROJECTS.BAM(path,sample_id,reference_id) values(?,?,?)");

						while((line=br.readLine())!=null)
							{
							if(line.isEmpty() || line.startsWith("#")) continue;
							String tokens[]=line.split("[\t ]+", 3);
							if(tokens.length!=3 || tokens[0].trim().isEmpty() || tokens[1].trim().isEmpty())
								{
								messages.add(new Message("Bad line in "+line));
								con.rollback();
								break;
								}
							
							File bamfile=new File(tokens[0]);
							if(!(tokens[0].endsWith(".bam") ||
								  bamfile.isFile() &&
								  bamfile.isAbsolute() &&
								  bamfile.exists() &&
								  bamfile.canRead()))
								{
								messages.add(new Message("Bad file "+bamfile+" must be absolute/exists/readeable"));
								con.rollback();
								break;
								}

							Sample sample=model.getSampleByName(tokens[1]);
							if(sample==null)
								{
								messages.add(new Message("UNknown sample "+tokens[1]));
								con.rollback();
								break;
								}
							
							Reference reference=model.getReferenceByName(tokens[2]);
							if(reference==null)
								{
								messages.add(new Message("UNknown reference "+tokens[2]));
								con.rollback();
								break;
								}
							
							
							if(model.getBamByPath(tokens[0])!=null)
								{
								messages.add(new Message("Bam already defined "+tokens[0]));
								continue;
								}
							
							
														
							pstmt.setString(1,bamfile.getAbsolutePath());
							pstmt.setLong(2, sample.getId());
							pstmt.setLong(3, reference.getId());
							pstmt.executeUpdate();
							}
						con.commit();
						messages.add(new Message("BAMS inserted"));
						resp.sendRedirect("/ngsproject/ngsprojects/bams");
						return;
						}
					/** ADD PROJECT ****************************************************************/
					else if(path[1].equals("addproject"))
						{
						req.setAttribute("groups", model.getAllGroups());
						req.setAttribute("bams", model.getAllBams());
						String name=req.getParameter("name");
						String description=req.getParameter("description");
						String groupstr=req.getParameter("group");
						String bamstrs[]=req.getParameterValues("bams");
						dispathPath="/WEB-INF/jsp/addproject.jsp";

						if(	name==null
							|| name.trim().isEmpty()
							|| req.getParameter("bams")==null
							|| !isULong(groupstr)
							|| bamstrs==null || bamstrs.length==0
							)
							{
							break;
							}
						
						if(model.getProjectByName(name)!=null)
							{
							messages.add(new Message("Project already exists"));
							break;
							}
						if(!name.matches("[a-zA-Z][a-zA-Z0-9]*") || name.length()>10)
							{
							messages.add(new Message("Bad project name"));
							break;
							}
						
						if(description==null || description.trim().isEmpty())
							{
							description=name;
							}
						
						Group group= model.getGroupById(Long.parseLong(groupstr));
						if(group==null)
							{
							messages.add(new Message("Cannot get group id "+groupstr));
							break;
							}
						
						Set<Long> bamIds=new LinkedHashSet<Long>();
						for(String b: bamstrs)
							{
							long bam_id=Long.parseLong(b);
							if(model.getBamById(bam_id)==null)
								{
								messages.add(new Message("Cannot get bam id "+groupstr));
								bamIds=null;
								break;
								}
							bamIds.add(bam_id);
							}
						if(bamIds==null) break;
						
						
						con=model.getDataSource().getConnection();
						con.setAutoCommit(false);
						pstmt=con.prepareStatement(
								"insert into PROJECT(name,description,group_id) values(?,?,?)",
								PreparedStatement.RETURN_GENERATED_KEYS);
						pstmt.setString(1,name.trim());
						pstmt.setString(2,description.trim());
						pstmt.setLong(3,group.getId());
						pstmt.executeUpdate();
						
						Long projectId=null;
						ResultSet row=pstmt.getGeneratedKeys();
						while(row.next())
							{
							projectId=row.getLong(1);
							}
						row.close();
						pstmt.close();
						pstmt=null;
						if(projectId==null) throw new SQLException("id ??");
						
						pstmt=con.prepareStatement("insert into PROJECT2BAM(project_id,bam_id) values(?,?)");
						for(Long bamid:bamIds)
							{
							pstmt.setLong(1,projectId);
							pstmt.setLong(2,bamid);
							pstmt.executeUpdate();
							}

						con.commit();
						messages.add(new Message("Project inserted"));
						resp.sendRedirect("/ngsproject/ngsprojects/project/"+name);
						return;
						}
					break;
					}
				}
			catch(Exception err)
				{
				try{ if(con!=null) con.rollback();} catch(Exception err2){}
				messages.add(new Message(err));
				}
			finally
				{
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
			String posStr=req.getParameter("pos");
			if(posStr==null) posStr="";
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
				
				}
			
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
		else if(path[0].equals("references"))
			{
			dispathPath="/WEB-INF/jsp/references.jsp";
			req.setAttribute("references", model.getAllReferences());
			}
		else if(path[0].equals("bams"))
			{
			dispathPath="/WEB-INF/jsp/bams.jsp";
			req.setAttribute("bams", model.getAllBams());
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
