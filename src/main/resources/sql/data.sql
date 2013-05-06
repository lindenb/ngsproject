set SCHEMA NGSPROJECTS;

insert into SAMPLE(id,name) values(1,'CD00172');
insert into SAMPLE(id,name) values(2,'CD01337');
insert into SAMPLE(id,name) values(3,'CD03840');
insert into SAMPLE(id,name) values(4,'CD03903');
insert into SAMPLE(id,name) values(5,'CD05294');
insert into SAMPLE(id,name) values(6,'CD05295');
insert into SAMPLE(id,name) values(7,'CD05296');
insert into SAMPLE(id,name) values(8,'V2528');
insert into SAMPLE(id,name) values(9,'X1564');
insert into SAMPLE(id,name) values(10,'X1565');
insert into SAMPLE(id,name) values(11,'X1566');
insert into SAMPLE(id,name) values(12,'X1567');
insert into SAMPLE(id,name) values(13,'X1607');
insert into SAMPLE(id,name) values(14,'X1608');
insert into SAMPLE(id,name) values(15,'X1891');
insert into SAMPLE(id,name) values(16,'X1893');

insert into REFERENCE(id,path,name,description) values (37,'/commun/data/pubdb/broadinstitute.org/bundle/1.5/b37/human_g1k_v37.fasta','human_g1k_v37','human_g1k_v37');


insert into BAM(id,path,sample_id,reference_id) values(1,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD00172/CD00172_recal.bam',1,37);
insert into BAM(id,path,sample_id,reference_id) values(2,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD01337/CD01337_recal.bam',2,37);
insert into BAM(id,path,sample_id,reference_id) values(3,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD03840/CD03840_recal.bam',3,37);
insert into BAM(id,path,sample_id,reference_id) values(4,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD03903/CD03903_recal.bam',4,37);
insert into BAM(id,path,sample_id,reference_id) values(5,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05294/CD05294_recal.bam',5,37);
insert into BAM(id,path,sample_id,reference_id) values(6,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05295/CD05295_recal.bam',6,37);
insert into BAM(id,path,sample_id,reference_id) values(7,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/CD05296/CD05296_recal.bam',7,37);
insert into BAM(id,path,sample_id,reference_id) values(8,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/V2528/V2528_recal.bam',8,37);
insert into BAM(id,path,sample_id,reference_id) values(9,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1564/X1564_recal.bam',9,37);
insert into BAM(id,path,sample_id,reference_id) values(10,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1565/X1565_recal.bam',10,37);
insert into BAM(id,path,sample_id,reference_id) values(11,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1566/X1566_recal.bam',11,37);
insert into BAM(id,path,sample_id,reference_id) values(12,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1567/X1567_recal.bam',12,37);
insert into BAM(id,path,sample_id,reference_id) values(13,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1607/X1607_recal.bam',13,37);
insert into BAM(id,path,sample_id,reference_id) values(14,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1608/X1608_recal.bam',14,37);
insert into BAM(id,path,sample_id,reference_id) values(15,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1891/X1891_recal.bam',15,37);
insert into BAM(id,path,sample_id,reference_id) values(16,'/commun/data/projects/20130201.SNL149_0019_AD1HJ6ACXX.Exome3/align/X1893/X1893_recal.bam',16,37);



insert into PROJECT(id,name,description,group_id) values (1,'Exome3','Exome3',1);

insert into PROJECT2BAM(id,project_id,bam_id) values(1,1,1);
insert into PROJECT2BAM(id,project_id,bam_id) values(2,1,2);
insert into PROJECT2BAM(id,project_id,bam_id) values(3,1,3);
insert into PROJECT2BAM(id,project_id,bam_id) values(4,1,4);
insert into PROJECT2BAM(id,project_id,bam_id) values(5,1,5);
insert into PROJECT2BAM(id,project_id,bam_id) values(6,1,6);
insert into PROJECT2BAM(id,project_id,bam_id) values(7,1,7);
insert into PROJECT2BAM(id,project_id,bam_id) values(8,1,8);
insert into PROJECT2BAM(id,project_id,bam_id) values(9,1,9);
insert into PROJECT2BAM(id,project_id,bam_id) values(10,1,10);
insert into PROJECT2BAM(id,project_id,bam_id) values(11,1,11);
insert into PROJECT2BAM(id,project_id,bam_id) values(12,1,12);
insert into PROJECT2BAM(id,project_id,bam_id) values(13,1,13);
insert into PROJECT2BAM(id,project_id,bam_id) values(14,1,14);
insert into PROJECT2BAM(id,project_id,bam_id) values(15,1,15);
insert into PROJECT2BAM(id,project_id,bam_id) values(16,1,16);
