package com.github.lindenb.ngsproject.model;

import java.util.List;


public interface Sample extends ActiveRecord
	{
	public String getName();
	public List<Project> getProjects();
}