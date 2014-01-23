package ca.ualibraries.dit.martini.index;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Indexer {
  
  static Logger logger = LoggerFactory.getLogger(Indexer.class);

  public static void main(String[] args) throws InterruptedException {
  	if (unknownArgs(args) || args.length < 1) {
      logger
          .warn("Usage: java -jar "
  						+ LocalMartiniIndexer.class.getName()
  						+ " <solr.home> [coreName=<corename> contentDir=<baseDirectory> config=<data-config> mountDate=<mountdate> poll=<pollFrequency in seconds>]");
  		return;
  	}
  	MartiniIndexer indexer;
  	  	
  	String solrHome = args[0];
    String coreName = getArg("coreName", args);
    long pollFrequency = 5000L;
    try {
      pollFrequency = Long.parseLong( getArg("poll", args) ) * 1000L;
    }catch(NumberFormatException e) {
      
    }
  	indexer = new LocalMartiniIndexer( solrHome, coreName );
  	  ((LocalMartiniIndexer) indexer).setConfig( getArg("config", args));
  	
  	try {
  		indexer.start(getArg("contentDir", args), getArg("mountDate", args));
  		while (indexer.poll()) {
  			Thread.sleep(pollFrequency);
  		};
      logger.info( indexer.report() );
  	} catch (SolrServerException e) {
  		logger.error("Fatal error: Problem indexing");
  		e.printStackTrace();
  		return;
  	} finally {
  		indexer.close();
  	}
  
  }

  private static String[] knownArgs = { "coreName", "contentDir", "config",
  			"mountDate", "poll" };

  private static boolean unknownArgs(String[] args) {
  	boolean isKnown = true;
  	
  	for (int i = 1; i < args.length; i++) {
  		String arg = args[i];
  		String[] keyValue = arg.split("=");
  		isKnown = false;
  		for( String known : knownArgs ) {
  			if (keyValue[0].equals(known))
  				isKnown = true;
  		}
  		if (!isKnown) {
  			logger.error("Unknown argument " + arg);
  			return !isKnown;
  		}
  	}
  	return false;
  }

  private static String getArg(String target, String[] args) {
  	for (String arg : args) {
  		String[] keyValue = arg.split("=");
  		if (target.equals(keyValue[0]))
  			return keyValue[1];
  	}
  	return null;
  }

  public Indexer() {
    super();
  }
  
}