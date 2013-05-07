 #!/bin/bash
 baseurl=http://wisecat.library.ualberta.ca:8080/solr-newspapers/select?q=collection:newspapers
 rows=5000
 start=0
 docs=8825495
 
 while [ $start -lt $docs ]; do
    destFile="test-files/newspapers$start.xml"
 	url="$baseurl&rows=$rows&start=$start"
 
 	wget -O $destFile "$url"
 	$((start+=$rows))
 done