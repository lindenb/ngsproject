package com.github.lindenb.ngsproject.model;

import java.util.List;


public interface User extends ActiveRecord
{
public String getName();
public List<Group> getGroups();
}
