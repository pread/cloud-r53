h1. cloud-r53: Spring 3 REST Template to call Amazon Route 53 

This is an example how to use Spring 3 REST Template to call Amazon Route 53 to update the DNS name of images. The applicaton will query AWS images that have a defined tag name, e.g. ShortName. All running images with this tag name will be assigned a CNAME, e.g. if ShortName=dev1 then DNS will be updated as dev1.domain.com.

# Written in Java
# Build by Maven 
# Uses Spring 3 REST Template to call Amazon Route 53

h2. Get sources

<pre>
$ git clone https://github.com/pread/cloud-r53.git
</pre>

h2. Build and Run

<pre>
$ mvn install -DskipTests=true exec:java -Dexec.mainClass="com.amazonaws.services.route53.scripts.CloudR53"
</pre>