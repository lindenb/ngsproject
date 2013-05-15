package com.github.lindenb.ngsproject.model;

import java.util.List;

public interface Group  extends ActiveRecord
	{
	public String getName();
	public List<User> getUsers();
	public boolean isPublic();
	}