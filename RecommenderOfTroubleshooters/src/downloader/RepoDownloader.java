package downloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RepoDownloader {
	private String owner;
	private String repository;
	
	public RepoDownloader() {
		
	}
	
	public RepoDownloader(String owner, String repository) {
		setOwner(owner);
		setRepository(repository);
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

	public void download() throws IOException {
		URL url = new URL("https://github.com/"+ owner + "/" + repository + "/archive/master.zip");
		InputStream input = url.openStream();
		int byteRead = 0;
		(new File("repositories/")).mkdir();
		(new File("repositories/" + repository + "/")).mkdir();		
		BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream("repositories/" + repository + "/" + repository + "-master.zip"));
		int size = 1024;
		byte[] buf = new byte[size];
								
		while ((byteRead = input.read(buf)) != -1)
			outStream.write(buf, 0, byteRead);
		
		input.close();
		outStream.close();
		unzip();		
	}

	private void unzip() throws IOException {
		byte[] buf = new byte[1024];    	
		String outputFolder = "repositories/" + repository + "/src/";//" + repository + "-master/";
     	ZipInputStream zis = new ZipInputStream(new FileInputStream("repositories/" + repository + "/" + repository + "-master.zip"));
    	ZipEntry ze = zis.getNextEntry();
    	(new File(outputFolder)).mkdir();
 
    	while(ze!=null) { 
    		String fileName = ze.getName();

    		if (fileName.endsWith(".java")) {
    			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputFolder + new File(fileName).getName()));    		
    			int byteRead;
    		
    			while ((byteRead = zis.read(buf)) > 0) 
    				outStream.write(buf, 0, byteRead);
         
    			outStream.close();    			
    		}
    		
    		ze = zis.getNextEntry();
    	}
 
        zis.closeEntry();
    	zis.close(); 
    }
}