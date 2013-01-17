package org.martini.analysis;

/**
 * Copyright 2005 University of Alberta Libraries
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttributeImpl;
//import org.apache.lucene.analysis.standard.*;

import java.io.IOException;
import java.util.LinkedList;

public class MartiniFilter extends TokenFilter {

	protected MartiniFilter(TokenStream input) {
		super(input);
	}

	private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offAtt = addAttribute(OffsetAttribute.class);

	private int anchorStartOffset = 0;
	private int anchorEndOffset = 0;

	/*
	 * Olive provides full-text with alternate values for a given word in-line using #:word syntax.  For instance
	 * "L'hiver a �t� 0:ete froid � 0:a Montr�al 0:Montreal"
	 * This filter will modify the position increment and term text to position the alternatives at the same position without the #: prefix
	 * (non-Javadoc)
	 * @see org.apache.lucene.analysis.TokenStream#incrementToken()
	 */
	@Override
	public final boolean incrementToken() throws IOException {
		while( input.incrementToken() ) {
			// ASSUMPTION: colon's will only appear following an integer value (n) indicating
			// the word following the colon has a position of n relative to the previous token
			String tokenStr = termAtt.toString();
			// split around the colon
			tokenStr.replaceAll("\"", "");
			String[] compParts = tokenStr.split(":");
			if( 1 == compParts.length ) {  // if term is anchor
				anchorStartOffset = offAtt.startOffset();
				anchorEndOffset = offAtt.endOffset();
			} else { // if string separated by a colon
				posIncrAtt.setPositionIncrement( Integer.parseInt( compParts[0] ) );
				termAtt.setLength(trimTerm( termAtt.buffer(), termAtt.length() ) );
				offAtt.setOffset( anchorStartOffset, anchorEndOffset );
			}
			return true;
		}
		return false;
	}

	/* 
	 * trims #: from the term where # is the integer that indicates position offset
	 */
	private int trimTerm(char[] buffer, int length ) {
		String str = new String( buffer );
		str = str.substring( str.indexOf( ':' )+1, length );
		termAtt.copyBuffer( str.toCharArray(), 0, str.length() );
		return str.length();
	}
}