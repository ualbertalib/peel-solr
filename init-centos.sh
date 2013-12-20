sudo su 

###
# update
###
yum -y update

###
# install software
###
yum -y install wget ant

###
# install oracle's java
###
if [ ! -f jdk-6u45-linux-x64-rpm.bin ]; then
  wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F" -O jdk-6u45-linux-x64-rpm.bin "http://download.oracle.com/otn-pub/java/jdk/6u45-b06/jdk-6u45-linux-x64-rpm.bin"
  chmod a+x jdk-6u45-linux-x64-rpm.bin
  ./jdk-6u45-linux-x64-rpm.bin
  chmod a+x jdk-6u45-linux-amd64.rpm
  rpm -Uvh jdk-6u45-linux-amd64.rpm
  alternatives --install /usr/bin/java java /usr/java/jdk1.6.0_45/jre/bin/java 20000
  alternatives --install /usr/bin/jar jar /usr/java/jdk1.6.0_45/bin/jar 20000
  alternatives --install /usr/bin/javac javac /usr/java/jdk1.6.0_45/bin/javac 20000
  alternatives --install /usr/bin/javaws javaws /usr/java/jdk1.6.0_45/jre/bin/javaws 20000
  alternatives --set java /usr/java/jdk1.6.0_45/jre/bin/java
  alternatives --set jar /usr/java/jdk1.6.0_45/bin/jar
  alternatives --set javac /usr/java/jdk1.6.0_45/bin/javac
  alternatives --set javaws /usr/java/jdk1.6.0_45/jre/bin/javaws
fi

###
# install Solr -- replace with rpm Solr
###
if [ ! -f solr-4.1.0.tgz ]; then
  wget http://archive.apache.org/dist/lucene/solr/4.1.0/solr-4.1.0.tgz
fi 
if [ ! -d /opt/solr ]; then
  gunzip -c solr-4.1.0.tgz | tar -xvf -	
  mv solr-4.1.0/example/ /opt/solr
fi 
cd /opt/solr			
java -jar start.jar > solr.log 2>&1 &	
sleep 20

###
# create peel-solr
###
cd /opt/solr_collections
ant -Dname=peel -DinstanceDir=/opt/solr_collections/peel create-core
ant 				# tests if Solr is installed, status
