package com.github.lindenb.ngsproject.model;

public interface ActiveRecord {

	public long getId();
	public Model getModel();
	public Table getTable();
}
