package ca.ualibraries.dit.martini;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.BeforeClass;
import org.junit.Test;

public class CocoonPeelbibQueryTest extends SolrTestCaseJ4 {

	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig.xml", "schema.xml", getFile("solr.home")
				.getAbsolutePath(), "martini");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		// request to dataimport require baseDir and mountdate as a parameter
		LocalSolrQueryRequest request = lrf.makeRequest("command",
				"full-import", "config", "peel-bib-data-config-indexing.xml",
				"clean", "true", "commit", "true", "synchronous", "true",
				"indent", "true", "baseDir", getFile("indexing/peelbib/")
						.getAbsolutePath(), "mountdate", dateFormat
						.format(date));
		h.query("/dataimport", request);
		assertQ(req("*:*"), "//result[@numFound='292']");
	}

	@Test
	public void testAdvancePeelbibQuery() {
		assertQ(req(
				"echoParams",
				"all",
				"qt",
				"standard",
				"wt",
				"standard",
				"fl",
				"*,score",
				"q",
				"pubyear:(1850 TO 1860) +bibrecord:(book of common prayer) +actyear:(1856) +digstatus:(mounted) +peelnum:(329)",
				"fq", "language:(cre)"), tests);
	}

	@Test
	public void testFacetsPeelbibQuery() {
		assertQ(req("q", "*:*", "facet", "true", "facet.mincount", "1",
				"facet.field", "geodisplay", "facet.field", "authordisplay",
				"facet.field", "subjectdisplay", "facet.field", "language",
				"facet.field", "digstatus", "facet.field", "pubyear", "rows",
				"0"), facetTests
				);
	}

	String[] tests = { "//result[@numFound='1']", "//doc/str[@name='peelnum']",
			"//doc/str[@name='titledisplay']",
			"//doc/arr[@name='authordisplay']",
			"//doc/arr[@name='origindisplay']", "//doc/str[@name='pubyear']",
			"//doc/arr[@name='language']", "//doc/str[@name='digstatus']" };

	String[] facetTests = {
 "//lst[@name='facet_fields']/lst[@name='geodisplay']",
			"//lst[@name='facet_fields']/lst[@name='authordisplay']",
			"//lst[@name='facet_fields']/lst[@name='subjectdisplay']",
			"//lst[@name='facet_fields']/lst[@name='language']",
			"//lst[@name='facet_fields']/lst[@name='digstatus']",
			"//lst[@name='facet_fields']/lst[@name='pubyear']"
	};
}
