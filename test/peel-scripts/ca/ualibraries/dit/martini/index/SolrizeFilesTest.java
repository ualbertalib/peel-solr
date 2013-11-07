package ca.ualibraries.dit.martini.index;

import static org.junit.Assert.*;

import org.junit.Test;

public class SolrizeFilesTest {
  
  
  @Test
  public void testPubyear() {
    String input = "2013";
    String output = "2013";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testRangeSeparatedByHyphen() {
    String input = "1908-1957";
    String output = "1908";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testRangeSeparatedBySlash() {
    String input = "1929/1930";
    String output = "1929";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testCDate() {
    String input = "c194";
    String output = "1940";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testUncertainDecade() {
    String input = "194?";
    String output = "1940";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testGarbage() {
    String input = "betw";
    String output = "";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testDateWithGarbage() {
    String input = "1660 ie 1880";
    String output = "1660";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testNumberWithGarbage() {
    String input = "ca1";
    String output = "";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testaCentury() {
    String input = "a 19";
    String output = "1900";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
  
  @Test
  public void testUncertainCentury() {
    String input = "19?";
    String output = "1900";
    assertEquals( input, output, SolrizeFiles.validateDate(input) );
  }
}
