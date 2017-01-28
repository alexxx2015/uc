package de.tum.in.i22.ucwebmanager.deploy;

import java.io.File;
import java.io.IOException;

public class WarPackager extends Thread {
	    	
	private String warName;
	private String webappPath;
	
	public WarPackager(String warName, String webappPath) {
		this.warName = warName;
		this.webappPath=webappPath;
	}
	
	public void run() {
		
		//If the file.war already exists the packaging fails
		File warFile = new File(webappPath+File.separator+warName);
		if (warFile.exists())
			warFile.delete();
		
		//command = jar -cvf mypackage.war -C path .
		ProcessBuilder pb = new ProcessBuilder("jar", "-cvf", webappPath+File.separator+warName, "-C", webappPath, ".");
		try {
			Process process = pb.start();
			process.waitFor();
			System.out.println("War packaged!");
		}
		catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
}
