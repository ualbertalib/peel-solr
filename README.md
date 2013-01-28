peel-solr
===========
This project deploys Solr with Peel (peel.library.ualberta.ca) configuration and plugins with or without Tomcat using Ant.

Usage:
------
    ant install-solr  // will try to deploy the /solr webapp
other

    ant uninstall-solr // will try to undeploy the /solr webapp
    ant clean // will remove the created files -- use before commit
    ant report // will run all tests and create tidy report of results
    ant dist-solr-plugins -- will create jar and install in solr.home/lib
    ant dist-peel-scripts // will create stand-alone jar for use by peel-scripts
    ant [info] // will display usage

Requirements:
-------------
* Tomcat and Manager application installed and configured
  * pay particular attention to setting user with appropriate roles (manager-gui) in $CATALINA_HOME/conf/tomcat-user.xml
  * *modify build.properties to reflect this configuration*
* Java 6+
* Apache Ant
* Apache Ivy
