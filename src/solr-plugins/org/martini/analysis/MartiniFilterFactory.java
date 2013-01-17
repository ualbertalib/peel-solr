package org.martini.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class MartiniFilterFactory extends TokenFilterFactory {

	@Override
	public TokenStream create(TokenStream input) {
		return new MartiniFilter( input );
	}

}
