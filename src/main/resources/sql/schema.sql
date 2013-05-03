create table if not exists USERGROUP
	(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT
	);

insert or ignore into USERGROUP(id,name) values(1,"public");
	
create table if not exists USERS
	(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT
	);

insert or ignore into USERGROUP(id,name) values(1,"root");
insert or ignore into USERGROUP(id,name) values(2,"anonymous");
	
	
create table if not exists REFERENCE
	(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	path TEXT,
	name TEXT,
	description TEXT
	);

	
create table if not exists SAMPLE
	(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT
	);	
	
	
create table if not exists BAM
	(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	path TEXT,
	sample_id INTEGER,
	reference_id INTEGER,
	
	FOREIGN KEY(sample_id ) REFERENCES SAMPLE(id),
	FOREIGN KEY(reference_id ) REFERENCES REFERENCE(id)
	);	

create table if not exists PROJECT
	(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT,
	description TEXT,
	group_id INTEGER,
	FOREIGN KEY(group_id ) REFERENCES USERGROUP(id)
	);	

	
	
create table if not exists USER2GROUP
	(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	user_id INTEGER,
	group_id INTEGER,
	FOREIGN KEY(user_id ) REFERENCES USERS(id),
	FOREIGN KEY(group_id ) REFERENCES USERGROUP(id)
	);	
	
create table if not exists PROJECT2BAM
	(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	project_id INTEGER,
	bam_id INTEGER,
	FOREIGN KEY(project_id ) REFERENCES PROJECT(id),
	FOREIGN KEY(bam_id ) REFERENCES BAM(id)
	);	

