package com.github.lindenb.ngsproject;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

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

		chain.doFilter(req, res);
		}

	@Override
	public void destroy() {
		this.cfg=null;
		}
	}
