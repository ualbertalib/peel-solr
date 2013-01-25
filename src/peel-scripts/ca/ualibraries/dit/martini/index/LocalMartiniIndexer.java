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

public class LocalMartiniIndexer {

	private SolrServer server;
	static Logger logger = LoggerFactory.getLogger(LocalMartiniIndexer.class);


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


	public void close() {
		server.shutdown();
	}


	public void start(String baseDir, String config, String mountDate)
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

	public boolean poll() throws SolrServerException {
		// if DIH done return true
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

	public String report() throws SolrServerException {
		ModifiableSolrParams p = new ModifiableSolrParams();
		p.add("qt", "/dataimport");
		p.add("command", "status");
		QueryResponse qr = server.query(p, METHOD.POST);
		NamedList nl = qr.getResponse();
		LinkedHashMap lhm = (LinkedHashMap) nl.get("statusMessages");
		return lhm.get("").toString();
	}
	
	public static void main(String[] args) throws InterruptedException {
		if (unknownArgs(args) || args.length < 1) {
			logger.warn("Usage: java "
							+ LocalMartiniIndexer.class.getName()
							+ " <solr.home> [coreName=<corename> contentDir=<baseDirectory> config=<data-config> mountDate=<mountdate>]");
			return;
		}
		LocalMartiniIndexer indexer;
		String coreName = getArg("coreName", args);
		indexer = new LocalMartiniIndexer(args[0], coreName);
		
		try {
			indexer.start(getArg("contentDir", args), getArg("config", args),
					getArg("mountDate", args));
			while (indexer.poll()) {
			}
			;
		} catch (SolrServerException e) {
			logger.error("Fatal error: Problem indexing");
			e.printStackTrace();
			return;
		} finally {
			indexer.close();
		}

	}


	private static String[] knownArgs = { "coreName", "contentDir", "config",
			"mountDate" };

	private static boolean unknownArgs(String[] args) {
		boolean isKnown = true;
		
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			String[] keyValue = arg.split("=");
			isKnown = false;
			for( String known : knownArgs ) {
				if (keyValue[0].equals(known))
					isKnown = true;
			}
			if (!isKnown) {
				logger.error("Unknown argument " + arg);
				return !isKnown;
			}
		}
		return false;
	}
	private static String getArg(String target, String[] args) {
		for (String arg : args) {
			String[] keyValue = arg.split("=");
			if (target.equals(keyValue[0]))
				return keyValue[1];
		}
		return null;
	}

}
