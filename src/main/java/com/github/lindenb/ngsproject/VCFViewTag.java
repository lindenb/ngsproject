package com.github.lindenb.ngsproject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.broad.tribble.readers.AsciiLineReader;
import org.broad.tribble.readers.TabixReader;

import net.sf.samtools.util.BlockCompressedInputStream;

import com.github.lindenb.ngsproject.model.VCF;
import com.github.lindenb.vizbam.SAMSequenceInterval;
import com.github.lindenb.vizbam.locparser.LocParser;

public class VCFViewTag extends SimpleTagSupport
	{
	private boolean meta=false;
	private VCF vcf;
	private int limit=1000;
	private String intervalStr=null;
	private boolean escapeXml=true;
	
	private void print(JspWriter w,String s)throws IOException
		{
		if(isEscapeXml())
			{
			for(int i=0;i< s.length();i++)
				{
				char c=s.charAt(i);
				switch(c)
					{
					case '<': w.print("&lt;");break;
					case '>': w.print("&gt;");break;
					case '&': w.print("&amp;");break;
					case '\"': w.print("&apos;");break;
					case '\'': w.print("&quot;");break;
					default: w.print(c);break;
					}
				}
			}
		else
			{
			w.print(s);
			}
		}
	
	private void print(
			Pattern tab,
			JspWriter w,
			String line,
			Set<Integer> visible_columns
			) throws IOException
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
			print(w,tokens[i]);
			}
		w.println();
		}
	
	public void setVcf(VCF vcf)
		{
		this.vcf = vcf;
		}
	
	public VCF getVcf()
		{
		return vcf;
		}
	
	
	public void setInterval(String interval)
		{
		this.intervalStr = interval;
		}
	
	public String getInterval()
		{
		return intervalStr;
		}
	
	public void setMeta(boolean meta)
		{
		this.meta = meta;
		}
	
	public boolean isMeta()
		{
		return meta;
		}
	
	public void setLimit(int limit)
		{
		this.limit = limit;
		}
	
	public int getLimit()
		{
		return limit;
		}
	public void setEscapeXml(boolean escapeXml)
		{
		this.escapeXml = escapeXml;
		}
	
	public boolean isEscapeXml()
		{
		return escapeXml;
		}
	
	
	@Override
	public void doTag() throws JspException, IOException
		{
		if(this.vcf==null || !this.vcf.isIndexedWithTabix()) return;
		
		JspWriter w=getJspContext().getOut();
		PageContext pageCtxt=(PageContext)this.getJspContext();
		
		boolean vcf_visible= Functions.visible((HttpServletRequest)pageCtxt.getRequest(), vcf);
		
		SAMSequenceInterval interval=LocParser.parseInterval(
					vcf.getReference().getIndexedFastaSequenceFile().getSequenceDictionary(),
					getInterval(), true);
		if(interval==null)
			{
			return;
			}
		
		
		AsciiLineReader r=null;
		TabixReader tabix=null;
		try
			{
			r=new AsciiLineReader(new BlockCompressedInputStream(vcf.getFile()));
			String line;
			Set<Integer> visible_columns=new HashSet<Integer>();
			Pattern tab=Pattern.compile("[\t]");
			while((line=r.readLine())!=null)
				{
				if(line.isEmpty()) continue;
				if(!line.startsWith("#")) break;
				if(isMeta() && vcf_visible)
					{
					print(w,line);
					w.println();
					}
				if(line.startsWith("#CHROM"))
					{
					String tokens[]=tab.split(line);
					for(int i=9;i< tokens.length;++i)
						{
						if(Functions.visible(
								(HttpServletRequest)pageCtxt.getRequest(),
								vcf.getModel().getSampleByName(tokens[i]))
								)
							{
							visible_columns.add(i);
							}
						}
					if(vcf_visible) print(tab,w,line,visible_columns);
					break;
					}
				}
			
			r.close();
			r=null;
			int found_hits=0;
			tabix=new TabixReader(vcf.getPath());
			TabixReader.Iterator iter=tabix.query(
					interval.getName()+":"+interval.getStart()+"-"+interval.getEnd());
			while(iter!=null && (line=iter.next())!=null && limit>0L)
				{
				--limit;
				if(vcf_visible) print(tab,w,line,visible_columns);
				++found_hits;
				}
			tabix.close();
			tabix=null;
			if(!vcf_visible && found_hits>0)
				{
				w.print("Found "+found_hits+" Hit(s) in private vcf.id:"+vcf.getId());
				}
			}
		catch(Exception err)
			{
			throw new IOException(err);
			}
		finally
			{
			if(w!=null) try { w.flush();} catch(Exception err){}
			if(tabix!=null) try { tabix.close();} catch(Exception err){}
			}
		}
	
	
	}
