package com.github.lindenb.ngsproject.model;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;



public class Linkage {
private List<Sample> samples;
private List<Genotype> genotypes;
public Linkage(
		List<Sample> samples,
		List<Genotype> genotypes
		)
	{
	this.samples=samples;
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

public List<Sample> getSamples()
	{
	return samples;
	}

public Set<String> getGenotypes(Variation var,Sample sample)
	{
	Set<String> gens=new TreeSet<String>();
	for(Genotype g:genotypes)
		{
		if(!g.getVariation().equals(var))
			{
			continue;
			}
		if(g.getSample().getName().equals(sample.getName()))
			{
			gens.add(g.getA1()+"/"+g.getA2());
			}
		}
	return gens;
	}

@Override
	public String toString() {
		return genotypes.toString();
		}
}
