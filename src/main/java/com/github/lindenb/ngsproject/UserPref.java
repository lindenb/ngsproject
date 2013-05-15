package com.github.lindenb.ngsproject;

import java.util.ArrayList;
import java.util.List;

import com.github.lindenb.ngsproject.model.User;

public class UserPref
	{
	private User user;
	private List<Message> messages=new ArrayList<Message>();
	
	public void setUser(User user)
		{
		this.user = user;
		}
	
	public User getUser()
		{
		return this.user;
		}
	
	
	public List<Message> getMessages()
		{
		return messages;
		}
	
	
	public boolean isLogged()
		{
		return user!=null;
		}
	public boolean isAdmin()
		{
		if(! isLogged()) return false;
		return getUser()!=null && getUser().isAdmin();
		}
	@Override
	public String toString() {
		return user==null?"anonymous":user.getName();
		}
	}
