package com.github.lindenb.ngsproject.model;

public enum Table { REFERENCE,BAM,SAMPLE,PROJECT,USER,GROUP,VCF;
public  String sqlTable()
	{
	switch(this)
		{
		case USER: return "USERS";
		case GROUP: return "USERGROUP";
		default:return name().toUpperCase();
		}
	}
}
