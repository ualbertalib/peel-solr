package ca.ualibraries.dit.martini.index;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.solr.SolrJettyTestBase;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.BeforeClass;
import org.junit.Test;

@org.apache.lucene.util.LuceneTestCase.SuppressCodecs({"Lucene3x","Lucene40"})
public class HttpMartiniIndexerTest extends SolrJettyTestBase {
  
  private static HttpMartiniIndexer hmi;

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private Date date = new Date();
  
  static List<String> defaultFields = new ArrayList<String>();

  @BeforeClass
  public static void init() throws Exception {
    Collections.addAll(defaultFields,"modified", "uri", "mountDate");
    
    createJetty("src/solr.home", null, null);
    String solrHome = "http://127.0.0.1:"+ jetty.getLocalPort() + "/solr";

    hmi = new HttpMartiniIndexer(solrHome, "peel");
    
    server = new HttpSolrServer( solrHome + "/" + "peel" );
  }
   
  @Test
  public void testPeelBib() throws SolrServerException, IOException {

    server.deleteByQuery("*:*");
    hmi.start("test-files/indexing/peelbib/", dateFormat.format(date));
    assertTrue("should return status=0", hmi.report().contains("status=0") );
    
    ModifiableSolrParams qparams = new ModifiableSolrParams();
    qparams.add("q", "*:*");
    QueryResponse qres = server.query(qparams);
    SolrDocumentList results = qres.getResults();
    assertEquals(296, results.getNumFound());
    
    List<String> fields = new ArrayList( defaultFields );
    Collections.addAll(fields,"peelnum");
    for( String field : fields )
    assertTrue( "should contain " + field, results.get(0).containsKey( field ) );
  }
  
  @Test
  public void testNews() throws SolrServerException, IOException {
    
    server.deleteByQuery("*:*");
    hmi.start("test-files/indexing/newspapers/", dateFormat.format(date));
    assertTrue("should return status=0", hmi.report().contains("status=0") );
    
    ModifiableSolrParams qparams = new ModifiableSolrParams();
    qparams.add("q", "*:*");
    QueryResponse qres = server.query(qparams);
    SolrDocumentList results = qres.getResults();
    assertEquals(329, results.getNumFound());
    
    List<String> fields = new ArrayList( defaultFields );
    for( String field : fields )
    assertTrue( "should contain " + field, results.get(0).containsKey( field ) );
  }
  
}
