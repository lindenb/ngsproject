package com.github.lindenb.ngsproject.model;

public class Variation implements Comparable<Variation>
	{
	private String chrom;
	private int pos;
	private String ID;
	private String ref;
	
	public Variation(String chrom, int pos, String iD, String ref)
		{
		super();
		this.chrom = chrom;
		this.pos = pos;
		ID = iD;
		this.ref = ref;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (pos != other.pos) return false;
		if (!chrom.equals(other.chrom)) return false;
		if (!ref.equals(other.ref)) return false;
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
		return i;
		}
	
	@Override
	public String toString()
		{
		String s=chrom + ":" + pos+"("+ref+")";
		if(ID!=null) s+=" "+ID;
		return s;
		}
	
	
	
}
