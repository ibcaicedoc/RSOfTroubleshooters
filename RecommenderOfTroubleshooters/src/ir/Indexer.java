package ir;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
	private String repository;
	private IndexWriter writer;
	
	public Indexer(String repository) {
		setRepository(repository);
	}
	
	public Indexer() {
		
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;		
	}
	
	public int makeIndex() throws Exception {
		(new File("repositories/" + repository + "/index/")).mkdir();
		initIndexWriter("repositories/" + repository + "/index/");
		int numIndexed = 0;
		
		try {
			numIndexed = index(new TextFilesFilter());
		}
		finally {
			close();
		}
		
		return numIndexed;
	}
	
	private void initIndexWriter(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.UNLIMITED);
	}
	
	private void close() throws IOException {
		writer.close();
	}
	
	private int index(FileFilter filter)	throws Exception {
		File[] files = new File("repositories/" + repository + "/src/").listFiles();
			
		for (File f: files) 
			if (!f.isDirectory() && !f.isHidden() && f.exists() &&	f.canRead() &&	(filter == null || filter.accept(f)))
				indexFile(f);
			
			
		return writer.numDocs();
	}
	
	private static class TextFilesFilter implements FileFilter {
		public boolean accept(File path) {
			return path.getName().toLowerCase().endsWith(".java");
		}
	}
	
	protected Document getDocument(File f) throws Exception {
		Document doc = new Document();
		doc.add(new Field("contents", new FileReader(f)));
		doc.add(new Field("filename", f.getName(),
		Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("fullpath", f.getCanonicalPath(),
		Field.Store.YES, Field.Index.NOT_ANALYZED));
		return doc;
	}
	
	private void indexFile(File f) throws Exception {
		System.out.println("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f);
		writer.addDocument(doc);
	}
}
