package com.github.lindenb.ngsproject;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import com.github.lindenb.ngsproject.model.DefaultModel;
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

				
				String jdbcDriver=ctx.getServletContext().getInitParameter("jdbc.driver");
				if(jdbcDriver==null)
					{
					error=new ServletException("parameter 'jdbc.driver' undefined");
					break;
					}
				
				try
					{
					Class.forName(jdbcDriver).newInstance();
					}
				catch(Exception err)
					{
					error=err;
					break;
					}
				
				String jdbcUri=ctx.getServletContext().getInitParameter("jdbc.uri");
				if(jdbcUri==null)
					{
					error=new ServletException("parameter 'jdbc.uri' undefined");
					break;
					}
				
			
				SimpleDataSource ds=new SimpleDataSource(jdbcUri);
				ds.setDefaultSchema("NGSPROJECTS");
				Connection con=ds.getConnection();
				con.close();
				Model model=new DefaultModel(ds);
				
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
		Model model=(Model)ctx.getServletContext().getAttribute("model");
		if(model!=null) model.dispose();

		}
	}
