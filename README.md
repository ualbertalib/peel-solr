deploy-solr
===========
This project downloads the latest release of Solr and deploys it in Tomcat using Ant.

Usage:
------
    ant deploy  // will try to deploy the /solr webapp
other

    ant undeploy // will try to undeploy the /solr webapp
    ant [info] // will display usage

Requirements:
-------------
* Tomcat and Manager application installed and configured
  * pay particular attention to setting user with appropriate roles (manager-gui) in $CATALINA_HOME/conf/tomcat-user.xml
  * *modify build.properties to reflect this configuration*
* Java 6+
* Apache Ant
* Apache Ivy
