package com.github.lindenb.ngsproject.model;

public enum Table { REFERENCE,BAM,SAMPLE,PROJECT,USER,GROUP;
public  String sqlTable()
	{
	switch(this)
		{
		case USER: return "USERS";
		case GROUP: return "USERGROUP";
		}
	return name().toUpperCase();
}
}
