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
	public void testTermQueryPositionsPeelbibQuery() {
		assertQ(req("q", "text:horse", "fl", "null", "hl", "true",
				"hl.usePhraseHighlighter", "true", "hl.highlightMultiTerm",
				"true", "hl.fl", "content"), enPositionsTests);
	}

	@Test
	public void testFrTermQueryPositionsPeelbibQuery() {
		assertQ(req("q", "text:français", "fl", "null", "hl", "true",
				"hl.usePhraseHighlighter", "true", "hl.highlightMultiTerm",
				"true", "hl.fl", "content"), frPositionsTests);
	}

	@Test
	public void testPhraseQueryPositionsPeelbibQuery() {
		assertQ(req("q", "text:\"rocky mountains\"", "fl",
				"null", "hl", "true", "hl.usePhraseHighlighter", "true",
				"hl.highlightMultiTerm", "true", "hl.fl", "content"),
				phrasePositionsTests);
	}

	@Test
	public void testBooleanQueryPositionsPeelbibQuery() {
		assertQ(req("q", "text:(horse -dog) AND cat", "fl", "null", "hl",
				"true", "hl.usePhraseHighlighter", "true",
				"hl.highlightMultiTerm", "true", "hl.fl", "content"),
				booleanPositionsTests);
	}

	@Test
	public void testFuzzyQueryPositionsPeelbibQuery() {
		assertQ(req("q", "text:horse~", "fl", "null", "hl",
				"true", "hl.usePhraseHighlighter", "true",
				"hl.highlightMultiTerm", "true", "hl.fl", "content"),
				fuzzyPositionsTests);
	}

	@Test
	public void testProximityQueryPositionsPeelbibQuery() {
		assertQ(req("q", "text:\"horse dog\"~100", "fl", "null", "hl",
				"true", "hl.usePhraseHighlighter", "true",
				"hl.highlightMultiTerm", "true", "hl.fl", "content"),
				proximityPositionsTests);
	}

	@Test
	public void testTruncationQueryPositionsPeelbibQuery() {
		assertQ(req("q", "text:horse*", "fl", "null", "hl",
				"true", "sort", "uid asc", "hl.usePhraseHighlighter",
				"true",
				"hl.highlightMultiTerm", "true", "hl.fl", "content"),
				truncationPositionsTests);
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
			"21 = count(//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='content']/int='1606'",
			"//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='content']/int='4664'",
			"42 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='content']/int='23'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='content']/int='16818'" };

	String[] frPositionsTests = {
			"2 = count(//lst[@name='highlighting']/lst)",
			"2 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.22.1_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.22.1_bib.properties']/arr[@name='content']/int='19020'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.22.1_bib.properties']/arr[@name='content']/int='19363'",
			"2 = count(//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='content']/int='33126'",
			"//lst[@name='highlighting']/lst[@name='peelbib_81_bib.properties']/arr[@name='content']/int='45703'" };

	String[] phrasePositionsTests = {
			"10 = count(//lst[@name='highlighting']/lst)",
			"40 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.10.4_bib.properties']/arr[@name='content']/int)",
			// notice the phrase appears as pairs
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.10.4_bib.properties']/arr[@name='content']/int='4860'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.10.4_bib.properties']/arr[@name='content']/int='4861'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.10.4_bib.properties']/arr[@name='content']/int='11044'", 
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.10.4_bib.properties']/arr[@name='content']/int='11045'",
			"16 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.30.4_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.30.4_bib.properties']/arr[@name='content']/int='480'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.30.4_bib.properties']/arr[@name='content']/int='5849'" };

	String[] booleanPositionsTests = {
			"10 = count(//lst[@name='highlighting']/lst)",
			"15 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.21.4_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.21.4_bib.properties']/arr[@name='content']/int='5906'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.21.4_bib.properties']/arr[@name='content']/int='16583'",
			"7 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.31.4_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.31.4_bib.properties']/arr[@name='content']/int='633'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.31.4_bib.properties']/arr[@name='content']/int='20603'" };

	String[] fuzzyPositionsTests = {
			"10 = count(//lst[@name='highlighting']/lst)",
			"95 = count(//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='content']/int='413'",
			"//lst[@name='highlighting']/lst[@name='peelbib_2490_bib.properties']/arr[@name='content']/int='4820'",
			"99 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.44.2_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.44.2_bib.properties']/arr[@name='content']/int='220'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.44.2_bib.properties']/arr[@name='content']/int='17975'" };

	String[] proximityPositionsTests = {
			"10 = count(//lst[@name='highlighting']/lst)",
			"8 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='content']/int)",
			// in pairs with a distance less than 100
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='content']/int='768'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='content']/int='787'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='content']/int='13030'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.35.3_bib.properties']/arr[@name='content']/int='13057'",
			"2 = count(//lst[@name='highlighting']/lst[@name='peelbib_9021.4.3_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.4.3_bib.properties']/arr[@name='content']/int='382'",
			"//lst[@name='highlighting']/lst[@name='peelbib_9021.4.3_bib.properties']/arr[@name='content']/int='475'" };

	String[] truncationPositionsTests = {
			"10 = count(//lst[@name='highlighting']/lst)",
			// more results than horse alone, in different positions
			"4 = count(//lst[@name='highlighting']/lst[@name='peelbib_10571.10_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_10571.10_bib.properties']/arr[@name='content']/int='134'",
			"//lst[@name='highlighting']/lst[@name='peelbib_10571.10_bib.properties']/arr[@name='content']/int='8194'",
			"8 = count(//lst[@name='highlighting']/lst[@name='peelbib_10571.20_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='peelbib_10571.20_bib.properties']/arr[@name='content']/int='1334'",
			"//lst[@name='highlighting']/lst[@name='peelbib_10571.20_bib.properties']/arr[@name='content']/int='15009'" };
}
