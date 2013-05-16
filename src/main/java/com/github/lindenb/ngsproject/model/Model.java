package com.github.lindenb.ngsproject.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.broad.tribble.readers.TabixReader;

import net.sf.picard.io.IoUtil;
import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.util.Interval;

public class Model
{
private final Map<String,IndexedFastaSequenceFile>  path2reference=java.util.Collections.synchronizedMap(
		new HashMap<String,IndexedFastaSequenceFile>());
	

private interface SQLRowExtractor<T>
	{
	public T extract(ResultSet row) throws SQLException;
	}

private static final  SQLRowExtractor<String>  STRING_EXTRACTOR = new SQLRowExtractor<String>()
	{	
	public String extract(ResultSet row) throws SQLException
		{
		return row.getString(1);
		}
	};

	
	private static final  SQLRowExtractor<Long>  LONG_EXTRACTOR = new SQLRowExtractor<Long>()
		{	
		public Long extract(ResultSet row) throws SQLException
			{
			return row.getLong(1);
			}
		};	
	
	private static final  SQLRowExtractor<Integer>  INT_EXTRACTOR = new SQLRowExtractor<Integer>()
		{	
		public Integer extract(ResultSet row) throws SQLException
			{
			return row.getInt(1);
			}
		};	
		
		
	private Group publicGroup=new Group()
		{
		public long getId() {return 0L;};
		public String getName() {return "public";};
		public boolean isPublic() {return true;};
		public Model getModel() {return Model.this;};
		@Override
		public List<User> getUsers() { return getModel().getAllUsers(); }
		@Override
		public Table getTable() {
				return Table.GROUP;
				}
		};
		
	
private DataSource dataSource;
public Model(DataSource dataSource)
	{
	this.dataSource=dataSource;
	}

public DataSource getDataSource()
	{
	return dataSource;
	}



