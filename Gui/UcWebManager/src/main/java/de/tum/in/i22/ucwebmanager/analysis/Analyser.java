package de.tum.in.i22.ucwebmanager.analysis;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.Status.Status;

public class Analyser {
	public static void StaticAnalyser(App app, String configFile){
		ProcessBuilder pb = new ProcessBuilder("jav", "-jar", "-classpath",
						".:"+FileUtil.getRelativePathCode(app.getHashCode())+
						":../../../flowanalyzer",
						"-jar flowanalyzer.jar",configFile);
		pb.directory(new File(FileUtil.getPathOutput(app.getHashCode())));
		try {
			Process process = pb.start();
			//app.setStatus(Status.STATISTIC.getStage());
			//AppDAO.updateStatus(app);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
