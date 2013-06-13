<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
	version='1.0' 
	xmlns:crypto="http://exslt.org/crypto"
	>
<!-- 
Usage:
	 xsltproc admin.xml |\
	  	${HOME}/srv/glassfish/glassfish3/javadb/bin/ij

 -->
<xsl:output method="text"/>
<xsl:template match="/">
<xsl:apply-templates select="admin"/>
</xsl:template>

<xsl:template match="admin">
<xsl:if test="not(@uri)">
<xsl:message terminate="yes">@uri missing</xsl:message>
</xsl:if>
connect '<xsl:value-of select="@uri"/>';

set SCHEMA NGSPROJECTS;
<xsl:apply-templates/>
disconnect;
</xsl:template>

<xsl:template match="insert">
<xsl:apply-templates select="user" mode="insert"/>
<xsl:apply-templates select="group" mode="insert"/>
<xsl:apply-templates select="reference" mode="insert"/>
<xsl:apply-templates select="sample" mode="insert"/>
<xsl:apply-templates select="project" mode="insert"/>
<xsl:apply-templates select="vcf" mode="insert"/>
<xsl:apply-templates select="bam" mode="insert"/>
</xsl:template>



<xsl:template match="user" mode="insert">
<xsl:if test="not(name)">
<xsl:message terminate="yes">'name' missing</xsl:message>
</xsl:if>

insert into USERS(name,sha1sum<xsl:if test="admin='true'">,is_admin</xsl:if>) values(
'<xsl:value-of select="name"/>',
<xsl:choose>
<xsl:when test="sha1sum">
'<xsl:value-of select="sha1sum"/>'
</xsl:when>
<xsl:when test="password">
'<xsl:value-of select="crypto:sha1(password)"/>'
</xsl:when>
<xsl:otherwise>
<xsl:message terminate="yes">'password' missing</xsl:message>
</xsl:otherwise>
</xsl:choose>
<xsl:if test="admin='true'">,1</xsl:if>
);


<xsl:for-each select="group">
insert into USER2GROUP(user_id,group_id) values(
	( select id from USERS where name='<xsl:value-of select='../name'/>'),
	( select id from USERGROUP where name = '<xsl:value-of select="."/>' )
	
	);	
</xsl:for-each>


</xsl:template>

<xsl:template match="group" mode="insert">
<xsl:if test="not(name)">
<xsl:message terminate="yes">'name' missing</xsl:message>
</xsl:if>

insert into USERGROUP(name,is_public) values(
'<xsl:value-of select="name"/>',
<xsl:choose>
<xsl:when test="public='true'">1</xsl:when>
<xsl:otherwise>0</xsl:otherwise>
</xsl:choose>
);

<xsl:for-each select="user">
insert into USER2GROUP(user_id,group_id) values(
	( select id from USERS where name='<xsl:value-of select='.'/>'),
	( select id from USERGROUP where name = '<xsl:value-of select="../name"/>' )
	
	);	
</xsl:for-each>


</xsl:template>

<xsl:template match="project" mode="insert">
<xsl:if test="not(name)">
<xsl:message terminate="yes">'name' missing</xsl:message>
</xsl:if>
<xsl:if test="not(description)">
<xsl:message terminate="yes">'description' missing</xsl:message>
</xsl:if>
<xsl:if test="not(group)">
<xsl:message terminate="yes">'group' missing</xsl:message>
</xsl:if>

insert into PROJECT(name,description,group_id) values (
'<xsl:value-of select="name"/>',
'<xsl:value-of select="description"/>',
( select id from USERGROUP where name = '<xsl:value-of select="group"/>' )
);
</xsl:template>



<xsl:template match="reference" mode="insert">
<xsl:if test="not(name)">
<xsl:message terminate="yes">'name' missing</xsl:message>
</xsl:if>
<xsl:if test="not(path)">
<xsl:message terminate="yes">'path' missing</xsl:message>
</xsl:if>
<xsl:if test="not(description)">
<xsl:message terminate="yes">'description' missing</xsl:message>
</xsl:if>

insert into REFERENCE(name,description,group_id) values (
'<xsl:value-of select="path"/>',
'<xsl:value-of select="name"/>',
'<xsl:value-of select="description"/>'
);

</xsl:template>


<xsl:template match="sample" mode="insert">
<xsl:if test="not(name)">
<xsl:message terminate="yes">'name' missing</xsl:message>
</xsl:if>

insert into SAMPLE(name) values(
'<xsl:value-of select="name"/>'
);
</xsl:template>

<xsl:template match="bam" mode="insert">
<xsl:if test="not(path)">
<xsl:message terminate="yes">'path' missing</xsl:message>
</xsl:if>
<xsl:if test="not(sample)">
<xsl:message terminate="yes">'sample' missing</xsl:message>
</xsl:if>

<xsl:if test="not(reference)">
<xsl:message terminate="yes">'reference' missing</xsl:message>
</xsl:if>

insert into BAM(path,sample_id,reference_id) values(
	'<xsl:value-of select="path"/>',
	( select id from SAMPLE where name = '<xsl:value-of select="sample"/>' ),
	( select id from REFERENCE where name = '<xsl:value-of select="reference"/>' )
	);


<xsl:for-each select="project">
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = '<xsl:value-of select="."/>' ),
	( select id from BAM where path= '<xsl:value-of select="../path"/>')
	);	
</xsl:for-each>


</xsl:template>

<xsl:template match="vcf" mode="insert">
<xsl:if test="not(path)">
<xsl:message terminate="yes">'path' missing</xsl:message>
</xsl:if>
<xsl:if test="not(description)">
<xsl:message terminate="yes">'description' missing</xsl:message>
</xsl:if>
<xsl:if test="not(reference)">
<xsl:message terminate="yes">'reference' missing</xsl:message>
</xsl:if>

insert into VCF(path,description,reference_id) values(
	'<xsl:value-of select="path"/>',
	'<xsl:value-of select="description"/>',
	( select id from REFERENCE where name = '<xsl:value-of select="reference"/>' )
	);
<xsl:for-each select="sample">
insert into VCF2SAMPLE(sample_id,vcf_id) values(
	( select id from SAMPLE where name = '<xsl:value-of select="."/>' ),
	( select id from VCF where path= '<xsl:value-of select="../path"/>')
	);	
</xsl:for-each>

<xsl:for-each select="project">
insert into VCF2PROJECT(project_id,vcf_id) values(
	( select id from PROJECT where name = '<xsl:value-of select="."/>' ),
	( select id from VCF where path= '<xsl:value-of select="../path"/>')
	);	
</xsl:for-each>


</xsl:template>



</xsl:stylesheet>