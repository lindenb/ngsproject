package com.github.lindenb.ngsproject.model.sql;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class SimpleDataSource implements DataSource
	{
	private static long ID_GENERATOR=0L;
	private String jdbcuri;
	private List<Connection> connections;
	private String defaultSchema;
	
	public SimpleDataSource(String jdbcuri)
		{
		this.connections=new ArrayList<Connection>();
		this.jdbcuri=jdbcuri;
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
	
	public void setDefaultSchema(String defaultSchema)
		{
		this.defaultSchema = defaultSchema;
		}
	
	public String getDefaultSchema()
		{
		return defaultSchema;
		}
	
	
	public Connection createNativeConnection(String userid, String password) throws SQLException
		{
		log("Create new Connection");
		Connection con= DriverManager.getConnection(
				this.jdbcuri,
				userid,
				password
				);
		if(getDefaultSchema()!=null) con.setSchema(getDefaultSchema());
		return con;
		}
	
	@Override
	public synchronized Connection getConnection(String userid, String password)
			throws SQLException
		{
		while(!this.connections.isEmpty())
			{
			Connection con=this.connections.remove(0);
			try { if(con==null || con.isClosed()) continue; } catch(SQLException err){ continue;}
			try { con.clearWarnings(); }catch(SQLException err) {}
			log("recycle old connection");
			if(getDefaultSchema()!=null) con.setSchema(getDefaultSchema());
			return wrap(con);
			}
		Connection con=createNativeConnection(userid,password);
		con.setReadOnly(true);
		return wrap(con);
		}
	
}
