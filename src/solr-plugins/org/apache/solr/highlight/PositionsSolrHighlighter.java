package org.apache.solr.highlight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

/**
 * <p>
 * Example configuration:
 * 
 * <pre class="prettyprint">
 *   &lt;searchComponent class="solr.HighlightComponent" name="highlight"&gt;
 *     &lt;highlighting class="org.apache.solr.highlight.PositionsSolrHighlighter"/&gt;
 *   &lt;/searchComponent&gt;
 * </pre>
 * <p>
 * Notes:
 * <ul>
 * <li>fields to highlight must be configured with termVectors="true"
 * termPositions="true" termOffsets="true"
 * <li>hl.q (string) can specify the query
 * <li>hl.fl (string) specifies the field list.
 * </ul>
 * 
 * @lucene.experimental
 */
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

	/**
	 * Generates a list of Highlighted query term position(s) for each item in a
	 * list of documents, or returns null if highlighting is disabled.
	 * 
	 * @param docs query results
	 * @param query the query
	 * @param req the current request
	 * @param defaultFields default list of fields to summarize
	 * 
	 * @return NamedList containing a NamedList for each document, which in
	 *         turns contains sets (field, positions) pairs.
	 */
	@Override
	public NamedList<Object> doHighlighting(DocList docs, Query query,
			SolrQueryRequest req, String[] defaultFields) throws IOException {
		SolrParams params = req.getParams();
		
		// if highlighting isnt enabled, then why call doHighlighting?
		if (isHighlightingEnabled(params)) {
			FastVectorHighlighter fvh = new FastVectorHighlighter(
			        // FVH cannot process hl.usePhraseHighlighter parameter per-field basis
					params.getBool(HighlightParams.USE_PHRASE_HIGHLIGHTER, true),
			        // FVH cannot process hl.requireFieldMatch parameter per-field basis
					params.getBool(HighlightParams.FIELD_MATCH, false));
			fvh.setPhraseLimit(params.getInt(HighlightParams.PHRASE_LIMIT, Integer.MAX_VALUE));
			    
			SolrIndexSearcher searcher = req.getSearcher();
			IndexSchema schema = searcher.getSchema();
			int[] docIDs = toDocIDs(docs);

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
				if (null != keyField) {
					fset.add(keyField.getName());
				}
			}

			NamedList<Object> list = new SimpleOrderedMap<Object>();

			for (int docID : docIDs) {
				NamedList<Object> summary = new SimpleOrderedMap<Object>();
				for (String field : fieldNames) {
					FieldQuery fq = fvh.getFieldQuery(query,
							searcher.getIndexReader());
					FieldTermStack stack = new FieldTermStack(req.getSearcher()
							.getIndexReader(), docID, field, fq);
					FieldPhraseList fpl = new FieldPhraseList(stack, fq);

					ArrayList<Integer> positions = new ArrayList<Integer>();
					for (FieldPhraseList.WeightedPhraseInfo wpi : fpl
							.getPhraseList()) {
						for (FieldTermStack.TermInfo ti : wpi.getTermsInfos()) {
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

}