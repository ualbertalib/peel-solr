package org.apache.solr.handler.dataimport;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.wrapAndThrow;
import static org.apache.solr.handler.dataimport.DataImporter.COLUMN;
import static org.apache.solr.handler.dataimport.XPathEntityProcessor.URL;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class PropertiesEntityProcessor extends EntityProcessorBase {

  private static final Logger LOG = LoggerFactory.getLogger(PropertiesEntityProcessor.class);
  private boolean done = false;
  @Override
  protected void firstInit(Context context) {
    done = false;
  }
  @Override
  public Map<String, Object> nextRow() {
    if(done) return null;
	Map<String, Object> row = new ConcurrentHashMap<String, Object>();
    DataSource<Reader> dataSource = context.getDataSource();
    Reader isr = dataSource.getData(context.getResolvedEntityAttribute(URL));
    Properties prop = new Properties();
    try {
      prop.load( isr );
      for (Map<String, String> field : context.getAllEntityFields()) {
        String col = field.get(COLUMN);
        String s = prop.getProperty( col );
        if (s != null && !s.isEmpty()) row.put(col, s);
      }
    } catch (IOException e) {
      wrapAndThrow(SEVERE, e, "Unable to load properties file");
    } 
    done = true;
    return row; 
  }
  
}
