package com.github.lindenb.ngsproject.model;

public class Variation implements Comparable<Variation>
	{
	private String chrom;
	private int pos;
	private String ID;
	private String ref;
	private String alt;
	
	public Variation(String chrom, int pos, String iD, String ref, String alt)
		{
		super();
		this.chrom = chrom;
		this.pos = pos;
		ID = iD;
		this.ref = ref;
		this.alt = alt;
		}
	
	public String getChrom() {
		return chrom;
	}
	public int getPos() {
		return pos;
	}
	public String getID() {
		return ID;
	}
	public String getRef() {
		return ref;
	}
	public String getAlt() {
		return alt;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alt.hashCode();
		result = prime * result + chrom.hashCode();
		result = prime * result + pos;
		result = prime * result + ref.hashCode();
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
		Variation other = (Variation) obj;
		if (pos != other.pos)
			return false;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equalsIgnoreCase(other.alt))
			return false;
		if (chrom == null) {
			if (other.chrom != null)
				return false;
		} else if (!chrom.equals(other.chrom))
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Variation var)
		{
		int i= chrom.compareTo(var.chrom);
		if(i!=0) return i;
		i=pos-var.pos;
		if(i!=0) return i;
		i=ref.compareTo(var.ref);
		if(i!=0) return i;
		return alt.compareTo(var.alt);
		}
	
	@Override
	public String toString()
		{
		String s=chrom + ":" + pos+"("+ref+"/"+alt+")";
		if(ID!=null) s+=" "+ID;
		return s;
		}
	
	
	
}
