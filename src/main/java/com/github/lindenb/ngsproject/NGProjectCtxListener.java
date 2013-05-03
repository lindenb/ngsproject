package com.github.lindenb.ngsproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import com.github.lindenb.ngsproject.model.Model;
import com.github.lindenb.ngsproject.model.sql.SimpleDataSource;

public class NGProjectCtxListener implements ServletContextListener
	{
	@Override
	public void contextInitialized(ServletContextEvent ctx)
		{
		Exception error=null;
		try
			{
			for(;;)
				{
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				ctx.getServletContext().setAttribute("deploy.time", dateFormat.format(new Date()));

				
				
				String dbPath=ctx.getServletContext().getInitParameter("db.path");
				if(dbPath==null)
					{
					error=new ServletException("parameter 'db.path' undefined");
					break;
					}
				
				File dbFile=new File(dbPath);
				if(!dbFile.exists())
					{
					error=new FileNotFoundException(dbPath);
					break;
					}
				if(!dbFile.isFile() || !dbFile.canRead())
					{
					error=new IOException("not a file or cannot read :"+dbFile);
					break;
					}
				SimpleDataSource ds=new SimpleDataSource(dbFile);
				Connection con=ds.getConnection();
				con.close();
				Model model=new Model(ds);
				ctx.getServletContext().setAttribute("datasource", ds);
				ctx.getServletContext().setAttribute("model", model);
				break;
				}
			}
		catch(Exception err)
			{
			error=err;
			}
		if(error!=null)
			{
			ctx.getServletContext().setAttribute("init.error", error);
			}
		}
	@Override
	public void contextDestroyed(ServletContextEvent ctx)
		{
		SimpleDataSource ds=(SimpleDataSource)ctx.getServletContext().getAttribute("datasource");
		if(ds!=null) ds.close();
		CallTview.dispose();
		}
	}
