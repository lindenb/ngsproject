package com.github.lindenb.ngsproject.model;

public class Genotype
	{
	private Variation variation;
	private Sample sample;
	private String A1;
	private String A2;
	
	public Genotype(Variation variation, Sample sample, String A1,String A2)
		{
		this.variation = variation;
		this.sample = sample;
		if(A1.compareTo(A2)<0)
			{
			this.A1=A1;
			this.A2=A2;
			}
		else
			{
			this.A1=A2;
			this.A2=A1;
			}
		}

	public Variation getVariation() {
		return variation;
	}

	public Sample getSample() {
		return sample;
	}

	public String getA1() {
		return A1;
	}

	public String getA2() {
		return A2;
	}

	
	
	public String getChrom() {
		return variation.getChrom();
	}

	public int getPos() {
		return variation.getPos();
	}

	public String getID() {
		return variation.getID();
	}

	public String getRef() {
		return variation.getRef();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sample == null) ? 0 : sample.hashCode());
		result = prime * result + ((A1 == null) ? 0 : A1.hashCode());
		result = prime * result + ((A2 == null) ? 0 : A2.hashCode());
		result = prime * result
				+ ((variation == null) ? 0 : variation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Genotype other = (Genotype) obj;
		if (sample == null) {
			if (other.sample != null)
				return false;
		} else if (!sample.equals(other.sample))
			return false;
		if (A1 == null) {
			if (other.A1 != null)
				return false;
		} else if (!A1.equals(other.A1))
			return false;
		if (A2 == null) {
			if (other.A2 != null)
				return false;
		} else if (!A2.equals(other.A2))
			return false;
		
		if (variation == null) {
			if (other.variation != null)
				return false;
		} else if (!variation.equals(other.variation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Genotype [variation=" + variation + ", sample=" + sample
				+ ", value=" + A1+"/"+A2 + "]";
	}
	
	
	}
