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
package org.apache.solr.handler.dataimport;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * Test for PropertiesEntityProcessor
 * </p>
 *
 *
 * @since solr 4.1
 */
@org.apache.lucene.util.LuceneTestCase.SuppressCodecs({"Lucene3x","Lucene40"})
public class TestPropertiesEntityProcessor extends AbstractDataImportHandlerTestCase {
  private String conf =
      "<dataConfig>" +
          "  <dataSource type=\"FileDataSource\"/>" +
          "  <document>" +
          "    <entity name=\"Properties\" processor=\"PropertiesEntityProcessor\" url=\"" + getFile("dihextras/solr.properties").getAbsolutePath() + "\" >" +
          "      <field column=\"key1\" name=\"key1_i\"/>" +
          "      <field column=\"key2\" name=\"key2_i\"/>" +
          "      <field column=\"key3\" name=\"key3_i\"/>" +
          "      <field column=\"fruits\" name=\"fruits__t\"/>" +
          "      <field column=\"cheeses\" name=\"cheeses_t\"/>" +
          "     </entity>" +
          "  </document>" +
          "</dataConfig>";
  
  private String[] tests = {
      "//*[@numFound='1']"
      ,"//int[@name='key1_i'][.='1']"
      ,"//int[@name='key2_i'][.='2']"
      ,"//int[@name='key3_i'][.='3']"
      ,"//str[@name='fruits__t'][.='apple, banana, pear, cantaloupe, watermelon, kiwi, mango']"
      ,"not(//str[@name='cheeses_t'])"
  };
  
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("dataimport-solrconfig.xml", "dataimport-schema-no-unique-key.xml", getFile("dihextras/solr").getAbsolutePath());
  }
  
  @Test
  public void testIndexingWithPropertiesEntityProcessor() throws Exception {
    runFullImport(conf);
    assertQ(req("*:*"), tests );
  }
}
