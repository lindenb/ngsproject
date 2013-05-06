package com.github.lindenb.ngsproject;

import java.io.PrintWriter;
import java.io.Writer;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.SAMSequenceDictionary;

import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.ngsproject.model.Reference;
import com.github.lindenb.vizbam.HTMLVizBam;
import com.github.lindenb.vizbam.SAMSequencePosition;
import com.github.lindenb.vizbam.locparser.LocParser;

public class CallTview
{

public void display(Writer out,Bam bam,String pos)
	{
	if(pos==null || bam==null) return;
	Reference ref=bam.getReference();
	IndexedFastaSequenceFile reference=null;

	try
		{
		if(ref==null || (reference=ref.getIndexedFastaSequenceFile())==null) return ;
		SAMSequenceDictionary dict=reference.getSequenceDictionary();
		SAMSequencePosition position=LocParser.parseOne(dict, pos, true);
		if(position==null)
			{
			position=new SAMSequencePosition(dict.getSequence(0), 1);
			}
	
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
