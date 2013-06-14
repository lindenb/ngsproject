package com.github.lindenb.ngsproject.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.broad.tribble.readers.AsciiLineReader;
import org.broad.tribble.readers.TabixReader;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;


import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.picard.util.Interval;
import net.sf.samtools.util.BlockCompressedInputStream;

public class DefaultModel implements Model
	{
	private final Map<String,IndexedFastaSequenceFile>  path2reference=java.util.Collections.synchronizedMap(
			new HashMap<String,IndexedFastaSequenceFile>());
		
	
	private static class SQLArgument
		{
		int sqlType;
		Object v;
		}
	
	@SuppressWarnings("unchecked")
	private class SQLList<T> extends AbstractList<T>
		{
		private Integer _size=null;
		String sql;
		List<SQLArgument> arguments=new ArrayList<SQLArgument>();
		private List<Object> buffer=null;
		private int buffer_offset=-1;
		int buffer_capacity=100;
		SQLRowExtractor<T> extractor=null;
		
		private PreparedStatement prepareStatement(Connection con)
			throws SQLException
			{
			PreparedStatement pstmt=con.prepareStatement(this.sql);
			for(int i=0;i< arguments.size();++i)
				{
				SQLArgument arg=arguments.get(i);
				pstmt.setObject(i+1, arg.v, arg.sqlType);
				}
			return pstmt;
			}
		
		@Override
		public int size()
			{
			if(_size==null)
				{
				if(this.buffer_offset==-1)
					{
					this.buffer_offset=0;
					buffer=new ArrayList<Object>(this.buffer_capacity);
					}
				Connection connection=null;
				PreparedStatement pstmt=null;
				ResultSet row=null;
				try
					{
					int n=0;
					connection=getDataSource().getConnection();
					pstmt=prepareStatement(connection);
					row=pstmt.executeQuery();
					while(row.next())
						{
						if(this.buffer_offset==0 && n< this.buffer_capacity)
							{
							buffer.add(this.extractor.extract(row));
							}
						n++;
						}
					this._size=n;
					}
				catch (Exception e)
					{
					this.buffer_offset=-1;
					throw new RuntimeException(e);
					}
				finally
					{
					if(row!=null) try { row.close();} catch(SQLException err) {}
					if(pstmt!=null) try { pstmt.close();} catch(SQLException err) {}
					if(connection!=null) try { connection.close();} catch(SQLException err) {}
					}
				}
			return this._size;
			}
		
		@Override
		public T get(int idx)
			{
			if(	buffer_offset!=-1 &&
				buffer!=null &&
				idx >= buffer_offset &&
				idx-buffer_offset < buffer.size()
				)
				{
				return (T)buffer.get(idx-buffer_offset);
				}
			this.buffer=null;
			this.buffer_offset=-1;
			Connection connection=null;
			PreparedStatement pstmt=null;
			ResultSet row=null;
			try
				{
				int n=-1;
				connection=getDataSource().getConnection();
				pstmt=prepareStatement(connection);
				
				row=pstmt.executeQuery();
				
				while(row.next())
					{
					n++;
					if(n<idx)
						{
						//ignore
						}
					else if(n==idx)
						{
						buffer_offset=idx;
						buffer=new ArrayList<Object>(this.buffer_capacity);
						buffer.add(this.extractor.extract(row));
						}
					else if(n<=idx+this.buffer_capacity)
						{
						buffer.add(this.extractor.extract(row));
						}
					else
						{
						//buffer filled
						break;
						}
					}
				if(this.buffer_offset==idx)
					{
					return (T)buffer.get(0);
					}
				}
			catch (Exception e)
				{
				throw new RuntimeException(e);
				}
			finally
				{
				if(row!=null) try { row.close();} catch(SQLException err) {}
				if(pstmt!=null) try { pstmt.close();} catch(SQLException err) {}
				if(connection!=null) try { connection.close();} catch(SQLException err) {}
				}
			throw new IndexOutOfBoundsException("Cannot get ["+idx+"]");
			}
		}
	
	private class SQLListFactory<T>
		{
		private SQLList<T> L=new SQLList<T>();
		
		private SQLListFactory<T> addParam(int sqlType,Object v)
			{
			SQLArgument a=new SQLArgument();
			a.sqlType=sqlType;
			a.v=v;
			L.arguments.add(a);
			return this;
			}
		
		public SQLListFactory<T> addString(String s)
			{
			return addParam(Types.VARCHAR,s);
			}
		public SQLListFactory<T> addLong(long s)
			{
			return addParam(Types.INTEGER,s);
			}
		
		public SQLListFactory<T> setExtractor(SQLRowExtractor<T> extractor)
			{
			L.extractor=extractor;
			return this;
			}
		
		public SQLListFactory<T> setCapacity(int n)
			{
			L.buffer_capacity=Math.max(n,1);
			return this;
			}
		public SQLListFactory<T> setQuery(String sql)
			{
			L.sql=sql;
			return this;
			}
		
		public List<T> create()
			{
			SQLList<T> L2=L;
			if(L2.sql==null) throw new IllegalStateException("sql is nil");
			if(L2.extractor==null) throw new IllegalStateException("extractor is nil");
			this.L=new SQLList<T>();
			this.L.extractor=L2.extractor;
			this.L.sql=L2.sql;
			this.L.buffer_capacity=L2.buffer_capacity;
			return L2;
			}
		}
	
	
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
			
		private class EntityExtractor<T extends ActiveRecord>
			implements SQLRowExtractor<T>
			{
			private Class<T> clazz;
			EntityExtractor(Class<T> clazz)
				{
				this.clazz=clazz;
				}
			@Override
			public T extract(ResultSet row) throws SQLException
				{
				return wrap(this.clazz,row.getLong(1));
				}
			
			}
			
			
		private Group publicGroup=new Group()
			{
			public long getId() {return 0L;};
			public String getName() {return "public";};
			public boolean isPublic() {return true;};
			public DefaultModel getModel() {return DefaultModel.this;};
			@Override
			public List<User> getUsers() { return getModel().getAllUsers(); }
			@Override
			public Table getTable() {
					return Table.GROUP;
					}
			};
			
		
	private DataSource dataSource;
	public DefaultModel(DataSource dataSource)
		{
		this.dataSource=dataSource;
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getDataSource()
	 */
	@Override
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
	
			if(method.equals(GET_MODEL)) return DefaultModel.this;
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
						methodName.equals("getFile") ||
						methodName.equals("isIndexedWithTabix")
						)
						{
						String path= getString(this,"path");
						if( methodName.equals("getFile")) return new File(path);
						if( methodName.equals("getName")) return new File(path).getName();
						if( methodName.equals("isIndexedWithTabix"))
							{
							File f=new File(path);
							File tbi=new File(path+".tbi");
							return tbi.exists() && tbi.lastModified()>=f.lastModified();
							}
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
						return getLinkage(
								Collections.singleton((VCF)this),
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
						List<VCF> vcfs= (List<VCF>)this.invoke(this,
								Project.class.getMethod("getVcfs"),
								new Object[0]
								);
						List<Sample> samples= (List<Sample>)this.invoke(this,
								Project.class.getMethod("getSamples"),
								new Object[0]
								);
	
						return getLinkage(
								vcfs,
								(Interval)args[0],
								samples
								); 
						}
	
					if( methodName.equals("getReference"))
						{
						List<Bam> bams=(List<Bam>)this.invoke(this,
								Project.class.getMethod("getBams"),
								new Object[0]
								);
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
							return DefaultModel.this.getIndexedFastaSequenceFileByPath(path);
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
			return DefaultModel.this;
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
		return new SQLListFactory<T>().setQuery(
				sql
				).setExtractor(new EntityExtractor<T>(clazz)).
				addLong(id).create();
		}
	
	private <T extends ActiveRecord> List<T> getAllObjects(Class<T> clazz)
		{
		return new SQLListFactory<T>().setQuery(
				"select id from "+ class2table(clazz).sqlTable()
				).setExtractor(new EntityExtractor<T>(clazz)).create();
		}
	
	private static Table class2table(Class<? extends ActiveRecord> C)
		{
		if(C==Bam.class) return Table.BAM;
		if(C==Sample.class) return Table.SAMPLE;
		if(C==User.class) return Table.USER;
		if(C==Group.class) return Table.GROUP;
		if(C==Reference.class) return Table.REFERENCE;
		if(C==Project.class) return Table.PROJECT;
		if(C==VCF.class) return Table.VCF;
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
	
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getGroupByName(java.lang.String)
	 */
	@Override
	public Group getGroupByName(String s)
		{
		if(s==null || s.trim().isEmpty()) return null;
		Long id=getIdByName(Table.GROUP,"name",s);
		return id==null?null:wrap(Group.class, id);
		}
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getGroupById(long)
	 */
	@Override
	public Group getGroupById(long id)
		{
		return contains(Table.GROUP,id)?wrap( Group.class, id):null;
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getSampleById(long)
	 */
	@Override
	public Sample getSampleById(long id)
		{
		return contains(Table.SAMPLE,id)?wrap(Sample.class, id):null;
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getSampleByName(java.lang.String)
	 */
	@Override
	public Sample getSampleByName(String s)
		{
		if(s==null || s.trim().isEmpty()) return null;
		Long id=getIdByName(Table.SAMPLE,"name",s);
		return id==null?null:wrap(Sample.class, id);
		}
	
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getAllSamples()
	 */
	@Override
	public List<Sample> getAllSamples()
		{
		return getAllObjects(Sample.class);
		}
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getUserById(long)
	 */
	@Override
	public User getUserById(long id)
		{
		return contains(Table.USER,id)?wrap(User.class, id):null;
		}
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getUserByName(java.lang.String)
	 */
	@Override
	public User getUserByName(String s)
		{
		if(s==null || s.trim().isEmpty()) return null;
		Long id=getIdByName(Table.USER,"name",s);
		return id==null?null:wrap(User.class, id);
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getAllUsers()
	 */
	@Override
	public List<User> getAllUsers()
		{
		return getAllObjects(User.class);
		}
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getProjectById(long)
	 */
	@Override
	public  Project getProjectById(long id)
		{
		return contains(Table.PROJECT,id)?wrap(Project.class, id):null;
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getProjectByName(java.lang.String)
	 */
	@Override
	public  Project getProjectByName(String s)
		{
		if(s==null || s.trim().isEmpty()) return null;
		Long id=getIdByName(Table.PROJECT,"name",s);
		return id==null?null:wrap(Project.class, id);
		}
	
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getAllProjects()
	 */
	@Override
	public List< Project> getAllProjects()
		{
		return getAllObjects(Project.class);
		}
	
	
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getProjects(com.github.lindenb.ngsproject.model.User)
	 */
	@Override
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
	
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getAllGroups()
	 */
	@Override
	public List< Group> getAllGroups()
		{
		return getAllObjects(Group.class);
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getReferenceById(long)
	 */
	@Override
	public  Reference getReferenceById(long id)
		{
		return contains(Table.REFERENCE,id)?wrap(Reference.class, id):null;
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getReferenceByName(java.lang.String)
	 */
	@Override
	public Reference getReferenceByName(String s)
		{
		if(s==null || s.trim().isEmpty()) return null;
		Long id=getIdByName(Table.REFERENCE,"name",s);
		return id==null?null:wrap(Reference.class, id);
		}
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getAllReferences()
	 */
	@Override
	public List< Reference> getAllReferences()
		{
		return getAllObjects(Reference.class);
		}
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getVcfById(long)
	 */
	@Override
	public  VCF getVcfById(long id)
		{
		return contains(Table.VCF,id)?wrap( VCF.class, id):null;
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getVcfByPath(java.lang.String)
	 */
	@Override
	public  VCF getVcfByPath(String path)
		{
		if(path==null || path.trim().isEmpty()) return null;
		Long id=getIdByName(Table.VCF,"path",path);
		return id==null?null:wrap( VCF.class, id);
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getAllVCFs()
	 */
	@Override
	public List< VCF> getAllVCFs()
		{
		return getAllObjects( VCF.class);
		}
	
	private Linkage getLinkage
			(
			Collection<VCF> vcfs,
			Interval interval,
			List<Sample> samples
			)
		{
		List<Genotype> genotypes=new ArrayList<Genotype>();
		for(VCF vcf:vcfs)
			{
			if(!vcf.isIndexedWithTabix()) continue;
			VCFCodec codec=new VCFCodec();
			TabixReader tabixReader=null;
			TabixReader.Iterator iter=null;
			AsciiLineReader r=null;
			BlockCompressedInputStream bcis=null;
			try {
				bcis=new BlockCompressedInputStream(vcf.getFile());
				r=new AsciiLineReader(bcis);
				codec.readHeader(r);
				r.close();
				String line;
				
				
				tabixReader=new TabixReader(vcf.getPath());
				System.err.println("#interval;"+interval);
				iter=tabixReader.query(interval.getSequence()+":"+interval.getStart()+"-"+interval.getEnd());
				while(iter!=null && (line=iter.next())!=null)
					{
					VariantContext var=codec.decode(line);
					System.err.println("#line;"+line);
					Variation variation=new Variation(var.getChr(), var.getStart(), var.getID(), var.getReference().getBaseString());
	
					for(Sample S:samples)
						{
						org.broadinstitute.variant.variantcontext.Genotype g=var.getGenotype(S.getName());
						if(g==null) continue;
						if(!g.isAvailable()) continue;
						if(!g.isCalled()) continue;
						if(g.isNoCall()) continue;
						if(g.isHomRef()) continue;
						List<Allele> alleles=g.getAlleles();
						if(alleles.size()!=2) continue;
						genotypes.add(new Genotype(
								variation , S,
								alleles.get(0).getBaseString(),
								alleles.get(1).getBaseString()
								));
						}
					}
				
				
				}
			catch (Exception err)
				{
				err.printStackTrace();
				}
			finally
				{
				if(bcis!=null ) try { bcis.close();} catch(Exception err) {}
				if(r!=null) try { r.close();} catch(Exception err) {}
				if(tabixReader!=null) tabixReader.close();
				}
			}
		return new Linkage(samples,genotypes);
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
						tokens[3].toUpperCase()
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
	
	
	
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getBamById(long)
	 */
	@Override
	public  Bam getBamById(long id)
		{
		return contains(Table.BAM,id)?wrap(Bam.class, id):null;
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getBamByPath(java.lang.String)
	 */
	@Override
	public  Bam getBamByPath(String path)
		{
		if(path==null || path.trim().isEmpty()) return null;
		Long id=getIdByName(Table.BAM,"path",path);
		return id==null?null:wrap(Bam.class, id);
		}
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getAllBams()
	 */
	@Override
	public List< Bam> getAllBams()
		{
		return getAllObjects(Bam.class);
		}
	
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#getIndexedFastaSequenceFileByPath(java.lang.String)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see com.github.lindenb.ngsproject.model.Model#dispose()
	 */
	@Override
	public void dispose()
		{
		synchronized (path2reference)
			{
			path2reference.clear();
			}
		}
}
