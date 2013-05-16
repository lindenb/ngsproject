package com.github.lindenb.ngsproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broad.tribble.readers.TabixReader;

import net.sf.picard.io.IoUtil;
import com.github.lindenb.ngsproject.model.Model;
import com.github.lindenb.ngsproject.model.VCF;
import com.github.lindenb.vizbam.SAMSequenceInterval;
import com.github.lindenb.vizbam.locparser.LocParser;

@SuppressWarnings("serial")
public class VCFViewServlet extends HttpServlet
	{
	private void print(
			Pattern tab,
			PrintWriter w,
			String line,
			Set<Integer> visible_columns
			)
		{
		String tokens[]=tab.split(line);
		for(int i=0;i< tokens.length;++i)
			{
			if(i==8 && visible_columns.isEmpty()) break;
			if(i>8 && !visible_columns.contains(i))
				{
				continue;
				}
			if(i>0) w.print('\t');
			w.print(tokens[i]);
			}
		w.println();
		}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
		{
		Model model=(Model)req.getServletContext().getAttribute("model");
		if(model==null) throw new ServletException("model is null");
		String param=req.getParameter("vcf-id");
		if(param==null) throw new ServletException("vcf-id missing");
		long vcf_id=Long.parseLong(param);
		VCF vcf=model.getVcfById(vcf_id);
		if(!Functions.visible(req, vcf)) vcf=null;
		if(vcf==null) throw new ServletException("undefined bam id:"+vcf_id);
		SAMSequenceInterval interval=null;
		
		if((param=req.getParameter("interval"))!=null)
			{
			interval=LocParser.parseInterval(
					vcf.getReference().getIndexedFastaSequenceFile().getSequenceDictionary(),
					param, true);
			}
		
		
		PrintWriter w=resp.getWriter();
		BufferedReader r=null;
		TabixReader tabix=null;
		long limit=1000L;
		try
			{
			r=IoUtil.openFileForBufferedUtf8Reading(vcf.getFile());
			String line;
			Set<Integer> visible_columns=new HashSet<Integer>();
			Pattern tab=Pattern.compile("[\t]");
			resp.setContentType("text/plain");
			while((line=r.readLine())!=null)
				{
				if(line.isEmpty()) continue;
				if(!line.startsWith("#")) break;
				w.println(line);
				if(line.startsWith("#CHROM"))
					{
					String tokens[]=tab.split(line);
					for(int i=9;i< tokens.length;++i)
						{
						if(Functions.visible(req, model.getSampleByName(tokens[i])))
							{
							visible_columns.add(i);
							}
						}
					print(tab,w,line,visible_columns);
					break;
					}
				}
			
			if(interval==null)
				{
				while((line=r.readLine())!=null && limit>0L)
					{
					--limit;
					print(tab,w,line,visible_columns);
					}
				r.close();
				r=null;
				}
			else
				{
				r.close();
				r=null;
				tabix=new TabixReader(vcf.getPath());
				TabixReader.Iterator iter=tabix.query(
						interval.getName()+":"+interval.getStart()+"-"+interval.getEnd());
				while((line=iter.next())!=null && limit>0L)
					{
					--limit;
					print(tab,w,line,visible_columns);
					}
				tabix.close();
				tabix=null;
				}
			}
		catch(Exception err)
			{
			throw new IOException(err);
			}
		finally
			{
			if(w!=null) try { w.flush();} catch(Exception err){}
			if(w!=null) try { w.close();} catch(Exception err){}
			if(tabix!=null) try { tabix.close();} catch(Exception err){}
			}
		}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
		{
		this.doPost(req, resp);
		}
	}
