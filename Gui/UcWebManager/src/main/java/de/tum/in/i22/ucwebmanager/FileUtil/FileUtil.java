package de.tum.in.i22.ucwebmanager.FileUtil;

import java.io.File;
import java.io.IOException;

import com.vaadin.server.VaadinService;

public class FileUtil {
	public enum Dir{
		SOURCEANDSINKS		(VaadinService.getCurrent().getBaseDirectory().getPath() + File.separator + "sourceandsinks" + File.separator),
		BLACKANDWHITELIST	(VaadinService.getCurrent().getBaseDirectory().getPath() + File.separator + "whiteAndBlackList"),
		APPS				(VaadinService.getCurrent().getBaseDirectory().getPath() + File.separator + "apps"),
		CODE				(File.separator + "code"),
		JOANACONFIG			(File.separator + "joana-config"),
		JOANAOUTPUT 		(File.separator + "joana-output"),
		INSTRUMENTATION 	(File.separator + "instrumentations"),
		RUNTIME 			(File.separator + "runtime");
		private String dir;
		private Dir(String s){
			dir = s;
		}
		public String getDir(){
			return dir;
		}
	};
	public static void unzipFile(File path,String fileName){
		ProcessBuilder pb = new ProcessBuilder("jar", "xf",fileName);
		pb.directory(path);
		try {
			Process process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getPathHashCode(String hashCode){
		String s = Dir.APPS.getDir() + File.separator + hashCode;
		return s;
	}
	public static String getPathCode(String hashCode){
		String s = getPathHashCode(hashCode) + Dir.CODE.getDir();
		return s;
	}
	public static String getPathConfig(String hashCode){
		String s = getPathHashCode(hashCode) + Dir.JOANACONFIG.getDir();
		return s;
	}
	public static String getPathInstrumentationOfApp(String hashCode){
		String s = getPathHashCode(hashCode) + Dir.INSTRUMENTATION.getDir();
		return s;
	}
	public static String getPathOutput(String hashCode){
		String s = getPathHashCode(hashCode) + Dir.JOANAOUTPUT.getDir();
		return s;
	}
	public static String getPathSourceAndSinks(){
		return Dir.SOURCEANDSINKS.getDir();
	}
	public static String getPathBlackAndWhiteList(){
		return Dir.BLACKANDWHITELIST.getDir();
	}
	// Relative Path, using  VaadinService.getCurrent().getBaseDirectory().getPath() -> .../src/main/webapp
	public static String getRelativePathHashCode(String hashCode){
		String s = "." + File.separator + "apps" + File.separator + hashCode;
		return s;
	}
	public static String getRelativePathCode(String hashCode){
		String s = getRelativePathHashCode(hashCode) + Dir.CODE.getDir();
		return s;
	}
	public static String getRelativePathConfig(String hashCode){
		String s = getRelativePathHashCode(hashCode) + Dir.JOANACONFIG.getDir();
		return s;
	}
	public static String getRelativePathOutput(String hashCode){
		String s = getRelativePathHashCode(hashCode) + Dir.JOANAOUTPUT.getDir();
		return s;
	}
	public static String getRelativePathIntrumentation(String hashCode){
		String s = getRelativePathHashCode(hashCode) + Dir.INSTRUMENTATION.getDir();
		return s;
	}
	
	
}
