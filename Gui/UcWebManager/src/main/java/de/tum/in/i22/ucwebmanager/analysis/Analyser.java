package de.tum.in.i22.ucwebmanager.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.Status.Status;

public class Analyser extends Thread {
	App app;
	String configFile;
	public Analyser(App app, String configFile) {
		// TODO Auto-generated constructor stub
		this.app = app;
		this.configFile = configFile;
	}
	public void run() {
//		String flowanalyser = "../../../lib/flowanalyzer-optimized.jar";
		String flowanalyser = "../../../lib/flowanalyzer.jar";
		String jonaconfig = ".." + FileUtil.Dir.JOANACONFIG.getDir() + "/" + configFile;
		String xmx = "-Xmx5G"; // maximum memory allocation pool for JVM
		String p = VaadinServlet.getCurrent().getServletContext().getInitParameter("java-xmx");
		if(!"".equals(p.trim()))
			xmx = "-Xmx"+p;
		ProcessBuilder pb = new ProcessBuilder("java", xmx, "-jar", flowanalyser, jonaconfig);
		pb.directory(new File(FileUtil.getPathCode(app.getHashCode())));
					try {
						Process process = pb.start();
						String inputstream = getInputStream(process);
						String errorStream = getErrorStream(process);
						process.waitFor();
//						System.out.println(inputstream);
						System.out.println(errorStream);
//						if (errorStream.available() != 0){
						System.out.println("Process ended!");
						// TODO send inputstream to Mainview depends on App's ID
//			 			}
//						UI.getCurrent().getNavigator().navigateTo(DashboardViewType.MAIN.getViewName() + "/" + String.valueOf(app.getId()) + "/" + inputstream);
					} catch (IOException | InterruptedException e) {
						String error = e.getMessage();
						e.printStackTrace();	
						new Notification("ERROR!",
								error,
								Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
					}
					

//				}
//			};
//		}
//			catch (Exception ex) {
//
//			}
		
	}
	
	private static String getInputStream(Process process){
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = builder.toString();
		return result;
	}
	private static String getErrorStream(Process process){
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		StringBuilder builder = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = builder.toString();
		return result;
	}
}
