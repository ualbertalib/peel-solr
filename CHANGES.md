Peel-Solr 0.2
===============

features
* added DataImportHandler Request Handlers for each ingestion type (peelbib, newspapers).  Simpler URL, 
provides defaults which can be overridden. 
* created ScriptTransformer for pubyears that show up as edge cases (c192, 1920-1930, etc)
* created ScriptTransformer to assign mountDate when itemstatus.properties does not give it

bug fixes
* relaxed default performance limits
* changed language to string field type as hypen is important to some language codes (de-low)
* changed oaiset to multivalued

Peel-Solr 0.1
===============

MartiniFilter
-------------
Is a [Filter, FilterFactory and Analyzer](http://wiki.apache.org/solr/AnalyzersTokenizersTokenFilters) 
for use with [Lucene or Solr](http:lucene.apache.org).  This accepts text in 
the "martini" format where a string like "Montréal 0:Montreal" of the form term 
positionOffset:alternateTerm denotes Montreal should be considered an alternate 
term at the same position as Montréal. There may be zero to many alternate terms 
at a given position. 

Also used in Peel to create distance between text that should
not be considered together as a phrase or otherwise.  For instance, distinct 
author names like "Smith, John 100:Doe, Jane" 

PositionHighlighter
-------------------
As an alternative to returning snippets, this highlighter provides the (term) 
position for query matches. One use-case for this is to reconcile the term position 
from the Solr index with 'word' coordinates provided by an OCR process. In this 
way we are able to 'highlight' an image, like a page from a book or an article 
from a newspaper, in the locations that match the user's query.

This is based on the FastVectorHighlighter and requires that termVectors, termOffsets 
and termPositions be stored.  

[See SOLR-4722](https://issues.apache.org/jira/browse/SOLR-4722)

PropertiesEntityProcessor
-------------------------
A simple [EntityProcessor](http://wiki.apache.org/solr/DataImportHandler#EntityProcessor) 
which can read from any DataSource<Reader> and output rows corresponding to the 
properties file key/value pairs.

Meant to simplify the data import config which reads Peel bib.properties files.

[See SOLR-3928](https://issues.apache.org/jira/browse/SOLR-3928)

DIH configs
-----------
## peel-bib-data-config-indexing.xml

Maps expected peelbib index fields from bib.properties files and content from 
fulltext.txt to Solr schema fields when invoked http://solr-url/core/data-import?

Expects content of the form found test-files/indexing/peelbib.

### parameters:
baseDir -- the root directory of the content that you want to index.  Will be 
  traversed recursively for bib.properties files.
mountdate -- the date that the content to be indexed was mounted
config=peel-bib-data-config-indexing.xml -- must be explicitly specified, 
  newspapers is default
commit -- {true,false} should the commit command be sent when finished?
clean -- {true,false} should the current index be destroyed before indexing?
optimize -- {true,false} should the optimize command be sent when finished?
command -- {full-import,delta-import,status,reload-config,abort} common use 
  is full-import with the above parameters followed by status

## peel-newspapers-data-config-indexing.xml

Maps expected peelbib index fields from bib.properties files and content from 
fulltext.txt to Solr schema fields when invoked http://solr-url/core/data-import?

Expects content of the form found test-files/indexing/newspapers.

### parameters:
baseDir -- the root directory of the content that you want to index.  Will be 
  traversed recursively for bib.properties files.
mountdate -- the date that the content to be indexed was mounted
config=peel-bib-data-config-indexing.xml -- optional,  newspapers is default
commit -- {true,false} should the commit command be sent when finished?
clean -- {true,false} should the current index be destroyed before indexing?
optimize -- {true,false} should the optimize command be sent when finished?
command -- {full-import,delta-import,status,reload-config,abort} common use 
  is full-import with the above parameters followed by status
  
## peel-bib-solr-source-data-config-indexing.xml

Maps export of wisecat stored content to Solr schema fields when invoked 
http://solr-url/core/data-import?

Expects content of the form found test-files/indexing/solr-source

## peel-newspapers-solr-source-data-config-indexing.xml

Maps export of wisecat stored content to Solr schema fields when invoked 
http://solr-url/core/data-import?

Expects content of the form found test-files/indexing/solr-source/newspapers
