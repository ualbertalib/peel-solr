package ca.ualibraries.dit.martini.index;

import static org.junit.Assert.*;

import org.apache.solr.SolrJettyTestBase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@org.apache.lucene.util.LuceneTestCase.SuppressCodecs({"Lucene3x","Lucene40"})
public class IndexerTest extends SolrJettyTestBase {
    
  @BeforeClass
  public static void init() throws Exception {
    createJetty( "src/solr.home", null, null );  
  }
  
  @Test
  public void testLocal() throws Exception {
    String[] args = {"src/solr.home", "coreName=peel", "contentDir=test-files/indexing/",  "config=peel-bib-data-config-indexing.xml", "poll=30"};
    Indexer.main(args);
  }
  
}
