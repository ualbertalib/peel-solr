package org.apache.solr.highlight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FieldTermStack;
import org.apache.solr.common.params.HighlightParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.PluginInfo;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.util.plugin.PluginInfoInitialized;

public class PositionsSolrHighlighter extends SolrHighlighter implements
		PluginInfoInitialized {

	@Override
	public void init(PluginInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public void initalize(SolrConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public NamedList<Object> doHighlighting(DocList docs, Query query,
			SolrQueryRequest req, String[] defaultFields) throws IOException {
		SolrParams params = req.getParams();
		
		// if highlighting isnt enabled, then why call doHighlighting?
		if (isHighlightingEnabled(params)) {
			FastVectorHighlighter fvh = new FastVectorHighlighter(
			        // FVH cannot process hl.usePhraseHighlighter parameter per-field basis
			        params.getBool( HighlightParams.USE_PHRASE_HIGHLIGHTER, true ),
			        // FVH cannot process hl.requireFieldMatch parameter per-field basis
			        params.getBool( HighlightParams.FIELD_MATCH, false ) );
			fvh.setPhraseLimit(params.getInt(HighlightParams.PHRASE_LIMIT, Integer.MAX_VALUE));
			    
			SolrIndexSearcher searcher = req.getSearcher();
			IndexSchema schema = searcher.getSchema();
			int[] docIDs = toDocIDs(docs);

			// fetch the unique keys
			String[] keys = getUniqueKeys(searcher, docIDs);

			// query-time parameters
			String[] fieldNames = getHighlightFields(query, req, defaultFields);
			Set<String> fset = new HashSet<String>();

			{
				// pre-fetch documents using the Searcher's doc cache
				for (String f : fieldNames) {
					fset.add(f);
				}
				// fetch unique key if one exists.
				SchemaField keyField = schema.getUniqueKeyField();
				if (null != keyField)
					fset.add(keyField.getName());
			}

			int numSnippets = params.getInt(HighlightParams.SNIPPETS, 1);

			NamedList<Object> list = new SimpleOrderedMap();

			/*
			 * TODO working in loops on creating NamedList 
			 * <lst name="id">
			 *   <arrname="field">
			 *     <int>#</int>
			 *   </arr>
			 * </lst>
			 */
			for (int docID : docIDs) {
				NamedList<Object> summary = new SimpleOrderedMap();
				for (String field : fieldNames) {
					FieldQuery fq = fvh.getFieldQuery(query,
							searcher.getIndexReader());
				    FieldTermStack stack = new FieldTermStack( req.getSearcher().getIndexReader(), docID, field, fq );
				    FieldPhraseList fpl = new FieldPhraseList( stack, fq );

					ArrayList<Integer> positions = new ArrayList<Integer>();
				    for( FieldPhraseList.WeightedPhraseInfo wpi : fpl.getPhraseList() ) {
				    	for( FieldTermStack.TermInfo ti : wpi.getTermsInfos() ) {
							positions.add(ti.getPosition());
				    	}
				    }
					summary.add(field, positions);
				}
				String printId = schema.printableUniqueKey(searcher.doc(docID,
						fset));
				list.add(printId == null ? null : printId, summary);
			}

			return list;
		} else {
			return null;
		}

	}

	/** Converts solr's DocList to the int[] docIDs */
	protected int[] toDocIDs(DocList docs) {
		int[] docIDs = new int[docs.size()];
		DocIterator iterator = docs.iterator();
		for (int i = 0; i < docIDs.length; i++) {
			if (!iterator.hasNext()) {
				throw new AssertionError();
			}
			docIDs[i] = iterator.nextDoc();
		}
		if (iterator.hasNext()) {
			throw new AssertionError();
		}
		return docIDs;
	}

	/** Retrieves the unique keys for the topdocs to key the results */
	protected String[] getUniqueKeys(SolrIndexSearcher searcher, int[] docIDs)
			throws IOException {
		IndexSchema schema = searcher.getSchema();
		SchemaField keyField = schema.getUniqueKeyField();
		if (keyField != null) {
			Set<String> selector = Collections.singleton(keyField.getName());
			String uniqueKeys[] = new String[docIDs.length];
			for (int i = 0; i < docIDs.length; i++) {
				int docid = docIDs[i];
				Document doc = searcher.doc(docid, selector);
				String id = schema.printableUniqueKey(doc);
				uniqueKeys[i] = id;
			}
			return uniqueKeys;
		} else {
			return new String[docIDs.length];
		}
	}
}