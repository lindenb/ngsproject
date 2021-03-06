package com.github.lindenb.ngsproject.model;

import java.io.IOException;
import java.util.List;

import net.sf.picard.reference.IndexedFastaSequenceFile;

public interface Reference extends ActiveRecord
{
public String getPath();
public String getName();
public String getDescription();
public IndexedFastaSequenceFile getIndexedFastaSequenceFile() throws IOException;
public List<String> getChromosomes();
}
