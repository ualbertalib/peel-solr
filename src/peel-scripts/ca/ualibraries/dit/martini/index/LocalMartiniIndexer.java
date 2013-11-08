package ca.ualibraries.dit.martini.index;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.CoreContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalMartiniIndexer extends Indexer implements MartiniIndexer {

	private SolrServer server;
	private String config;
	static Logger logger = LoggerFactory.getLogger(LocalMartiniIndexer.class);
	
	/**
	 * Initializes and starts an embedded Solr server (no servlet container --
	 * like Tomcat -- required)
	 * 
	 * @param solrHome
	 *            the solr.home directory as defined by Solr
	 * @param core
	 *            the name of the Solr core to use. The default defined in
	 *            solr.xml is used if null.
	 */
	public LocalMartiniIndexer(String solrHome, String core) {
		System.setProperty("solr.solr.home", solrHome);
		CoreContainer.Initializer initializer = new CoreContainer.Initializer();
		CoreContainer coreContainer = initializer.initialize();
		coreContainer.load(solrHome, new File(new File(solrHome), "solr.xml"));
		if (null == core) {
			core = coreContainer.getDefaultCoreName();
		}
		server = new EmbeddedSolrServer(coreContainer, core);
	}

	/**
  * @param config
  *            the Solr data-config.xml file which defines the import
  */
	public void setConfig(String config) {
	  this.config = config;
	}

	/**
	 * Shutdown the Solr server
	 * 
	 */
	public void close() {
		server.shutdown();
	}


	/**
	 * Performs a full-import dataimport query to the Solr server
	 * 
	 * @param baseDir
	 *            the path to look for martini bib.properties and fulltext.txt
	 *            files
	 * @param mountDate
	 *            the date that the content is mounted on the server
	 * @throws SolrServerException
	 */
	public void start(String baseDir, String mountDate)
			throws SolrServerException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		ModifiableSolrParams p = new ModifiableSolrParams();
		p.add("qt", "/dataimport");
		p.add("command", "full-import");
		p.add("baseDir", baseDir );
		p.add("config", config );
		if (null == mountDate) {
			p.add("mountdate", dateFormat.format(date));
		} else
			p.add("mountdate", mountDate);
		server.query(p, METHOD.POST);
	}

	/**
	 * Polls the dataimport handler for status
	 * 
	 * @return true if data import is still running else false
	 * @throws SolrServerException
	 */
	public boolean poll() throws SolrServerException {
		ModifiableSolrParams p = new ModifiableSolrParams();
		p.add("qt", "/dataimport");
		p.add("command", "status");
		QueryResponse qr = server.query(p, METHOD.POST);
		NamedList nl = qr.getResponse();
		String status = (String) nl.get("status");

		if ("busy".equals(status))
			return true;
		// else
		return false;
	}

	/**
	 * Reports the status of the dataimport handler (poll must return false)
	 * 
	 * @return the most recent status message from the dataimport handler
	 * @throws SolrServerException 
	 */
	public String report() throws SolrServerException {
		ModifiableSolrParams p = new ModifiableSolrParams();
		p.add("qt", "/dataimport");
		p.add("command", "status");
		QueryResponse qr = server.query(p, METHOD.POST);
		NamedList nl = qr.getResponse();
		LinkedHashMap lhm = (LinkedHashMap) nl.get("statusMessages");
		return lhm.get("").toString();
	}

}
