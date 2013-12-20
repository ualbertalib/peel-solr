peel-solr
===========
Solr configuration and plugins for Peel (peel.library.ualberta.ca).

Usage:
------
    vagrant up // will deploy solr and peel in a vm available from http://solr-server:8983/solr in your browser
    ant install-solr  // will try to deploy the /solr webapp -- requires Tomcat Manager
    ant uninstall-solr // will try to undeploy the /solr webapp
    
    ant clean // will remove the created files -- use before commit
    
    ant test  // will run all tests
    ant test-solrmeter // will run solrmeter for performance/load testing (requires windowing os)
    ant report // will run all tests and create tidy report of results
    
    ant dist-solr-plugins -- will create jar and install in solr.home/lib
    ant dist-peel-scripts // will create stand-alone jar for use by peel-scripts
    
    ant [info] // will display usage

Requirements:
-------------
* Java 6+
* Apache Ant
* Apache Ivy (will download and install Ivy if not available)
* Vagrant (with vagrant-hostupdater)
* Tomcat 6+
* Tomcat Manager application installed and configured in order to use install-solr/uninstall-solr targets
  * pay particular attention to setting user with appropriate roles (manager-gui) in $CATALINA_HOME/conf/tomcat-user.xml
  * *modify build.properties to reflect this configuration*
