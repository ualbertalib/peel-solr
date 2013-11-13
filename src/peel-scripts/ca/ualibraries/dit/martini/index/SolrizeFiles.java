package ca.ualibraries.dit.martini.index;

import static org.apache.solr.handler.dataimport.DataImporter.COLUMN;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.response.LukeResponse.FieldInfo;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.luke.FieldFlag;

public class SolrizeFiles extends SimpleFileVisitor<Path> {
  
  private Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
  private SolrInputDocument doc = null;
  static char dirSep = System.getProperty("file.separator").charAt(0);
  Map<String,FieldInfo> fields;
  String mountDate;
  
  public SolrizeFiles(Map<String,FieldInfo> fieldInfo, String mountDate) {
    fields = fieldInfo;
    this.mountDate = mountDate;
  }
  
  public Collection<SolrInputDocument> getDocs() {
    return docs;
  }
  
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
      throws IOException {
    doc = new SolrInputDocument();
    return super.preVisitDirectory(dir, attrs);
  }
  
  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc)
      throws IOException {
    if( null != doc && doc.containsKey("uid") ) {
      if( !doc.containsKey( "mountDate") && null != mountDate) {
        doc.addField("mountDate", mountDate);
      }
      docs.add( doc );
    }
    return super.postVisitDirectory(dir, exc);
  }
  
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
    if( file.toString().endsWith("fulltext.txt")) {
      String fulltext = FileUtils.readFileToString( file.toFile() );
      doc.addField( "content", fulltext);
      doc.addField( "uid", uid(file.toFile()));
      doc.addField( "uri", file.toAbsolutePath());
    }
    if( file.toString().endsWith(".properties")) {
      doProperties(file);
    } 
    if( file.toString().endsWith("bib.properties")) {
      doc.addField( "modified", file.toFile().lastModified() );
    }
    return FileVisitResult.CONTINUE;
  }

  private void doProperties(Path file) throws IOException {
    Properties prop = new Properties();
    prop.load( Files.newInputStream(file) );
    for ( Map.Entry entry: prop.entrySet() ) {
      String key = mapKey((String) entry.getKey());
      String[] values;
      if( isMultiValued( key ) ) {
        values = ((String) entry.getValue()).split("|");
      } else {
        values = new String[1];
        values[0] = (String) entry.getValue();
      }
      if( fields.containsKey( key ) && notRepeated(key) ) {
        if( "pubyear".equals( key ) ) {
          values[0] = validateDate( values[0] );
        }
        if( !"".equals(values[0]) ) {
          for( String value : values ) {
            doc.addField( key, value );
          }
        }
      }
    }
  }
  
  private String mapKey(String key) {
    if( null == fields.get(key) )
      key = null;
    if( "mountdate".equals(key))
      key = "mountDate";
    return key;
  }

  private boolean notRepeated(String key) {
    if( isMultiValued(key) )
      return true;
    else if( doc.containsKey(key) )
      return false;
    return true;
  }

  private boolean isMultiValued(String key) {
    FieldInfo info = fields.get(key);
    if( null == info )
      return false;
    return info.getFlags().contains(FieldFlag.MULTI_VALUED);
  }

  public static String validateDate(String input) {
    String pubyear = "";
    if(input.matches("[0-9]{4}([-/][0-9]{4})*") ) {
      pubyear = input.substring(0, 4);
    } else if(input.matches("a? ?[0-9]{2}[?-]?")) {
      pubyear = input.replaceAll("a? ?([0-9]{2})[?-]?", "$100");
    } else if (input.matches("c?[0-9]{3}[?-]?") ) {
      pubyear = input.replaceAll("c?([0-9]{3})[?-]?", "$10");
    } else if( input.matches( ".*([0-9]{4}).+" ) ) {
      pubyear = input.replaceAll(".*([0-9]{4}).+", "$1");
    }
    
    return pubyear;
  }
  
  /**
   * Each file has a unique id consisting of its full path and filename
   * with _ replacing the directory separators
   * NOTE: everything before the word "indexing/" will be dropped
   * e.g. c:\development\indexing\peelbib\1132\fulltext.txt becomes
   * peelbib\1132\fulltext.txt
   * (to allow indexes to be maintained on different machines)
   * @param f a file for which to make a uid
   * @return the uid for the given file
   */
  public static String uid(File f) { 
    String s = f.getPath().replace(dirSep, '_');
    int i = s.indexOf("_indexing_");
    return s.substring(i + 10);
  }
}
