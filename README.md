NGS PROJECTS
============

Java web server displaying **SAM**/**BAM** alignments.

Project status: **beta**

Author: Pierre Lindenbaum PhD. @yokofakun

Dependencies
------------

Tested with:

* Java 1.7
* Glassfish
* JavaDB (derby)
* Picard
* apache ant
* twitter bootsrap
* vizbam library for visualizing the BAM alignments. https://github.com/lindenb/vizbam

Screenshots
-----------

Displaying a BAM like `samtools tview` 

![tview](https://raw.github.com/lindenb/ngsproject/master/doc/tview.jpg)


Displaying a SAM like `samtools view` 

![view](https://raw.github.com/lindenb/ngsproject/master/doc/view.jpg)

Installation
------------

```bash
git clone "https://github.com/lindenb/ngsproject.git"
cd ngsproject.git
```

edit build.properties to configure the project. Something like:

```
asadmin=${glassfish.dir}/bin/asadmin
ivy.install.version=2.3.0
ivy.jar.dir=/commun/data/packages/ivy/jar
ivy.default.ivy.user.dir=${ivy.jar.dir}/../local
picard.version=1.87
picard.dir=/commun/data/packages/picard-tools-1.87/
vizbam=../vizbam/dist/vizbam.jar
```

install twitter bootsrap

```bash
ant install.bootstrap
```

create the database and the schema


```bash
ant start.database
```


create and start the server domain on glassfish

```bash
(TODO)
```


compile and deploy the application

```bash
ant deploy 
```




