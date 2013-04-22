package ca.ualibraries.dit.martini;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.BeforeClass;
import org.junit.Test;

public class PeelNewspapersDIHConfigTest extends SolrTestCaseJ4 {

	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig.xml", "schema.xml", getFile("solr.home")
				.getAbsolutePath(), "martini");
	}

	@Test
	public void testEndToEnd() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		// request to dataimport require baseDir and mountdate as a parameter
		LocalSolrQueryRequest request = lrf.makeRequest("command",
				"full-import", "config",
				"peel-newspapers-data-config-indexing.xml", "clean", "true",
				"commit", "true", "synchronous", "true", "indent", "true",
				"baseDir", getFile("indexing/newspapers/").getAbsolutePath(),
				"mountdate", dateFormat.format(date));
		h.query("/dataimport", request);
		assertQ(req("q", "*:*", "rows", "323"), testAll);
		assertQ(req("q", "language:en", "rows", "86", "fl", "publication"),
				testEn);
		assertQ(req("q", "language:fr", "rows", "237", "fl", "publication"),
				testFr);
	}

	private String[] testEn = { "//result[@numFound='86']",
			"//str[@name='publication']='CEO'",
			"//str[@name='publication']='GAT'" };
	private String[] testFr = { "//result[@numFound='237']",
			"//str[@name='publication']='PDW'",
			"//str[@name='publication']='ESA'",
			"//str[@name='publication']='LLP'" };
	private String[] testAll = {
 "//result[@numFound='323']",
			"//arr[@name='language']", "//int[@name='pubyear']",
			"//str[@name='actyear']", "//str[@name='digstatus']",
			"//str[@name='mountDate']", "//str[@name='publication']",
			"//str[@name='date']", "//str[@name='editionpath']",
			"//str[@name='type']", "//str[@name='size']",
			"//str[@name='page']", "//str[@name='article']",
			"//str[@name='headline-image']", "//str[@name='headline-width']",
			"//str[@name='headline-height']", "//str[@name='bibrecord']",
			"//str[@name='collection']='newspapers'",
			"//str[@name='uri']" };
}
