package com.github.lindenb.ngsproject.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Linkage {
private List<Genotype> genotypes;
public Linkage(List<Genotype> genotypes)
	{
	this.genotypes=genotypes;
	}

public Set<Variation> getVariations()
	{
	Set<Variation> vars=new TreeSet<Variation>();
	for(Genotype g:genotypes)
		{
		vars.add(g.getVariation());
		}
	return vars;
	}

public Set<String> getSamples()
	{
	Set<String> vars=new TreeSet<String>();
	for(Genotype g:genotypes)
		{
		vars.add(g.getSample());
		}
	return vars;
	}

public Map<Variation,Map<String,Set<String>>> getAsMap()
	{
	Map<Variation,Map<String,Set<String>>> var2gen=new TreeMap<Variation, Map<String,Set<String>>>();
	
	for(Genotype g:genotypes)
		{
		Map<String,Set<String>> sample2gen =var2gen.get(g.getVariation());
		if(sample2gen==null)
			{
			sample2gen=new TreeMap<String, Set<String>>();
			var2gen.put(g.getVariation(),sample2gen);
			}
		Set<String> gens=sample2gen.get(g.getSample());
		if(gens==null)
			{
			gens=new HashSet<String>();
			sample2gen.put(g.getSample(),gens);
			}
		gens.add(g.getValue());
		}
	return var2gen;
	}

@Override
	public String toString() {
		return genotypes.toString();
		}
}
