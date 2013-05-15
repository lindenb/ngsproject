package com.github.lindenb.ngsproject.model;

import java.io.File;
import java.util.List;


public interface Bam extends ActiveRecord
	{
	public String getPath();
	public File getFile();
	public String getName();
	public Sample getSample();
	public Reference getReference();
	public List<Project> getProjects();
	}