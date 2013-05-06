package com.github.lindenb.ngsproject.model;

import java.util.List;


public interface Project extends ActiveRecord
{
public String getName();
public String getDescription();
public List<Bam> getBams();
public Group getGroup();
public Bam getBamById(long bamid);
}