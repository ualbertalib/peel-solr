package ca.ualibraries.dit.martini.index;

import org.apache.solr.client.solrj.SolrServerException;

public interface MartiniIndexer {

  /**
   * Performs a query to the Solr server
   * 
   * @param baseDir
   *            the path to look for martini bib.properties and fulltext.txt
   *            files
   * @param mountDate
   *            the date that the content is mounted on the server
   * @throws SolrServerException
   */
  void start(String baseDir, String mountDate) throws SolrServerException;

  /**
   * Polls for status
   * 
   * @return true if still running else false
   * @throws SolrServerException
   */
  boolean poll() throws SolrServerException;

  /**
   * acts responsibly on objects that it's opened
   * 
   */
  void close();

  /**
   * Reports status
   * 
   * @return a String providing information about the action taken by start
   * @throws SolrServerException
   */
  String report() throws SolrServerException;
  
}
