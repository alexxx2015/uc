package de.tum.in.i22.ucwebmanager.FileUtil;

import java.io.File;
import java.io.IOException;

import com.vaadin.server.VaadinService;

public class FileUtil {
	public enum Dir{
		APPS			(VaadinService.getCurrent().getBaseDirectory().getPath()+"/apps"),
		CODE			("/code"),
		JOANACONFIG		("/joana-config"),
		JOANAOUTPUT 	("/joana-output"),
		INSTRUMENTATION ("/instrumentations"),
		RUNTIME 		("/runtime");
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
		String s = Dir.APPS.getDir() +"/"+ hashCode;
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
	// Relative Path, using  VaadinService.getCurrent().getBaseDirectory().getPath() -> .../src/main/webapp
	public static String getRelativePathHashCode(String hashCode){
		String s = "./apps/"+hashCode;
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
