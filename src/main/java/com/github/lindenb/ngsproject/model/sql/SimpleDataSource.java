package com.github.lindenb.ngsproject.model.sql;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class SimpleDataSource implements DataSource
	{
	private static long ID_GENERATOR=0L;
	private File dbPath;
	private List<Connection> connections;
	
	static {{
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
			}
		catch (Throwable e) {
			e.printStackTrace();
			}
		
		}}
	public SimpleDataSource(File dbPath)
		{
		this.connections=new ArrayList<Connection>();
		this.dbPath=dbPath;
		}
	
	private void log(Object o) throws SQLException
		{
		PrintWriter out=getLogWriter();
		if(out==null) return ;
		out.println(String.valueOf(o));
		}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
		}
	
	public synchronized void close()
		{
		while(!this.connections.isEmpty())
			{
			Connection con=this.connections.remove(0);
			try { if(con!=null) con.close();} catch(SQLException err){}
			}
		}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return  DriverManager.getLogWriter();
	}

	@Override
	public int getLoginTimeout() throws SQLException {
	     return DriverManager.getLoginTimeout();
	     }

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	      DriverManager.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int timeout) throws SQLException {
	     DriverManager.setLoginTimeout(timeout);
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}

	@Override
	public synchronized Connection getConnection() throws SQLException {
		 return getConnection(null, null);
		 }
	
	private class ConnexionProxy  implements InvocationHandler
		{
		private long connection_id=(++ID_GENERATOR);
		private boolean closed=false;
		private Connection delegate;
		public ConnexionProxy(Connection delegate)
			{
			this.delegate=delegate;
			this.closed=false;
			}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			 if(method.getName().equals("close"))
			 	{
				this.closed=true;
				SimpleDataSource.this.recycle(this);
				return null;
			 	}
			 else if(method.getName().equals("isClosed"))
			 	{
				return this.closed;
			 	}
			 else if(method.getName().equals("toString"))
			 	{
				return "ConnectionId"+this.connection_id;
			 	}
			log(method.getName()+" "+connection_id);
			 try  {
			      return method.invoke(delegate, args);
			      }
		 	catch (Exception ex) {
		 		ex.printStackTrace();
		        throw new SQLException(ex);
		      }
			}
		
		}
	
	private synchronized void recycle(ConnexionProxy con) throws SQLException
		{
		Connection delegate=con.delegate;
		if(delegate==null) return;
		log("recycle returning connection");
		this.connections.add(delegate);
		}
	
	private Connection wrap(Connection con)
		{
		return (Connection)
		        java.lang.reflect.Proxy.newProxyInstance(
		        Connection.class.getClassLoader(),
		        new Class[]{java.sql.Connection.class},
		        new ConnexionProxy(con)
		        );
		}
	
	@Override
	public synchronized Connection getConnection(String userid, String password)
			throws SQLException
		{
		while(!this.connections.isEmpty())
			{
			Connection con=this.connections.remove(0);
			try { if(con==null || con.isClosed()) continue; } catch(SQLException err){ continue;}
			log("recycle old connection");
			return wrap(con);
			}
		log("Create new Connection");
		Connection con= DriverManager.getConnection(
				"jdbc:sqlite:"+this.dbPath,
				userid,
				password
				);
		con.setReadOnly(true);
		return wrap(con);
		}
	
	public static void main(String[] args) throws Exception
		{
		SimpleDataSource ds=new SimpleDataSource(new File("/home/lindenb/jeter.sqlite"));
		ds.setLogWriter(new PrintWriter(System.err));
		Connection cons[]=new Connection[3];
		for(int i=0;i< cons.length;++i)
			{
			cons[i]=ds.getConnection();
			}
		for(int i=0;i< cons.length;++i)
			{
			ds.getLogWriter().println(cons[i]);
			PreparedStatement pstmt=cons[i].prepareStatement("select midline from Hsp");
			ResultSet row=pstmt.executeQuery();
			while(row.next())
				{
				System.out.println(row.getString(1));
				}
			pstmt.close();
			}
		
		
		for(int i=0;i< cons.length;++i)
		{
		cons[i].close();
		}
		
		for(int i=0;i< cons.length;++i)
			{
			cons[i].close();
			}
		
		ds.close();
		}
}
