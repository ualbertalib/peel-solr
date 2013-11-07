package ca.ualibraries.dit.martini.index;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.SolrJettyTestBase;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.BeforeClass;
import org.junit.Test;

@org.apache.lucene.util.LuceneTestCase.SuppressCodecs({"Lucene3x","Lucene40"})
public class HttpMartiniIndexerTest extends SolrJettyTestBase {
  
  private static HttpMartiniIndexer hmi;

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private Date date = new Date();

  @BeforeClass
  public static void init() throws Exception {
    createJetty("src/solr.home", null, null);
    String solrHome = "http://127.0.0.1:"+ jetty.getLocalPort() + "/solr";
    /*createJetty(solrHome, null, null);*/
    hmi = new HttpMartiniIndexer(solrHome, "peel");
  }
 
  @Test
  public void testPeelBib() throws SolrServerException {
    hmi.start("test-files/indexing/peelbib/", dateFormat.format(date));
    assertTrue("should return status=0", hmi.report().contains("status=0") );
  }
  
  @Test
  public void testNews() throws SolrServerException {
    hmi.start("test-files/indexing/newspapers/", dateFormat.format(date));
    assertTrue("should return status=0", hmi.report().contains("status=0") );
  }
  
}