   private static final java.lang.reflect.Method GET_MODEL;
   private static final java.lang.reflect.Method GET_ID;
    static{
       try{
       
    	   GET_MODEL = ActiveRecord.class.getDeclaredMethod("getModel",new Class[]{});
    	   GET_ID = ActiveRecord.class.getDeclaredMethod("getId",new Class[]{});
       }catch( Exception e){
        throw new  Error(e.getMessage());
       }
    }

protected class DefaultActiveRecord
	implements ActiveRecord,java.lang.reflect.InvocationHandler
	{
	private Map<String,Object> eagerData=new HashMap<String, Object>();
	private Table table;
	private long id;
	DefaultActiveRecord(Table table,long id)
		{
		this.table=table;
		this.id=id;
		}
	
	@Override
	public Table getTable() {
		return table;
		}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable
		{
		String methodName=method.getName();
		if(method.equals(GET_ID)) return this.id;
		if(methodName.equals("hashCode")) return this.hashCode();
		if(methodName.equals("equals")) return this.equals(args[0]);
		if(methodName.equals("getTable")) return this.table;

		if(method.equals(GET_MODEL)) return Model.this;
		switch(this.table)
			{
			case SAMPLE:
				{
				if(methodName.equals("getName") || methodName.equals("toString"))
					{
					return getString(this,"name");
					}
				if(methodName.equals("getBams"))
					{
					return manyToMany(Bam.class,
							"select id from bam where sample_id=?",
							this.id
							);
					}
				if( methodName.equals("getVcfs"))
					{
					return manyToMany(
						VCF.class,
						"select distinct vcf_id from VCF2SAMPLE where sample_id=?",
						this.id);
					}
				if( methodName.equals("getProjects"))
					{
					return manyToMany(
							Project.class,
							"select distinct T1.project_id from " +
							"PROJECT2BAM as T1,BAM as T2 where " +
							"T2.sample_id=? and T1.bam_id=T2.id",
							this.id);
					}
				if( methodName.equals("compareTo"))
					{
					Sample me=(Sample)this;
					Sample other=Sample.class.cast(args[0]);
					return me.getName().compareTo(other.getName());
					}
				break;
				}
			case USER:
				{
				if(methodName.equals("getName") || methodName.equals("toString"))
					{
					return getString(this,"name");
					}
				if(methodName.equals("getSha1Sum"))
					{
					return getString(this,"sha1sum");
					}
				if(methodName.equals("isAdmin"))
					{
					return getInt(this,"IS_ADMIN")==1;
					}
				if(methodName.equals("getGroups"))
					{
					if(getInt(this,"IS_ADMIN")==1) return this.getModel().getAllGroups();
					return manyToMany(Group.class,
							"select distinct group_id from user2group where user_id=?",
							this.id
							);
					}
				break;
				}
			case GROUP:
				{
				if(methodName.equals("getName") || methodName.equals("toString"))
					{
					return getString(this,"name");
					}
				if(methodName.equals("getUsers"))
					{
					return manyToMany(Group.class,
							"select distinct user_id from user2group where group_id=?",
							this.id
							);
					}
				if(methodName.equals("isPublic"))
					{
					return getInt(this,"IS_PUBLIC")==1;
					}
				break;
				}
			case BAM:
				{
				if( methodName.equals("getName") ||
					methodName.equals("getPath") || 
					methodName.equals("toString") ||  
					methodName.equals("getFile") )
					{
					String path= getString(this,"path");
					if( methodName.equals("getFile")) return new File(path);
					if( methodName.equals("getName")) return new File(path).getName();
					return path;
					}
				if( methodName.equals("getSample"))
					{
					return manyToOne(
							this,Sample.class,"sample_id",id
							);
					}
				if(methodName.equals("getReference"))
					{
					return manyToOne(
							this,Reference.class,"reference_id",id
							);
					}
				if( methodName.equals("getProjects"))
					{
					return manyToMany(Project.class,
							"select distinct project_id from PROJECT2BAM where bam_id=?",
							this.id
							);
					}
				break;
				}
			case VCF:
				{
				if( methodName.equals("getName") ||
					methodName.equals("getPath") || 
					methodName.equals("toString") ||  
					methodName.equals("getFile") )
					{
					String path= getString(this,"path");
					if( methodName.equals("getFile")) return new File(path);
					if( methodName.equals("getName")) return new File(path).getName();
					return path;
					}
				
				if( methodName.equals("getDescription"))
					{
					return getString(this,"description");
					}
				if(methodName.equals("getReference"))
					{
					return manyToOne(
							this,Reference.class,"reference_id",id
							);
					}
				
				if( methodName.equals("getSamples"))
					{
					return manyToMany(
							Sample.class,
							"select distinct sample_id from VCF2SAMPLE where vcf_id=?",
							id
							);
					}
				
				if( methodName.equals("getProjects"))
					{
					return manyToMany(
							Project.class,
							"select distinct project_id from VCF2PROJECT where vcf_id=?",
							this.id
							);
					}
				if( methodName.equals("getGenotypes"))
					{
					return getLinkage((VCF)this,
							(Interval)args[0],
							((VCF)this).getSamples()
							); 
					}
				if( methodName.equals("getVariations"))
					{
					return getVariations((VCF)this,
							(Interval)args[0]
							); 
					}
				break;
				}

			case PROJECT:
				{
				if(methodName.equals("getName") || methodName.equals("toString"))
					{
					return getString(this,"name");
					}
				if(methodName.equals("getDescription"))
					{
					return getString(this,"description");
					}
				if( methodName.equals("getBams") ||
					methodName.equals("getBamById"))
					{
					List<Bam> L=manyToMany(
							Bam.class,
							"select distinct bam_id from project2bam where project_id=?",
							id);
					if(methodName.equals("getBamById"))
						{
						Long bamId=(Long)args[0];
						if(bamId==null || bamId<1L) return null;
						for(Bam b:L) 
							{
							if(b.getId()==bamId) return b;
							}
						return null;
						}
					return L;
					}
				
				if( methodName.equals("getVcfs"))
					{
					return manyToMany(
						VCF.class,
						"select distinct vcf_id from VCF2PROJECT where project_id=?",
						id);
					}
				
				if(methodName.equals("getGroup"))
					{
					Group g= manyToOne(
							this,Group.class,"group_id",id
							);
					if(g==null || g.getId()<=0) return publicGroup;
					return g;
					}
				
				if( methodName.equals("getSamples"))
					{
					return manyToMany(
							Sample.class,
							"select distinct T2.sample_id from " +
							"PROJECT2BAM as T1,BAM as T2 where " +
							"T1.project_id=? and T1.bam_id=T2.id",
							this.id);
					}
				
				if( methodName.equals("getGenotypes"))
					{
					return getLinkage((VCF)this,
							(Interval)args[0],
							((Project)this).getSamples()
							); 
					}

				if( methodName.equals("getReference"))
					{
					List<Bam> bams=((Project)this).getBams();
					if(bams.isEmpty()) return null;
					return bams.get(0).getReference();
					}
				
				break;
				}
			case REFERENCE:
				{
				if(methodName.equals("getName") || methodName.equals("toString"))
					{
					return getString(this,"name");
					}
				if(methodName.equals("getDescription"))
					{
					return getString(this,"description");
					}
				if( methodName.equals("getPath") ||
					methodName.equals("getFile") ||
					methodName.equals("getIndexedFastaSequenceFile")
					)
					{
					String path= getString(this,"path");
					if( methodName.equals("getFile")) return new File(path);
					if( methodName.equals("getIndexedFastaSequenceFile"))
						{
						return Model.this.getIndexedFastaSequenceFileByPath(path);
						}
					return path;
					}
				break;
				}
			}
		if(methodName.equals("toString")) return this.table.sqlTable()+"/"+this.id;

		throw new RuntimeException("unexpected invocation exception: " +method);
		}
	
	
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + table.hashCode();
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
		}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		DefaultActiveRecord other = (DefaultActiveRecord) obj;
		if (table != other.table) return false;
		if (id != other.id) return false;
		return true;
		}
	@Override
	public Model getModel()
		{
		return Model.this;
		}
	@Override
	public long getId() {
		return this.id;
		}
	@Override
	public String toString() {
		return this.table.name()+"("+getId()+")";
		}
	}

