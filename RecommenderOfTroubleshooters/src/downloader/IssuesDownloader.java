package downloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class IssuesDownloader {
	private String owner;
	private String repository;
	
	public IssuesDownloader(String owner, String repository) {
		setOwner(owner);
		setRepository(repository);
	}
	
	public IssuesDownloader() {
		
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
	
	public ArrayList<IssueJSon> download() throws MalformedURLException, IOException {
		URL url = new URL(String.format("https://api.github.com/repos/%s/%s/issues", owner, repository));
		ArrayList<IssueJSon> issues = new ArrayList<IssueJSon>();
		InputStream input = url.openStream();
		int ch;
		String response = "";
		
		while ((ch = input.read()) != -1) 
			response += (char)ch;
				
		input.close();
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(response).getAsJsonArray();
		Gson gson = new Gson();
		
		for (int i = 0; i < array.size(); i++) 
			issues.add(gson.fromJson(array.get(i), IssueJSon.class));
		
		return issues;
	}
}