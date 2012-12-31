package recommenderoftroubleshooters.views;


import ir.AuthorRetriever;
import ir.FindRelevantFiles;
import ir.Indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import downloader.IssueJSon;
import downloader.IssuesDownloader;
import downloader.RepoDownloader;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class RecommenderOfTroubleshootersView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "recommenderoftroubleshooters.views.RecommenderOfTroubleshootersView";

	private Text txfRepo;
	private Text txfOwner;	
	private Label lbRepo;
	private Label lbOwner;	
	private Combo comboIssue;
	private Button btnSearchIssues;
	private Button btnSearch;	
	private List resultList;
	private ArrayList<IssueJSon> issues;
	private String repository;
	//private TableViewer viewer;
	//private FormToolkit form;
	
	
	public RecommenderOfTroubleshootersView() {
		issues = new ArrayList<IssueJSon>(); 
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout(4, true);		
		parent.setLayout(gridLayout);
		lbRepo = new Label(parent, SWT.SEARCH);
		lbRepo.setText("Repository");
		txfRepo = new Text(parent, SWT.SEARCH);
		lbOwner = new Label(parent, SWT.SEARCH);
		lbOwner.setText("Owner");		
		txfOwner = new Text(parent, SWT.SEARCH);		
		btnSearchIssues = new Button(parent, SWT.PUSH);
		btnSearchIssues.setText("Download issues");
		
		btnSearchIssues.addMouseListener( new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				searchIssues();
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
					
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
			}
		});
		
		comboIssue = new Combo(parent, SWT.READ_ONLY);
		/*comboIssue.add("one");
		comboIssue.add("two");
		comboIssue.add("three");
		comboIssue.select(0);*/
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		
		btnSearch = new Button(parent, SWT.PUSH);
		btnSearch.setEnabled(false);
		btnSearch.setText("Suggest me troubleshooters");
		
		btnSearch.addMouseListener( new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				suggest();
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
					
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
			}
		});
		
		resultList = new List(parent, SWT.SINGLE | SWT.V_SCROLL);
		
		resultList.setLayoutData(gridData);
		/*resultList.add("1 - Pablito");
		resultList.add("2 - Sutanito");
		resultList.add("3 - Menganito");
		resultList.add("4 - Motarito");*/
	}

	@Override
	public void setFocus() {
		txfRepo.setFocus();		
	}	
	
	protected void searchIssues() {
		if (txfRepo.getText().isEmpty()) {
			showMessage("You must enter a repository name");
			txfRepo.setFocus();
			return;
		}
		
		if (txfOwner.getText().isEmpty()) {
			showMessage("You must enter an owner");
			txfOwner.setFocus();
			return;
		}
		
		try {
			String owner = txfOwner.getText(); 
			repository = txfRepo.getText();
			RepoDownloader repoDownloader  = new RepoDownloader(owner, repository);
			repoDownloader.download();
			Indexer indexer = new Indexer(repository);
			int numIndexedFiles = indexer.makeIndex();
			System.out.println("Number of indexed files " + numIndexedFiles);
			IssuesDownloader issuesDownloader = new IssuesDownloader(owner, repository);
			issues = issuesDownloader.download();
			comboIssue.removeAll();
			txfOwner.setText("");
			txfRepo.setText("");
			
			if (issues != null)
				if (issues.size() > 0) {
					for (IssueJSon issue : issues) 
						comboIssue.add(issue.getTitle());
					
					comboIssue.select(0);
					btnSearch.setEnabled(true);
				}
			
			
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}		
	}
	
	private void suggest() {
		FindRelevantFiles finder = new FindRelevantFiles(repository, makeQuery(comboIssue.getSelectionIndex()));
		ArrayList<String> files = new ArrayList<String>();

		try {
			files = finder.retrieveFiles();
		} catch(Exception e) {
			e.printStackTrace(System.err);
		}

		Hashtable<String, Integer> authors = new Hashtable<String, Integer>();

		for (String f : files) {
			AuthorRetriever authorRetriever = new AuthorRetriever(f);			

			try {
				ArrayList<String> retrievedAuthors = authorRetriever.retrievingAuthors();
				
				for (String a : retrievedAuthors)
					if (!authors.containsKey(a))
						authors.put(a, 1);
					else
						authors.put(a, authors.get(a) + 1);
						
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
			
			resultList.removeAll();
			
			while (authors.size() > 0) {
				String author = greaterRating(authors);
				authors.remove(author);
				resultList.add(author);
			}
		}	
	}
	
	private String greaterRating(Hashtable<String, Integer> authors) {
		String a = "";
		Enumeration<String> keys = authors.keys();
		
		if (keys != null)
			if (keys.hasMoreElements())
				a = authors.keys().nextElement();
		
		while (keys.hasMoreElements()) {
			String k = keys.nextElement();
			
			if (authors.get(k) > authors.get(a))
				a = k;
		}
			 
		
		return a;
	}
	
	private String makeQuery(int index) {
		IssueJSon issue = issues.get(index);
		String query = issue.getTitle() + " " + issue.getBody();
		query = query.replace("(", "");
		query = query.replace(")", "");
		query = query.replace(":", "");
		query = query.replace("+", "");
		query = query.replace("^", "");
		query = query.replace("\"", "");
		query = query.replace("?", "");
		query = query.replace("/", "");
		query = query.replace("\\", "");
		query = query.replace("-", "");
		query = query.replace("*", "");
		query = query.replace("[", "");
		query = query.replace("]", "");
		query = query.replace("{", "");
		query = query.replace("}", "");
		return query;
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(txfOwner.getShell(),
			"Recommender of troubleshooters",
			message);
	}
}

/*			
for (IssueJSon issue : issues) {
	String query = issue.getTitle() + " " + issue.getBody();
	query = query.replace("(", "");
	query = query.replace(")", "");
	query = query.replace(":", "");
	query = query.replace("+", "");
	query = query.replace("^", "");
	query = query.replace("\"", "");
	query = query.replace("?", "");
	query = query.replace("/", "");
	query = query.replace("\\", "");
	query = query.replace("-", "");
	query = query.replace("*", "");
	query = query.replace("[", "");
	query = query.replace("]", "");
	query = query.replace("{", "");
	query = query.replace("}", "");
	FindRelevantFiles finder = new FindRelevantFiles(repository, query);
	ArrayList<String> files = new ArrayList<String>();
	
	try {
		files = finder.retrieveFiles();
	} catch(Exception e) {
		e.printStackTrace(System.err);
	}
	
	System.out.println("--------------------------------------");
	System.out.println(issue.getTitle());
	System.out.println("Files");
	
	for (String f : files) 	{				
		System.out.println(f);
		System.out.println("========Authors=======");
		AuthorRetriever authorRetriever = new AuthorRetriever(f);
		
		try {
			for (String author : authorRetriever.retrievingAuthors())
				System.out.println(author);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
}
/mkelandis/ReadingLog
*
*
*/