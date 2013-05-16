package com.github.lindenb.ngsproject.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

package com.github.lindenb.ngsproject.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class Linkage {
private List<Genotype> genotypes;
public Linkage(List<Genotype> genotypes)
	{
	this.genotypes=genotypes;
	}
public boolean isEmpty()
	{
	return this.genotypes.isEmpty();
	}


	
public SortedSet<Variation> getVariations()
	{
	SortedSet<Variation> vars=new TreeSet<Variation>();
	for(Genotype g:genotypes)
		{
		vars.add(g.getVariation());
		}
	return vars;
	}

public SortedSet<Sample> getSamples()
	{
	SortedSet<Sample> vars=new TreeSet<Sample>();
	for(Genotype g:genotypes)
		{
		vars.add(g.getSample());
		}
	return vars;
	}

public Set<String> getGenotypes(Variation var,Sample sample)
	{
	Set<String> gens=new TreeSet<String>();
	for(Genotype g:genotypes)
		{
		if(!g.getVariation().equals(var)) continue;
		if(!g.getSample().equals(sample)) continue;
		gens.add(g.getValue());
		}
	return gens;
	}

@Override
	public String toString() {
		return genotypes.toString();
		}
}
