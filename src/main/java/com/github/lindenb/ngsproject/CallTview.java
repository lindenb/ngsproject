package com.github.lindenb.ngsproject;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.SAMSequenceDictionary;

import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.ngsproject.model.Reference;
import com.github.lindenb.vizbam.HTMLVizBam;
import com.github.lindenb.vizbam.SAMSequencePosition;
import com.github.lindenb.vizbam.locparser.LocParser;

public class CallTview
{
private static final Map<String,IndexedFastaSequenceFile>  PATH2REF=java.util.Collections.synchronizedMap(
		new HashMap<String,IndexedFastaSequenceFile>());

public void display(Writer out,Bam bam,String pos)
	{
	if(pos==null || bam==null) return;
	Reference ref=bam.getReference();
	String fastaPath=null;
	if(ref==null || (fastaPath=ref.getPath())==null) return ;
	try
		{
		synchronized (PATH2REF)
			{
			IndexedFastaSequenceFile reference=null;
			reference=PATH2REF.get(fastaPath);
			if(reference==null)
				{
				reference=new IndexedFastaSequenceFile(new File(fastaPath));
				
				}
			
		
			SAMSequenceDictionary dict=reference.getSequenceDictionary();
			SAMSequencePosition position=LocParser.parseOne(dict, pos, true);
			if(position==null)
				{
				position=new SAMSequencePosition(dict.getSequence(0), 1);
				}
			HTMLVizBam viz=new HTMLVizBam(bam.getFile(),reference);
			viz.align(position.getName(), position.getPosition());
			viz.print(out);
			viz.close();
			
			PATH2REF.put(fastaPath, reference);
			}
		}
	catch(Exception err)
		{
		PrintWriter w=new PrintWriter(out);
		err.printStackTrace(w);
		w.flush();
		
		}
		
	}
public static void dispose()
	{
	synchronized (PATH2REF)
		{
		PATH2REF.clear();
		}
	}
}
