package com.github.lindenb.ngsproject.model;

import java.util.List;


public interface User extends ActiveRecord
{
public String getName();
public String getSha1Sum();
public boolean isAdmin();
public List<Group> getGroups();
}
