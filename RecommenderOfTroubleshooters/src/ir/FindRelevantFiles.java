package ir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
/*import java.util.List;
import pitt.search.semanticvectors.Search;
import pitt.search.semanticvectors.SearchResult;*/

public class FindRelevantFiles {
	private String query;
	private String repository;
	
	public FindRelevantFiles(String repo, String query) {
		setQuery(query);
		setRepository(repo);
	}
	
	public FindRelevantFiles() {
		
	}
	
/*	public ArrayList<String> retrieveFiles() {
		ArrayList<String> files = new ArrayList<String>();
		String[] qs = query.split("\\s");
		String[] arguments = new String[4 + qs.length];
		int i = 4;		
		arguments[0] = "-luceneindexpath";
		arguments[1] = "repositories/" + repository + "/index/";
		arguments[2] = "-searchtype"; 
		arguments[3] = "sum";
		
		for (String s : qs)
			arguments[i++] = s;
		
		List<SearchResult> results = Search.RunSearch(arguments, 10);
		
		for (SearchResult result: results) {
	        //System.out.println(result.getScore() + ":" + ((ObjectVector)result.getObjectVector()).getObject().toString());
			System.out.println(result.getScore() + ":" + result.getObjectVector().getObject().toString());
			files.add(result.getObjectVector().getObject().toString());
		}
		
		return files;
	}*/
	
	public ArrayList<String> retrieveFiles()  throws IOException, ParseException {
		ArrayList<String> files = new ArrayList<String>();
		Directory dir = FSDirectory.open(new File("repositories/" + repository + "/index/"));
		IndexSearcher is = new IndexSearcher(dir);
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,	"contents",	new StandardAnalyzer(Version.LUCENE_CURRENT));
		Query queryObject = parser.parse(query);
		TopDocs hits = is.search(queryObject, 10);
		
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			files.add(doc.get("fullpath"));
		}
		
		is.close();
		return files;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
}
