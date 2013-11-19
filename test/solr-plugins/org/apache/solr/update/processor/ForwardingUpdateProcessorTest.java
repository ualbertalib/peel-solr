/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.update.processor;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.JettySolrRunner;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.apache.lucene.util.LuceneTestCase.SuppressCodecs({"Lucene3x","Lucene40"})
public class ForwardingUpdateProcessorTest extends UpdateProcessorTestBase {

  private static Logger LOG = LoggerFactory.getLogger(ForwardingUpdateProcessorTest.class);
  
  private static final String SOLR_CONFIG = "solrconfig.xml";
  private static final String SOLR_SCHEMA = "schema.xml";
    
  File solrHomeDirectory;
  private JettySolrRunner jetty;
  
  UpdateRequestProcessorChain pc;
  
  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    // origin solr core
    initCore(SOLR_CONFIG, SOLR_SCHEMA, getFile("upload/solr")
        .getAbsolutePath());
    
    // target solr instance
    solrHomeDirectory = new File(TEMP_DIR, "ForwardingUpdateProcessorTest_");
    if (solrHomeDirectory.exists()) {
      FileUtils.deleteDirectory(solrHomeDirectory);
    }
    FileUtils.copyDirectory( getFile("upload/solr"), solrHomeDirectory);
    
    jetty = new JettySolrRunner(solrHomeDirectory.getAbsolutePath(), "/solr", 0);
    jetty.start();
  }
  
  @Override
  @After
  public void tearDown() throws Exception {
    try {
      deleteCore();
    } catch (Exception e) {
      LOG.error("Error deleting core", e);
    }
    if (jetty != null) jetty.stop();
    FileUtils.deleteDirectory(solrHomeDirectory);
    super.tearDown();
  }

  public void testForwarding() throws Exception {
    ForwardingUpdateProcessorFactory f = new ForwardingUpdateProcessorFactory();
    NamedList<String> initArgs = new NamedList<String>();
    initArgs.add("target", "http://127.0.0.1:" + jetty.getLocalPort() + "/solr");
    f.init(initArgs);
    
    UpdateRequestProcessorFactory[] def = new UpdateRequestProcessorFactory[]{
        f
    };
    
    pc = new UpdateRequestProcessorChain( def, h.getCore() );
    
    SolrServer server = new HttpSolrServer( "http://127.0.0.1:" + jetty.getLocalPort() + "/solr");
    QueryResponse resp = server.query(new SolrQuery("*:*"));
    assertEquals( 0, resp.getResults().size() );
    
    // update doc
    SolrInputDocument d  = processAdd("forwarding",
                   doc(f("id", "1111")));
    processCommit( "forwarding");
    
    // check that it reached the target
    resp = server.query(new SolrQuery("*:*"));
    assertEquals( 1, resp.getResults().size() );
    
    server.shutdown();
    
  }
  
  public void testForwardingFail() throws Exception {
    ForwardingUpdateProcessorFactory f = new ForwardingUpdateProcessorFactory();
    NamedList<String> initArgs = new NamedList<String>();
    initArgs.add("target", "http://127.0.0.1/?core=xxx");
    f.init(initArgs);
    
    UpdateRequestProcessorFactory[] def = new UpdateRequestProcessorFactory[]{
        f
    };
    
    pc = new UpdateRequestProcessorChain( def, h.getCore() );
    
    // update doc
    boolean exception_ok = false;
    try {
    SolrInputDocument d  = processAdd("forwarding",
                   doc(f("id", "1111")));
    processCommit( "forwarding");
    } catch ( Exception e ) {
      exception_ok = true;
    }
    
    assertTrue( "Should have gotten an exception", exception_ok );
    
  }
  
  /**
   * Runs a document through the specified chain, and returns the final
   * document used when the chain is completed (NOTE: some chains may
   * modify the document in place
   */
  @Override
  protected SolrInputDocument processAdd(final String chain,
                                         final SolrParams requestParams,
                                         final SolrInputDocument docIn)
    throws IOException {

    SolrCore core = h.getCore();
    
    SolrQueryResponse rsp = new SolrQueryResponse();

    SolrQueryRequest req = new LocalSolrQueryRequest(core, requestParams);
    try {
      AddUpdateCommand cmd = new AddUpdateCommand(req);
      cmd.solrDoc = docIn;

      UpdateRequestProcessor processor = pc.createProcessor(req, rsp);
      processor.processAdd(cmd);

      return cmd.solrDoc;
    } finally {
      req.close();
    }
  }

  @Override
  protected void processCommit(final String chain) throws IOException {
    SolrCore core = h.getCore();
    
    SolrQueryResponse rsp = new SolrQueryResponse();

    SolrQueryRequest req = new LocalSolrQueryRequest(core, new ModifiableSolrParams());

    CommitUpdateCommand cmd = new CommitUpdateCommand(req,false);
    UpdateRequestProcessor processor = pc.createProcessor(req, rsp);
    try {
      processor.processCommit(cmd);
    } finally {
      req.close();
    }
  }

  @Override
  protected SolrInputDocument processAdd(String chain, SolrInputDocument docIn)
      throws IOException {
    return super.processAdd(chain, docIn);
  }

}
