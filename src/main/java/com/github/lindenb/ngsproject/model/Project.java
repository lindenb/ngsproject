package com.github.lindenb.ngsproject.model;

import java.util.List;

import net.sf.picard.util.Interval;


public interface Project extends ActiveRecord
	{
	public String getName();
	public String getDescription();
	public List<Bam> getBams();
	public List<Sample> getSamples();
	public Group getGroup();
	public Bam getBamById(long bamid);
	public List<VCF> getVcfs();
	public Linkage getGenotypes(Interval interval);
	public Reference getReference();
	}