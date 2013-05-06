package com.github.lindenb.ngsproject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.picard.filter.SamRecordFilter;
import net.sf.picard.util.Interval;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMFileReader.ValidationStringency;
import net.sf.samtools.SAMFileWriterFactory;

import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.ngsproject.model.Model;
import com.github.lindenb.vizbam.SAMSequenceInterval;
import com.github.lindenb.vizbam.locparser.LocParser;

@SuppressWarnings("serial")
public class SAMViewServlet extends HttpServlet
	{
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
		{
		Model model=(Model)req.getServletContext().getAttribute("model");
		if(model==null) throw new ServletException("model is null");
		String param=req.getParameter("bam-id");
		if(param==null) throw new ServletException("bam-id missing");
		long bam_id=Long.parseLong(param);
		Bam bam=model.getBamById(bam_id);
		boolean print_unmapped=false;
		if(bam==null) throw new ServletException("undefined bam id:"+bam_id);
		SAMSequenceInterval interval=null;
		LocParser p;
		if((param=req.getParameter("interval"))!=null)
			{
			interval=LocParser.parseInterval(
					bam.getReference().getIndexedFastaSequenceFile().getSequenceDictionary(),
					param, true);
			}
		
		if((param=req.getParameter("unmapped"))!=null && param.equals("true"))
			{
			print_unmapped=true;
			}
		
		Integer min_qual=null;
		if((param=req.getParameter("qual"))!=null)
			{
			min_qual=Integer.parseInt(param);
			}
		int filter_flag=0;
		if(req.getParameter("filter")!=null)
			{
			for(String value:req.getParameterValues("filter"))
				{
				filter_flag|=Integer.parseInt(value);
				}
			}
		
		int require_flag=0;
		if(req.getParameter("require")!=null)
			{
			for(String value:req.getParameterValues("require"))
				{
				require_flag|=Integer.parseInt(value);
				}
			}

		
		
		SAMFileWriterFactory sfwf=new SAMFileWriterFactory();
		File tmpDir=(File)req.getServletContext().getAttribute(ServletContext.TEMPDIR);
		if(tmpDir!=null)
			{
			sfwf.setTempDirectory(tmpDir);
			}
		SAMFileReader sfr=null;
		SAMFileWriter sfw=null;
		OutputStream out=resp.getOutputStream();
		SAMRecordIterator iter=null;
		long limit=1000L;
		try
			{
			sfr=new SAMFileReader(bam.getFile());
			sfr.setValidationStringency(ValidationStringency.SILENT);
			if(print_unmapped)
				{
				iter=sfr.queryUnmapped();
				}
			else if(interval!=null)
				{
				iter=sfr.queryOverlapping(interval.getName(), interval.getStart(), interval.getEnd());
				}
			else
				{
				iter=sfr.iterator();
				}
			resp.setContentType("text/plain");
			
			sfw=sfwf.makeSAMWriter(sfr.getFileHeader(), true, out);
			
			while(iter.hasNext() && limit>0L)
				{
				SAMRecord rec=iter.next();
				--limit;
				if(filter_flag!=0 && (rec.getFlags() & filter_flag) != 0)
					{
					continue;
					}
				
				if(require_flag!=0 && (rec.getFlags() & require_flag) == 0)
					{
					continue;
					}
				
				if(min_qual!=null && min_qual > rec.getMappingQuality())
					{
					continue;
					}
				
				sfw.addAlignment(rec);
				}
			}
		catch(Exception err)
			{
			throw new IOException(err);
			}
		finally
			{
			if(iter!=null) try { iter.close();} catch(Exception err){}
			if(sfw!=null) try { sfw.close();} catch(Exception err){}
			if(sfr!=null) try { sfr.close();} catch(Exception err){}
			}
		out.flush();
		out.close();
		}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
		{
		this.doPost(req, resp);
		}
	}
