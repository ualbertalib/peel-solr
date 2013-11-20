package org.apache.solr.update.processor;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;

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

public class ForwardingProcessor extends UpdateRequestProcessor {

  @Override
  public void finish() throws IOException {
    if(!isDisabled) {
      server.shutdown();
    }
    super.finish();
  }

  private static final String TARGET_PARAM = "target";
  private static final String TARGET_NONE = "none";
  private SolrServer server;
  private boolean isDisabled = false;
  
  public ForwardingProcessor(SolrParams params, SolrQueryRequest req,
      SolrQueryResponse rsp, UpdateRequestProcessor next) {
    super(next);
    if (params != null) {
      if( params.get(TARGET_PARAM).equals(TARGET_NONE)) {
        isDisabled = true;
      } else {
        server = new HttpSolrServer( params.get(TARGET_PARAM) );
      }
    }
  }

  @Override
  public void processAdd(AddUpdateCommand cmd) throws IOException {
    if(!isDisabled) {
      try {
        server.add( cmd.getSolrInputDocument() );
      } catch (SolrServerException e) {
        throw new RuntimeException();
      }
    }
    super.processAdd(cmd);
  }

  @Override
  public void processCommit(CommitUpdateCommand cmd) throws IOException {
    if(!isDisabled) {
      try {
        server.commit();
      } catch (SolrServerException e) {
        throw new RuntimeException();
      }
    }
    super.processCommit(cmd);
  }
  
}
