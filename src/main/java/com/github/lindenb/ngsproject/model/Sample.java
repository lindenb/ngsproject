package com.github.lindenb.ngsproject.model;

import java.util.List;


public interface Sample extends ActiveRecord,Comparable<Sample>
	{
	public String getName();
	public List<Bam> getBams();
	public List<VCF> getVcfs();
	public List<Project> getProjects();
	}