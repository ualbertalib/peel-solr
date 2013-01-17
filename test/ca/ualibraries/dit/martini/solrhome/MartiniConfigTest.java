package ca.ualibraries.dit.martini.solrhome;

import org.apache.solr.SolrTestCaseJ4;
import org.junit.BeforeClass;
import org.junit.Test;

public class MartiniConfigTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void beforeClass() throws Exception {
	initCore("solrconfig.xml", "schema.xml", getFile("solr.home")
		.getAbsolutePath(), "martini");
    }

    @Test
    public void testInitOK() throws Exception {
	String response = h.query("/admin/ping", req());
	assertEquals("status should be OK", null,
		h.validateXPath(response, "//str[@name='status']='OK'"));
    }

}