private String getString(DefaultActiveRecord record,String column)
	{
	return getObject(record,column,STRING_EXTRACTOR);
	}


private Long getLong(DefaultActiveRecord record,String column)
	{
	return getObject(record,column,LONG_EXTRACTOR);
	}

private Integer getInt(DefaultActiveRecord record,String column)
	{
	return getObject(record,column,INT_EXTRACTOR);
	}


@SuppressWarnings("unchecked")
private <T> T getObject(DefaultActiveRecord record,String column,SQLRowExtractor<T> extractor)
	{
	if(record.eagerData.containsKey(column))
		{
		return (T)record.eagerData.get(column);
		}
	Connection con=null;
	PreparedStatement pstmt=null;
	ResultSet row=null;
	
	try{
		T o=null;
		con=getDataSource().getConnection();
		pstmt=con.prepareStatement(
				"select "+column+" from "+record.table.sqlTable()+" where id=?");
		pstmt.setLong(1, record.id);
		row=pstmt.executeQuery();
		while(row.next())
			{
			o=extractor.extract(row);
			break;
			}
		record.eagerData.put(column,o);
		return o;
		}
	catch(SQLException err)
		{
		err.printStackTrace(System.out);
		throw new RuntimeException(err);
		}
	finally
		{
		if(row!=null) try {row.close();} catch(SQLException err) { }
		if(pstmt!=null) try {pstmt.close();} catch(SQLException err) { }
		if(con!=null) try {con.close();} catch(SQLException err) { }
		}
	}

private <T extends ActiveRecord> T manyToOne(
		DefaultActiveRecord record,
		Class<T> clazz,
		String column,
		long id
		)
	{
	Long fkey=getLong(record, column);
	if(fkey==null) return null;
	return wrap(clazz,fkey);
	}


