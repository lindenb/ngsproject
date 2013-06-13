package com.github.lindenb.ngsproject.model;

import java.util.List;

import javax.sql.DataSource;

import net.sf.picard.reference.IndexedFastaSequenceFile;

public interface Model
	{

	public abstract DataSource getDataSource();

	/** GROUP ******************************************************************************/
	public abstract Group getGroupByName(String s);

	public abstract Group getGroupById(long id);

	/** SAMPLE ******************************************************************************/
	public abstract Sample getSampleById(long id);

	public abstract Sample getSampleByName(String s);

	public abstract List<Sample> getAllSamples();

	/** User ******************************************************************************/
	public abstract User getUserById(long id);

	public abstract User getUserByName(String s);

	public abstract List<User> getAllUsers();

	/** Project ******************************************************************************/
	public abstract Project getProjectById(long id);

	public abstract Project getProjectByName(String s);

	public abstract List<Project> getAllProjects();

	/** get project visible by this user */
	public abstract List<Project> getProjects(User user);

	public abstract List<Group> getAllGroups();

	/** Reference ******************************************************************************/
	public abstract Reference getReferenceById(long id);

	public abstract Reference getReferenceByName(String s);

	public abstract List<Reference> getAllReferences();

	/** VCF ******************************************************************************/
	public abstract VCF getVcfById(long id);

	public abstract VCF getVcfByPath(String path);

	public abstract List<VCF> getAllVCFs();

	/** Bam ******************************************************************************/
	public abstract Bam getBamById(long id);

	public abstract Bam getBamByPath(String path);

	public abstract List<Bam> getAllBams();

	public abstract IndexedFastaSequenceFile getIndexedFastaSequenceFileByPath(
			String fastaPath);

	public abstract void dispose();

	}