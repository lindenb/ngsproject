package com.github.lindenb.ngsproject;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.lindenb.ngsproject.model.Model;

public class NGSProjectFilter implements Filter
	{
	private FilterConfig cfg;
	@Override
	public void init(FilterConfig cfg) throws ServletException
		{
		this.cfg=cfg;
		}
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException
		{
		//fix char encoding
		String enc=req.getCharacterEncoding();
		if(enc==null) {enc="ISO-8859-1";req.setCharacterEncoding(enc);}
		if(res.getCharacterEncoding()==null)
			{
			res.setCharacterEncoding(enc);
			}
		if(req instanceof HttpServletRequest)
			{
			String proxyBase="";
			/** quick hack for http redirection= UGLY */
			String x_forwarded_host=HttpServletRequest.class.cast(req).getHeader("x-forwarded-host");
			if(x_forwarded_host!=null)
				{
				//String proxy_prefix=(String)cfg.getInitParameter("proxy_prefix");
				//if(proxy_prefix==null) proxy_prefix="/glassfish";
				
				proxyBase=HttpServletRequest.class.cast(req).getScheme()+
						"://"+x_forwarded_host+"/glassfish"
						;
				}
			HttpServletRequest.class.cast(req).setAttribute("proxyBase", proxyBase);
			}
		
		/** redirect to error on init-context error */
		Object err;
		if((err=this.cfg.getServletContext().getAttribute("init.error"))!=null)
			{
			req.setAttribute("exception", err);
			if(res instanceof HttpServletResponse)
				{
				((HttpServletResponse)res).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			
	        this.cfg.getServletContext().getRequestDispatcher("/WEB-INF/jsp/config-error.jsp").
	        	forward(req, res);
	        return;
			}
		/** create user session */
		if(req instanceof HttpServletRequest)
			{
			Model model=(Model)req.getServletContext().getAttribute("model");
			if(model==null) throw new ServletException("model is null");
			UserPref currentUser=null;
			HttpSession session=HttpServletRequest.class.cast(req).getSession(true);
			currentUser=(UserPref)session.getAttribute("user");
			if(currentUser==null)
				{
				currentUser=new UserPref();
				session.setAttribute("user", currentUser);
				}
			}
		chain.doFilter(req, res);
		}

	@Override
	public void destroy() {
		this.cfg=null;
		}
	}