private <T extends ActiveRecord> List<T> manyToMany(Class<T> clazz,String sql,long id)
	{
	List<T> array=new ArrayList<T>();
	Connection con=null;
	PreparedStatement pstmt=null;
	ResultSet row=null;
	
	try{
		con=getDataSource().getConnection();
		pstmt=con.prepareStatement(
				sql
				);
		pstmt.setLong(1, id);
		row=pstmt.executeQuery();
		while(row.next())
			{
			array.add(wrap(clazz,row.getLong(1)));
			}
		return array;
		}
	catch(SQLException err)
		{
		err.printStackTrace(System.out);
		throw new RuntimeException(err);
		}
	finally
		{
		if(row!=null) try {row.close();} catch(SQLException err) { }
		if(pstmt!=null) try {pstmt.close();} catch(SQLException err) { }
		if(con!=null) try {con.close();} catch(SQLException err) { }
		}
	}

private <T extends ActiveRecord> List<T> getAllObjects(Class<T> clazz)
	{
	List<T> array=new ArrayList<T>();
	Connection con=null;
	PreparedStatement pstmt=null;
	ResultSet row=null;
	
	try{
		con=getDataSource().getConnection();
		pstmt=con.prepareStatement(
				"select id from "+ class2table(clazz).sqlTable()
				);
		row=pstmt.executeQuery();
		while(row.next())
			{
			array.add(wrap(clazz,row.getLong(1)));
			}
		return array;
		}
	catch(SQLException err)
		{
		err.printStackTrace(System.out);
		throw new RuntimeException(err);
		}
	finally
		{
		if(row!=null) try {row.close();} catch(SQLException err) { }
		if(pstmt!=null) try {pstmt.close();} catch(SQLException err) { }
		if(con!=null) try {con.close();} catch(SQLException err) { }
		}
	}

private static Table class2table(Class<? extends ActiveRecord> C)
	{
	if(C==Bam.class) return Table.BAM;
	if(C==Sample.class) return Table.SAMPLE;
	if(C==User.class) return Table.USER;
	if(C==Group.class) return Table.GROUP;
	if(C==Reference.class) return Table.REFERENCE;
	if(C==Project.class) return Table.PROJECT;
	throw new IllegalArgumentException(String.valueOf(C));
	}

@SuppressWarnings("unchecked")
private <T extends ActiveRecord> T wrap(Class<T> C,long id)
	{
	return (T) Proxy.newProxyInstance(
			C.getClassLoader(),
            new Class[] { C },
            new DefaultActiveRecord(class2table(C),id)
            );
	}

private boolean contains(Table table,long id)
	{
	Connection con=null;
	PreparedStatement pstmt=null;
	ResultSet row=null;
	
	try{
		con=getDataSource().getConnection();
		pstmt=con.prepareStatement(
				"select count(*) from "+ table.sqlTable()+" where id=?");
		pstmt.setLong(1, id);
		row=pstmt.executeQuery();
		while(row.next())
			{
			return row.getInt(1)>0;
			}
		return false;
		}
	catch(SQLException err)
		{
		throw new RuntimeException(err);
		}
	finally
		{
		if(row!=null) try {row.close();} catch(SQLException err) { }
		if(pstmt!=null) try {pstmt.close();} catch(SQLException err) { }
		if(con!=null) try {con.close();} catch(SQLException err) { }
		}
	}

private Long getIdByName(Table table,String column,String id)
	{
	if(id==null || id.isEmpty()) return null;
	Connection con=null;
	PreparedStatement pstmt=null;
	ResultSet row=null;
	
	try{
		con=getDataSource().getConnection();
		pstmt=con.prepareStatement(
				"select id from "+ table.sqlTable()+" where "+column+"=?");
		pstmt.setString(1, id);
		row=pstmt.executeQuery();
		while(row.next())
			{
			return row.getLong(1);
			}
		return null;
		}
	catch(SQLException err)
		{
		throw new RuntimeException(err);
		}
	finally
		{
		if(row!=null) try {row.close();} catch(SQLException err) { }
		if(pstmt!=null) try {pstmt.close();} catch(SQLException err) { }
		if(con!=null) try {con.close();} catch(SQLException err) { }
		}
	}


/** GROUP ******************************************************************************/
public Group getGroupByName(String s)
	{
	if(s==null || s.trim().isEmpty()) return null;
	Long id=getIdByName(Table.GROUP,"name",s);
	return id==null?null:wrap(Group.class, id);
	}
public Group getGroupById(long id)
	{
	return contains(Table.GROUP,id)?wrap( Group.class, id):null;
	}

