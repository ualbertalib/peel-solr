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
		assertQ(req("echoParams", "all", "qt", "standard", "wt", "standard",
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

	@Test
	public void testTermQueryPositionsNewspaperQuery() {
		assertQ(req("q", "text:horse", "fl", "null", "hl", "true",
				"hl.usePhraseHighlighter", "true", "hl.highlightMultiTerm",
				"true", "hl.fl", "content"), enPositionsTests);
	}

	@Test
	public void testFrTermQueryPositionsNewspaperQuery() {
		assertQ(req("q", "text:français", "fl", "null", "hl", "true",
				"hl.usePhraseHighlighter", "true", "hl.highlightMultiTerm",
				"true", "hl.fl", "content"), frPositionsTests);
	}

	@Test
	public void testPhraseQueryPositionsNewspaperQuery() {
		assertQ(req("q", "text:\"junior football\"", "fl", "null", "hl",
				"true", "hl.usePhraseHighlighter", "true",
				"hl.highlightMultiTerm", "true", "hl.fl", "content"),
				phrasePositionsTests);
	}

	@Test
	public void testBooleanQueryPositionsNewspaperQuery() {
		assertQ(req("q", "text:(horse OR dog) AND cat", "fl", "null", "hl",
				"true", "hl.usePhraseHighlighter", "true",
				"hl.highlightMultiTerm", "true", "hl.fl", "content"),
				booleanPositionsTests);
	}

	@Test
	public void testProximityQueryPositionsNewspaperQuery() {
		assertQ(req("q", "text:\"alberta saskatchewan\"~100", "fl", "null",
				"hl", "true",
				"hl.usePhraseHighlighter", "true", "hl.highlightMultiTerm",
				"true", "hl.fl", "content"), proximityPositionsTests);
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

	String[] enPositionsTests = {
			"2 = count(//lst[@name='highlighting']/lst)",
			"1 = count(//lst[@name='highlighting']/lst[@name='newspapers_CEO_1908_10_03_3_Ad00305_2_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='newspapers_CEO_1908_10_03_3_Ad00305_2_bib.properties']/arr[@name='content']/int='190'",
			"2 = count(//lst[@name='highlighting']/lst[@name='newspapers_CEO_1908_10_03_1_Ar00103_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='newspapers_CEO_1908_10_03_1_Ar00103_bib.properties']/arr[@name='content']/int='6898'",
			"//lst[@name='highlighting']/lst[@name='newspapers_CEO_1908_10_03_1_Ar00103_bib.properties']/arr[@name='content']/int='11058'" };

	String[] frPositionsTests = {
			"10 = count(//lst[@name='highlighting']/lst)",
			"3 = count(//lst[@name='highlighting']/lst[@name='newspapers_LLP_1956_08_10_5_Ar00513_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='newspapers_LLP_1956_08_10_5_Ar00513_bib.properties']/arr[@name='content']/int='2'",
			"//lst[@name='highlighting']/lst[@name='newspapers_LLP_1956_08_10_5_Ar00513_bib.properties']/arr[@name='content']/int='73'",
			"1 = count(//lst[@name='highlighting']/lst[@name='newspapers_LLP_1956_08_10_4_Ar00412_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='newspapers_LLP_1956_08_10_4_Ar00412_bib.properties']/arr[@name='content']/int='135'" };

	String[] phrasePositionsTests = {
			"1 = count(//lst[@name='highlighting']/lst)",
			"4 = count(//lst[@name='highlighting']/lst[@name='newspapers_GAT_1934_11_09_martini_4_Ar00400_bib.properties']/arr[@name='content']/int)",
			// notice the phrase appears as pairs
			"//lst[@name='highlighting']/lst[@name='newspapers_GAT_1934_11_09_martini_4_Ar00400_bib.properties']/arr[@name='content']/int='107'",
			"//lst[@name='highlighting']/lst[@name='newspapers_GAT_1934_11_09_martini_4_Ar00400_bib.properties']/arr[@name='content']/int='108'",
			"//lst[@name='highlighting']/lst[@name='newspapers_GAT_1934_11_09_martini_4_Ar00400_bib.properties']/arr[@name='content']/int='751'",
			"//lst[@name='highlighting']/lst[@name='newspapers_GAT_1934_11_09_martini_4_Ar00400_bib.properties']/arr[@name='content']/int='752'" };

	String[] booleanPositionsTests = {
			"1 = count(//lst[@name='highlighting']/lst)",
			"4 = count(//lst[@name='highlighting']/lst[@name='newspapers_CEO_1908_10_03_1_Ar00103_bib.properties']/arr[@name='content']/int)",
			"//lst[@name='highlighting']/lst[@name='newspapers_CEO_1908_10_03_1_Ar00103_bib.properties']/arr[@name='content']/int='279'",
			"//lst[@name='highlighting']/lst[@name='newspapers_CEO_1908_10_03_1_Ar00103_bib.properties']/arr[@name='content']/int='11058'" };

	String[] proximityPositionsTests = {
			"2 = count(//lst[@name='highlighting']/lst)",
			"2 = count(//lst[@name='highlighting']/lst[@name='newspapers_GAT_1934_11_09_martini_1_Ar00101_bib.properties']/arr[@name='content']/int)",
			// in pairs with a distance less than 100
			"//lst[@name='highlighting']/lst[@name='newspapers_GAT_1934_11_09_martini_1_Ar00101_bib.properties']/arr[@name='content']/int='341'",
			"//lst[@name='highlighting']/lst[@name='newspapers_GAT_1934_11_09_martini_1_Ar00101_bib.properties']/arr[@name='content']/int='349'" };

}
