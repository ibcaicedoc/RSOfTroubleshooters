package downloader;

public class IssueJSon {
	private String title;
	private String body;
	
	public IssueJSon(String title, String body) {
		setTitle(title);
		setBody(body);
	}
	
	public IssueJSon() {
		
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}	
}