/** SAMPLE ******************************************************************************/
public Sample getSampleById(long id)
	{
	return contains(Table.SAMPLE,id)?wrap(Sample.class, id):null;
	}

public Sample getSampleByName(String s)
	{
	if(s==null || s.trim().isEmpty()) return null;
	Long id=getIdByName(Table.SAMPLE,"name",s);
	return id==null?null:wrap(Sample.class, id);
	}


public List<Sample> getAllSamples()
	{
	return getAllObjects(Sample.class);
	}
/** User ******************************************************************************/
public User getUserById(long id)
	{
	return contains(Table.USER,id)?wrap(User.class, id):null;
	}
public User getUserByName(String s)
	{
	if(s==null || s.trim().isEmpty()) return null;
	Long id=getIdByName(Table.USER,"name",s);
	return id==null?null:wrap(User.class, id);
	}

public List<User> getAllUsers()
	{
	return getAllObjects(User.class);
	}
/** Project ******************************************************************************/
public  Project getProjectById(long id)
	{
	return contains(Table.PROJECT,id)?wrap(Project.class, id):null;
	}

public  Project getProjectByName(String s)
	{
	if(s==null || s.trim().isEmpty()) return null;
	Long id=getIdByName(Table.PROJECT,"name",s);
	return id==null?null:wrap(Project.class, id);
	}


public List< Project> getAllProjects()
	{
	return getAllObjects(Project.class);
	}



/** get project visible by this user */
public List< Project> getProjects(User user)
	{
	
	if(user!=null && user.isAdmin())
		{
		return getAllProjects();
		}

	List<Project> ret=new ArrayList<Project>();
	List<Group> groups=null;
	if(user!=null)
			{
			groups=user.getGroups();
			}
	else
			{
			groups=new ArrayList<Group>();
			}
	for(Project prj:getAllProjects())
		{
		Group g=prj.getGroup();
		if(g==null || g.isPublic())
			{
			ret.add(prj);
			continue;
			}
		
		for(Group g2:groups)
			{
			if(g==null || g.getId()==g2.getId())
				{
				ret.add(prj);
				}
			}
		}
	return ret;
	}


public List< Group> getAllGroups()
	{
	return getAllObjects(Group.class);
	}

/** Reference ******************************************************************************/
public  Reference getReferenceById(long id)
	{
	return contains(Table.REFERENCE,id)?wrap(Reference.class, id):null;
	}

public Reference getReferenceByName(String s)
	{
	if(s==null || s.trim().isEmpty()) return null;
	Long id=getIdByName(Table.REFERENCE,"name",s);
	return id==null?null:wrap(Reference.class, id);
	}
public List< Reference> getAllReferences()
	{
	return getAllObjects(Reference.class);
	}
/** VCF ******************************************************************************/
public  VCF getVcfById(long id)
	{
	return contains(Table.VCF,id)?wrap( VCF.class, id):null;
	}

public  VCF getVcfByPath(String path)
	{
	if(path==null || path.trim().isEmpty()) return null;
	Long id=getIdByName(Table.VCF,"path",path);
	return id==null?null:wrap( VCF.class, id);
	}

public List< VCF> getAllVCFs()
	{
	return getAllObjects( VCF.class);
	}

