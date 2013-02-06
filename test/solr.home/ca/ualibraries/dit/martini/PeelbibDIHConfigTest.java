package ca.ualibraries.dit.martini;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.BeforeClass;
import org.junit.Test;

public class PeelbibDIHConfigTest extends SolrTestCaseJ4 {

	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig.xml", "schema.xml", getFile("solr.home")
				.getAbsolutePath(), "martini");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		// request to dataimport require baseDir and mountdate as a parameter
		LocalSolrQueryRequest request = lrf.makeRequest("command",
				"full-import", "config",
 "peel-bib-data-config-indexing.xml",
				"clean", "true",
				"commit", "true", "synchronous", "true", "indent", "true",
 "baseDir", getFile("indexing/peelbib/")
						.getAbsolutePath(),
				"mountdate", dateFormat.format(date));
		h.query("/dataimport", request);
		assertQ(req("q", "*:*", "rows", "292"), testAll);
	}

	@Test
	public void testRepresentativeLanguageSample() {
		assertQ(req("q", "language:en", "rows", "283", "fl", "peelnum"), testEn);
		assertQ(req("language:fr"), testFr);
		assertQ(req("language:hu"), testHu);
		assertQ(req("language:de"), testDe);
	}

	@Test
	public void testRepresentativeContentSample() {
		assertQ(req("peelnum:000001 peelnum:000012 peelnum:002526 peelnum:000908 peelnum:004265 peelnum:000002"),
				testRecordOnly);
	}

	private String[] testRecordOnly = { "//result[@numFound='6']",
			"count(//arr[@name='content'])=0 or //arr[@name='content']/str[not(node())]" };
	
	private String[] testEn = { "//result[@numFound='283']",
			"//str[@name='peelnum']='000001'",
			"//str[@name='peelnum']='000002'",
			"//str[@name='peelnum']='002848'",
			"//str[@name='peelnum']='000089'",
			"//str[@name='peelnum']='000315'",
			"//str[@name='peelnum']='009021.1-2.3-1'",
			"//str[@name='peelnum']='010571'" };
	private String[] testFr = { "//result[@numFound='2']",
			"//str[@name='peelnum']='000081'",
			"//str[@name='peelnum']='000012'" };
	private String[] testHu = { "//result[@numFound='2']",
			"//str[@name='peelnum']='002490'",
			"//str[@name='peelnum']='002526'" };
	private String[] testDe = { "//result[@numFound='2']",
			"//str[@name='peelnum']='000089'",
			"//str[@name='peelnum']='000908'" };
	private static String[] testAll = { "//result[@numFound='292']",
			"//str[@name='uid']", "//str[@name='peelnum']",
			"//arr[@name='language']", "//str[@name='pubyear']",
			"//str[@name='actyear']", "//str[@name='digstatus']",
			"//str[@name='mountDate']", "//str[@name='bibrecord']",
			"//str[@name='collection']='peelbib'",
			"//str[@name='title']", "//str[@name='author']",
			"//arr[@name='subject_en']", "//arr[@name='subject_fr']",
			"//arr[@name='geodisplay_en']",
			"//arr[@name='origindisplay']", "//str[@name='digstatus']",
			"//arr[@name='content']" };
}
