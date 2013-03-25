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

	@Test
	public void testPositionsPeelbibQuery() {
		
		/* Examples:     
|type|query|hits|
|phrase|"rocky mountains"|124|
|boolean|horse dog|275|
|boolean|horse OR dog|275|
|boolean|horse AND dog|160|
|boolean|horse -dog|102|
|boolean|horse NOT dog|102|
|boolean|(horse OR dog) AND cat|61|
|fuzzy|horse~|282|
|proximity|"horse dog"~100|35|
|truncation|horse*|273| */
		// term query
		assertQ(req("q", "text:horse", "fl", "null", "hl", "true",
				"hl.usePhraseHighlighter", "true", "hl.highlightMultiTerm",
				"true", "hl.requireFieldMatch", "true"),
				enPositionsTests);
		// term query with french
		assertQ(req("q", "text:français", "fl", "null", "hl", "true",
				"hl.usePhraseHighlighter", "true", "hl.highlightMultiTerm",
				"true", "hl.requireFieldMatch", "true"), frPositionsTests);

		// term query with cree
		assertQ(req("q", "text:Kwayask ê-kî-pê-kiskinowâpahtihicik", "fl",
				"null", "hl", "true", "hl.usePhraseHighlighter", "true",
				"hl.highlightMultiTerm", "true", "hl.requireFieldMatch", "true"),
				crPositionsTests);

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

	String[] enPositionsTests = {
			"10 = count(//lst[@name='highlighting']/lst)",
			"21 = count(//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='text']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='text']/int='2388'",
			"//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='text']/int='6442'",
			"42 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='text']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='text']/int='239'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='text']/int='17023'" };

	String[] frPositionsTests = {
			"2 = count(//lst[@name='highlighting']/lst)",
			"2 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.22.1_bib.properties']/arr[@name='text']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.22.1_bib.properties']/arr[@name='text']/int='19229'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.22.1_bib.properties']/arr[@name='text']/int='19610'",
			"2 = count(//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='text']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='text']/int='37790'",
			"//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='text']/int='51823'" };

	String[] crPositionsTests = {
			"1 = count(//lst[@name='highlighting']/lst)",
			"3 = count(//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='text']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='text']/int='20068'",
			"//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='text']/int='51368'" };
}