private Linkage getLinkage
		(
		VCF vcf,
		Interval interval,
		List<Sample> samples
		)
	{
	TabixReader tabixReader=null;
	TabixReader.Iterator iter=null;
	BufferedReader r=null;
	try {
		List<Genotype> genotypes=new ArrayList<Genotype>();
		r=IoUtil.openFileForBufferedUtf8Reading(vcf.getFile());
		String line;
		
		Pattern tab=Pattern.compile("[\t]");
		Pattern colon=Pattern.compile("[\\:]");
		Map<Sample,Integer> sample2columns=new HashMap<Sample, Integer>(samples.size());
		while((line=r.readLine())!=null)
			{
			if(!line.startsWith("#")) return new Linkage(genotypes);
			if(!line.startsWith("#CHROM\t")) continue;
			String tokens[]=tab.split(line);
			for(int i=9;i< tokens.length;++i)
				{
				for(Sample S:samples)
					{
					if(!tokens[i].equals(S.getName())) continue;
					sample2columns.put(S, i);
					}
				}
			break;
			}
		r.close();
		r=null;
		if(sample2columns.isEmpty()) return new Linkage(genotypes);
		
		tabixReader=new TabixReader(vcf.getPath());
		iter=tabixReader.query(interval.getSequence()+":"+interval.getStart()+"-"+interval.getEnd());
		while((line=iter.next())!=null)
			{
			String tokens[]=tab.split(line);
			if(tokens.length<9) continue;
			Variation variation=new Variation(
					tokens[0],
					Integer.parseInt(tokens[1]), 
					(tokens[2].isEmpty() || tokens[2].equals(".")?null:tokens[2]),
					tokens[3].toUpperCase(),
					tokens[4].toUpperCase()
					);
			String format[]=colon.split(tokens[8]);
			int gt_column=-1;
			for(int i=0;i< format.length;++i)
				{
				if(format[i].equals("GT"))
					{
					gt_column=i;
					break;
					}
				}
			for(Sample sample: sample2columns.keySet())
				{
				int column=sample2columns.get(sample);
				if(column>=tokens.length) continue;
				if(tokens[column].isEmpty() || tokens[column].equals(".")) continue;
				format=colon.split(tokens[column]);
				if(gt_column>=format.length)continue;
				String genotype=tokens[gt_column];
				if(genotype.isEmpty() ||genotype.equals(".")||genotype.equals("./.")) continue;
				genotypes.add(new Genotype(variation, sample, genotype));
				}
			}
		
		return new Linkage(genotypes);
		}
	catch (Exception err)
		{
		throw new RuntimeException(err);
		}
	finally
		{
		if(r!=null) try { r.close();} catch(IOException err) {}
		if(tabixReader!=null) tabixReader.close();
		}
	}


private SortedSet<Variation> getVariations
	(
	VCF vcf,
	Interval interval
	)
	{
	TabixReader tabixReader=null;
	TabixReader.Iterator iter=null;
	try
		{
		SortedSet<Variation> vars=new TreeSet<Variation>();
		String line;
		
		Pattern tab=Pattern.compile("[\t]");
		
		tabixReader=new TabixReader(vcf.getPath());
		iter=tabixReader.query(interval.getSequence()+":"+interval.getStart()+"-"+interval.getEnd());
		while((line=iter.next())!=null)
			{
			String tokens[]=tab.split(line);
			if(tokens.length<9) continue;
			Variation variation=new Variation(
					tokens[0],
					Integer.parseInt(tokens[1]), 
					(tokens[2].isEmpty() || tokens[2].equals(".")?null:tokens[2]),
					tokens[3].toUpperCase(),
					tokens[4].toUpperCase()
					);
			vars.add(variation);
			}
		
		return vars;
		}
	catch (Exception err)
		{
		throw new RuntimeException(err);
		}
	finally
		{
		if(tabixReader!=null) tabixReader.close();
		}
	}




/** Bam ******************************************************************************/
public  Bam getBamById(long id)
	{
	return contains(Table.BAM,id)?wrap(Bam.class, id):null;
	}

public  Bam getBamByPath(String path)
	{
	if(path==null || path.trim().isEmpty()) return null;
	Long id=getIdByName(Table.BAM,"path",path);
	return id==null?null:wrap(Bam.class, id);
	}

public List< Bam> getAllBams()
	{
	return getAllObjects(Bam.class);
	}


public IndexedFastaSequenceFile getIndexedFastaSequenceFileByPath(String fastaPath)
	{
	try
		{
		synchronized (path2reference)
			{
			IndexedFastaSequenceFile reference=null;
			reference=this.path2reference.get(fastaPath);
			if(reference==null)
				{
				reference=new IndexedFastaSequenceFile(new File(fastaPath));
				}
			
			path2reference.put(fastaPath, reference);
			return reference;
			}
		}
	catch (FileNotFoundException err)
		{
		throw new RuntimeException(err);
		}
	}

public void dispose()
	{
	synchronized (path2reference)
		{
		path2reference.clear();
		}
	}
}
