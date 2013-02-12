package ca.ualibraries.dit.martini;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.BeforeClass;
import org.junit.Test;

public class CocoonNewspapersQueryTest extends SolrTestCaseJ4 {

	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig.xml", "schema.xml", getFile("solr.home")
				.getAbsolutePath(), "martini");
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

		assertQ(req("*:*"), "//result[@numFound='323']");
	}

	@Test
	public void testAdvanceNewspaperQuery() {
		assertQ(
req("echoParams", "all", "qt", "standard", "wt", "standard",
				"fl", "*,score", "q", "pubyear:(1913)", "fq", "language:(fr)",
				"fq", "size:(large+OR+medium+OR+xlarge+OR+small)", "fq",
				"type:(ad+OR+picture+OR+article)"), tests);
	}

	@Test
	public void testFacetsNewspaperQuery() {
		assertQ(req("q", "*:*", "facet", "true", "facet.mincount", "1",
				"facet.field", "language", "facet.field", "pubyear",
				"facet.field", "publication", "rows", "0"), facetTests);
	}

	String[] tests = { "//result[@numFound='15']",
			"//doc/str[@name='publication']", "//doc/str[@name='date']",
			"//doc/str[@name='page']", "//doc/str[@name='type']",
			"//doc/str[@name='size']", "//doc/arr[@name='language']",
			"//doc/str[@name='article']" };
	String[] facetTests = {
			"//lst[@name='facet_fields']/lst[@name='language']",
			"//lst[@name='facet_fields']/lst[@name='pubyear']",
			"//lst[@name='facet_fields']/lst[@name='publication']" };
}
