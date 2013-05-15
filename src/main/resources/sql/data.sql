set SCHEMA NGSPROJECTS;

insert into SAMPLE(name) values('CD00172');
insert into SAMPLE(name) values('CD01337');
insert into SAMPLE(name) values('CD03840');
insert into SAMPLE(name) values('CD03903');
insert into SAMPLE(name) values('CD05294');
insert into SAMPLE(name) values('CD05295');
insert into SAMPLE(name) values('CD05296');
insert into SAMPLE(name) values('V2528');
insert into SAMPLE(name) values('X1564');
insert into SAMPLE(name) values('X1565');
insert into SAMPLE(name) values('X1566');
insert into SAMPLE(name) values('X1567');
insert into SAMPLE(name) values('X1607');
insert into SAMPLE(name) values('X1608');
insert into SAMPLE(name) values('X1891');
insert into SAMPLE(name) values('X1893');

insert into REFERENCE(path,name,description) values ('/commun/data/pubdb/broadinstitute.org/bundle/1.5/b37/human_g1k_v37.fasta','human_g1k_v37','human_g1k_v37');

insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD00172/CD00172_recal.bam',
	( select id from SAMPLE where name = 'CD00172' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD01337/CD01337_recal.bam',
	( select id from SAMPLE where name = 'CD01337' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD03840/CD03840_recal.bam',
	( select id from SAMPLE where name = 'CD03840' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD03903/CD03903_recal.bam',
	( select id from SAMPLE where name = 'CD03903' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05294/CD05294_recal.bam',
	( select id from SAMPLE where name = 'CD05294' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05295/CD05295_recal.bam',
	( select id from SAMPLE where name = 'CD05295' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05296/CD05296_recal.bam',
	( select id from SAMPLE where name = 'CD05296' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/V2528/V2528_recal.bam',
	( select id from SAMPLE where name = 'V2528' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1564/X1564_recal.bam',
	( select id from SAMPLE where name = 'X1564' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1565/X1565_recal.bam',
	( select id from SAMPLE where name = 'X1565' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1566/X1566_recal.bam',
	( select id from SAMPLE where name = 'X1566' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1567/X1567_recal.bam',
	( select id from SAMPLE where name = 'X1567' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1607/X1607_recal.bam',
	( select id from SAMPLE where name = 'X1607' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1608/X1608_recal.bam',
	( select id from SAMPLE where name = 'X1608' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1891/X1891_recal.bam',
	( select id from SAMPLE where name = 'X1891' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);
insert into BAM(path,sample_id,reference_id) values(
	'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1893/X1893_recal.bam',
	( select id from SAMPLE where name = 'X1893' ),
	( select id from REFERENCE where name = 'human_g1k_v37' )
	);


insert into PROJECT(name,description,group_id) values ('Exome3','Exome3',( select id from USERGROUP where name = 'public' ) );

insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD00172/CD00172_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD01337/CD01337_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD03840/CD03840_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD03903/CD03903_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05294/CD05294_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05295/CD05295_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05296/CD05296_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/V2528/V2528_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1564/X1564_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1565/X1565_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1566/X1566_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1567/X1567_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1607/X1607_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1608/X1608_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1891/X1891_recal.bam' )
	);
insert into PROJECT2BAM(project_id,bam_id) values(
	( select id from PROJECT where name = 'Exome3' ),
	( select id from BAM where path = '/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1893/X1893_recal.bam' )
	);
