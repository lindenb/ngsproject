package com.github.lindenb.ngsproject.model;

public class Genotype
	{
	private Variation variation;
	private String sample;
	private String value;
	
	public Genotype(Variation variation, String sample, String value)
		{
		this.variation = variation;
		this.sample = sample;
		this.value = value;
		}

	public Variation getVariation() {
		return variation;
	}

	public String getSample() {
		return sample;
	}

	public String getValue() {
		return value;
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

	public String getAlt() {
		return variation.getAlt();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sample == null) ? 0 : sample.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
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
				+ ", value=" + value + "]";
	}
	
	
	}
