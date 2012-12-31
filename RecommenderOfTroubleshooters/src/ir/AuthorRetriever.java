package ir;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class AuthorRetriever {
	private String fileName;
	
	public AuthorRetriever() {
		
	}
	
	public AuthorRetriever(String fileName) {
		setFileName(fileName);		
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<String> retrievingAuthors() throws IOException {
		Scanner scanner = new Scanner(new FileReader(fileName));
		ArrayList<String> authors = new ArrayList<String>();
		
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			int inx = line.indexOf("@author");
			
			if (inx != -1) {
				String author = line.substring(inx + 7).trim();
				
				if (!authors.contains(author))
					authors.add(author);
			}
		}			
		
		scanner.close();
		return authors;
	}
}
