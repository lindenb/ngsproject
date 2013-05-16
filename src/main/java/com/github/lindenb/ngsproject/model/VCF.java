package com.github.lindenb.ngsproject.model;

import java.io.File;
import java.util.List;
import java.util.SortedSet;

import net.sf.picard.util.Interval;


public interface VCF extends ActiveRecord
	{
	public String getPath();
	public String getName();
	public String getDescription();
	public File getFile();
	public Reference getReference();
	public List<Sample> getSamples();
	public List<Project> getProjects();
	public Linkage getGenotypes(Interval interval);
	public SortedSet<Variation> getVariations(Interval interval);
	}