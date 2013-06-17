package com.github.lindenb.ngsproject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.lindenb.ngsproject.model.ActiveRecord;
import com.github.lindenb.ngsproject.model.Bam;
import com.github.lindenb.ngsproject.model.Group;
import com.github.lindenb.ngsproject.model.Project;
import com.github.lindenb.ngsproject.model.Sample;
import com.github.lindenb.ngsproject.model.Table;
import com.github.lindenb.ngsproject.model.VCF;

public class Functions
	{
	private static boolean belongToGroup(
			final UserPref user,
			Group g
			)
			{
			if(g==null ) return false;
			if(g.isPublic()) return true;
			if(!user.isLogged()) return false;
			for(ActiveRecord u:g.getUsers())
				{
				if(u.getTable()==Table.USER && u.getId()==user.getUser().getId()) return true;
				}
			return false;
			}
	
	
	public static boolean visible(
			final HttpServletRequest req,
			final ActiveRecord rec
			)
		{
		if(rec==null) return false;
		HttpSession session=req.getSession(false);
		if(session==null) return false;
		UserPref user=(UserPref)session.getAttribute("user");
		if(user==null) return false;
		if(user.isAdmin()) return true;
		switch(rec.getTable())
			{
			case REFERENCE: return true;
			case USER: return false;
			case GROUP:
				{
				return belongToGroup(user,((Group)rec));
				}
			case PROJECT:
				{
				return belongToGroup(user,((Project)rec).getGroup());
				}
			case BAM:
				{
				for( Project p:((Bam)rec).getProjects())
					{
					if(belongToGroup(user,p.getGroup())) return true;
					}
				return false;
				}
			case SAMPLE:
				{
				for( Project p:((Sample)rec).getProjects())
					{
					if(belongToGroup(user,p.getGroup())) return true;
					}
				return false;
				}
			case VCF:
				{
				for( Project p:((VCF)rec).getProjects())
					{
					if(belongToGroup(user,p.getGroup())) return true;
					}
				return false;
				}
			default: return false;
			}
		}
	
	
	
	public static <T extends ActiveRecord> Collection<T> filter(final HttpServletRequest req,Collection<T> t)
		{
		List<T> L=new ArrayList<T>();
		if(t==null || t.isEmpty()) return L;
		for(T i : t)
			{
			if(visible(req, i)) L.add(i);
			}
		return L;
		}
	}	
