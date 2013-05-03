package com.github.lindenb.ngsproject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.github.lindenb.ngsproject.model.Bam;

public class CopyOfCallTview
{
private static class ConsummeInputStreamThread extends Thread	
   {
   private InputStream is;
   public ConsummeInputStreamThread(InputStream is)
    	{
        this.is = is;
    	}
  
    @Override
    public void run()
	    {
        try
	        {
        	while (is.read()!=-1);
	        }
        catch (IOException ioe)
              {
        	  ioe.printStackTrace();  
              }
	    }
    
	}


protected Process proc=null;

public void display(Writer out,Bam bam,String pos)
	{
	ConsummeInputStreamThread err=null;
	try
		{
		List<String> args=new ArrayList<String>();
		args.add("/commun/data/packages/samtools-0.1.19/samtools");
		args.add("tview");
		args.add("-d");args.add("T");
		args.add(bam.getPath());
		args.add(bam.getReference().getPath());
		
		if(pos!=null && pos.matches("[^\\:]+\\:[0-9]+"))
			{
			args.add("-p");args.add(pos);
			}
		
		this.proc=Runtime.getRuntime().exec(
				args.toArray(new String[args.size()]),
				new String[]{},
				null
				);
	    err=new ConsummeInputStreamThread(proc.getErrorStream());
	    err.start();
	    int c;
	    InputStream os=proc.getInputStream();
	    while((c=os.read())!=-1)
	    	{
	    	out.write((char)c);
	    	}
	    out.flush();
	    err.join();
		}
	catch(Throwable t)
		{	
		
		}
	}
}
