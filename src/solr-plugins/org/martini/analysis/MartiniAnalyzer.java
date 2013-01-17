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

import java.io.Reader;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.util.Version;

/** An Analyzer that filters LetterTokenizer with LowerCaseFilter. 
 * Deals with OCR text with OliveMonographFilter.
 Based on Lucene in Action, pp. 129 ff.
*/

public class MartiniAnalyzer extends Analyzer {

	@Override
	   protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
	     Tokenizer source = new WhitespaceTokenizer(Version.LUCENE_40, reader);
	     TokenStream filter = new LowerCaseFilter(Version.LUCENE_40, source);
	     filter = new MartiniFilter(filter);
	     return new TokenStreamComponents(source, filter);
	   }

}
