package org.martini.analysis;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.junit.Test;

public class MartiniFilterTest extends BaseTokenStreamTestCase {

	@Test
	public void testZeroOccurances() throws IOException {
	StringReader reader = new StringReader("Montréal Montreal");
	    MartiniFilter filter = new MartiniFilter(new WhitespaceTokenizer(TEST_VERSION_CURRENT, reader));
	assertTokenStreamContents(filter,
		new String[] { "Montréal", "Montreal" }, new int[] { 1, 1 });
	}
	@Test
	public void testExactlyOnce() throws IOException {
	StringReader reader = new StringReader("Montréal 0:Montreal");
	    TokenStream stream = new MartiniFilter(new WhitespaceTokenizer(TEST_VERSION_CURRENT, reader));
	assertTokenStreamContents(stream,
		new String[] { "Montréal", "Montreal" }, new int[] { 1, 0 });
	}
	@Test
	public void testMiddleMultiple() throws IOException {
	StringReader reader = new StringReader(
		"Je n'aime pas l'hiver à 0:a Montréal 0:Montreal 0:mount 0:réal, jamais!");
	    TokenStream stream = new MartiniFilter(new WhitespaceTokenizer(TEST_VERSION_CURRENT, reader));
	    assertTokenStreamContents(stream, 
 new String[] { "Je", "n'aime", "pas",
		"l'hiver", "à", "a", "Montréal", "Montreal", "mount", "réal,",
		"jamais!" },
	    		new int[]{1,1,1,1,1,0,1,0,0,0,1});
	}
}
