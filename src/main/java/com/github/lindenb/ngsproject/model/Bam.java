package com.github.lindenb.ngsproject.model;

import java.io.File;


public interface Bam extends ActiveRecord
{
public String getPath();
public File getFile();
public String getName();
public Sample getSample();
public Reference getReference();
}