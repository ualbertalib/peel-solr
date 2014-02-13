package ca.ualibraries.dit.martini.index;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalMartiniIndexerTest {

	private static LocalMartiniIndexer lmi;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private Date date = new Date();
	static Logger logger = LoggerFactory.getLogger(Indexer.class);

	@BeforeClass
	public static void init() {
    lmi = new LocalMartiniIndexer("src/solr.home", "peel");
	}
	@Test
	public void testPeelBibDataImport() throws SolrServerException,
			InterruptedException {

    lmi.setConfig("peel-bib-data-config-indexing.xml");
		lmi.start("test-files/indexing/peelbib/", dateFormat.format(date));
		while (lmi.poll()) {
			Thread.sleep(5000L);
		}
		;
		assertEquals(
				"Indexing completed. Added/Updated: 296 documents. Deleted 0 documents.",
				lmi.report());
	}

	@Test
	public void testPeelBibDataImportMissingDateMounted()
			throws SolrServerException, InterruptedException {

    lmi.setConfig("peel-bib-data-config-indexing.xml");
		lmi.start("test-files/indexing/peelbib/", null);
		while (lmi.poll()) {
			Thread.sleep(5000L);
		}
		;
		assertEquals(
				"Indexing completed. Added/Updated: 296 documents. Deleted 0 documents.",
				lmi.report());
	}

	@Test
	public void testPeelNewsDataImport() throws SolrServerException,
			InterruptedException {
	  
    lmi.setConfig("peel-newspapers-data-config-indexing.xml");
		lmi.start("test-files/indexing/newspapers/", dateFormat.format(date));
		while (lmi.poll()) {
			Thread.sleep(5000L);
		}
		;
		assertEquals(
        "Indexing completed. Added/Updated: 323 documents. Deleted 0 documents.",
				lmi.report());

	}
	
	@Test
  public void testGZipIgnore() throws SolrServerException,
      InterruptedException {
    
    lmi.setConfig("peel-newspapers-data-config-indexing.xml");
    lmi.start("test-files/indexing/newspapers/LLT", dateFormat.format(date));
    while (lmi.poll()) {
      Thread.sleep(5000L);
    }
    ;
    assertEquals(
        "Indexing completed. Added/Updated: 0 documents. Deleted 0 documents.",
        lmi.report());

  }

	@AfterClass
	public static void close() {
		lmi.close();
	}

}
