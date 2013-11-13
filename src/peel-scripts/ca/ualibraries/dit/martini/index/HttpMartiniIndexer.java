package ca.ualibraries.dit.martini.index;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpMartiniIndexer implements MartiniIndexer {

  private static SolrServer server;
  private UpdateResponse lastResponse;
  static Logger logger = LoggerFactory.getLogger(HttpMartiniIndexer.class);
  
  public HttpMartiniIndexer(String url, String core) {
    server = new HttpSolrServer( url + "/" + core );
  }

  public void start(String baseDir, String mountDate) throws SolrServerException {
    
    Path startingDir = Paths.get(baseDir);
    LukeRequest luke = new LukeRequest();
    luke.setShowSchema(true);
    try {
      LukeResponse process = luke.process(server);
      SolrizeFiles sf = new SolrizeFiles( process.getFieldInfo(), mountDate );
      Files.walkFileTree(startingDir, sf);
      lastResponse = server.add( sf.getDocs() );
      lastResponse = server.commit();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 
  }

  public boolean poll() throws SolrServerException {
    return false;
  }

  public void close() {   
    server.shutdown();
  }

  public String report() throws SolrServerException {
    return lastResponse.toString();
  }
  
}
