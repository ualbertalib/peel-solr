package ca.ualibraries.dit.martini;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.Test;

public class SolrSourceDIHConfigTest extends SolrTestCaseJ4 {

  @Test
  public void test() throws Exception {
		initCore("solrconfig.xml", "schema.xml", getFile("solr.home")
				.getAbsolutePath(), "martini");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		// request to dataimport require baseDir and mountdate as a parameter
		LocalSolrQueryRequest request = lrf.makeRequest("command",
 "full-import",
        "config", "peel-bib-solr-source-data-config-indexing.xml",
				"clean", "true",
 "commit", "true", "synchronous", "true", "indent", "true",
        "baseDir", getFile("indexing/solr-source/").getAbsolutePath(),
        "mountdate", dateFormat.format(date));
		h.query("/dataimport", request);
    assertQ(req("q", "*:*", "rows", "1"), testAll);
	}

  private static String[] testAll = {"//result[@numFound='11368']",
      "//str[@name='uid']", "//str[@name='peelnum']",
      "//arr[@name='language']", "//int[@name='pubyear']",
      "//str[@name='actyear']", "//str[@name='digstatus']",
      "//str[@name='mountDate']", "//str[@name='bibrecord']",
      "//str[@name='collection']='peelbib'", "//str[@name='title']",
      "//arr[@name='author']", "//arr[@name='subject_en']",
      "//arr[@name='subject_fr']", "//arr[@name='geodisplay_en']",
      "//arr[@name='origindisplay']", "//str[@name='digstatus']"};
}
