package com.github.lindenb.ngsproject;

import java.io.PrintWriter;
import java.io.Writer;

import net.sf.picard.reference.IndexedFastaSequenceFile;

import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.vizbam.HTMLVizBam;

public class CallTview
	{
	
	public void display(Writer out,Bam bam,com.github.lindenb.vizbam.SAMSequencePosition position)
		{
		if(position==null || bam==null) return;
		IndexedFastaSequenceFile reference=null;
		try
			{
			reference=bam.getReference().getIndexedFastaSequenceFile();
			HTMLVizBam viz=new HTMLVizBam(bam.getFile(),reference);
			synchronized(IndexedFastaSequenceFile.class)
				{
				viz.align(position.getName(), position.getPosition());
				viz.print(out);
				}
			viz.close();
			}
		catch(Exception err)
			{
			PrintWriter w=new PrintWriter(out);
			err.printStackTrace(w);
			w.flush();
			}
			
		}
	}
