package com.github.lindenb.ngsproject;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Message
	{
	public enum MsgType{error,warning,info,ok};
	private String msg;
	private MsgType type;
	
	public Message()
		{
		this("undefined");
		}
	
	public Message(String msg)
		{
		this(msg,MsgType.info);
		}
	public Message(Throwable err)
		{
		try {
			StringWriter sw=new StringWriter();
			PrintWriter pw=new PrintWriter(sw);
			err.printStackTrace(pw);
			pw.flush();
			this.msg=sw.toString();
		} catch (Exception e) {
			this.msg=String.valueOf(err.getMessage());
			}
		this.type=MsgType.error;
		}
	
	public Message(String msg,MsgType type)
		{
		this.msg=msg;
		this.type=type;
		}
	public MsgType getType()
		{
		return type;
		}
	public String getMessage()
		{
		return msg;
		}
	@Override
	public String toString()
		{
		return getMessage();
		}
	}
