This Eclipse plug-in is a recommender system which suggests who authors can
fix certain issue from a softare repository stored inside Github.

The source code is organized in the following package:

+ downloader: This package contains the classes IssueJSon.java,
  IssueDownloader, and RepoDownloader.  The two first classes was
  implemented in order to download issues by using JSon.  
  The last class was implemented to download repositories.
+ ir: The classes stored in this package are Indexer.java, FindRelevantFiles.java, and
  AuthorRetriever.java. The first class makes a index from java files
  of a repository. The second class uses the title and the body of an
  issue to make a query in order to find relevant files. And the last class
  retrieves the author info from comments tagged by @author in the files
  retrieved for the prior class.
+ recommenderoftroubleshooters: Contains the class Activator.java
+ recommenderoftroubleshooters.view: This package contains the class
  RecommenderOfTroubleshootersView, which manages the GUI of the plug-in.
  
The plug-in is deployed in the Eclipse view folder, "Sample category", and the
its name is "Recommender of Troubleshooters".

When the plug-in is running, you must enter the repository's name and owner
in the text field labeled as: repository and owner, respectively. Thereafter,
you must press the button labeled: Download issues. Issues shall be
displayed in the combo box next to the last button. When it happens, you
must press select a issue, and press the button labeled: Suggest me troubleshooters.
Finally, in the bottom of the view shall be displayed a list of authors
who can fix the selected issue. Authors are organized in accord with their
relevance.