package org.apache.solr.highlight;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.handler.component.HighlightComponent;
import org.junit.BeforeClass;

@org.apache.lucene.util.LuceneTestCase.SuppressCodecs({"Lucene3x","Lucene40"})
public class TestPositionsSolrHighlighter extends SolrTestCaseJ4 {

	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig-positionshighlight.xml",
				"schema-positionshighlight.xml", getFile("highlighting/solr")
						.getAbsolutePath());

		// positionshighlighter should be used
		SolrHighlighter highlighter = HighlightComponent.getHighlighter(h
				.getCore());
		assertTrue("wrong highlighter: " + highlighter.getClass(),
				highlighter instanceof PositionsSolrHighlighter);

		assertU(adoc("text", "document one", "text2", "document one", "text3",
				"crappy document", "id", "101"));
		assertU(adoc("text", "second document", "text2", "second document",
				"text3", "crappier document", "id", "102"));
		assertU(commit());
	}
	public void testSimple() {
		assertQ("simplest test",
				req("q", "text:document", "sort", "id asc", "hl", "true"),
				"count(//lst[@name='highlighting']/*)=2",
				"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/int='0'",
				"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/int='1'");
	}

}
