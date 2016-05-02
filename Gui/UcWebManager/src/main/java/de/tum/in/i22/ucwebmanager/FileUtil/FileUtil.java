package de.tum.in.i22.ucwebmanager.FileUtil;

import java.io.File;
import java.io.IOException;

import com.vaadin.server.VaadinService;

public class FileUtil {
	public enum Dir{
		APPS	(VaadinService.getCurrent().getBaseDirectory().getPath()+"/apps"),
		CODE	("/code"),
		RUN		("/staticAnalysis/run"),
		CONFIG 	("/staticAnalysis/configurations"),
		INSTRUMENTATION ("/instrumentations"),
		RUNTIME ("/runtime");
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
	public static String getPathHashCodeOfApp(int hashCode){
		String s = Dir.APPS.getDir() +"/"+ String.valueOf(hashCode);
		return s;
	}
	public static String getPathCodeOfApp(int hashCode){
		String s = getPathHashCodeOfApp(hashCode) + Dir.CODE.getDir();
		return s;
	}
	public static String getPathConfigOfApp(int hashCode){
		String s = getPathHashCodeOfApp(hashCode) + Dir.CONFIG.getDir();
		return s;
	}
	public static String getPathInstrumentationOfApp(int hashCode){
		String s = getPathHashCodeOfApp(hashCode) + Dir.INSTRUMENTATION.getDir();
		return s;
	}
	public static String getPathRunOfApp(int hashCode){
		String s = getPathHashCodeOfApp(hashCode) + Dir.RUN.getDir();
		return s;
	}
	// Relative Path, using  VaadinService.getCurrent().getBaseDirectory().getPath() -> .../src/main/webapp
	public static String getRelativePathHashCode(int hashCode){
		String s = "./apps/"+String.valueOf(hashCode);
		return s;
	}
	public static String getRelativePathCode(int hashCode){
		String s = getRelativePathHashCode(hashCode) + Dir.CODE.getDir();
		return s;
	}
	public static String getRelativePathConfig(int hashCode){
		String s = getRelativePathHashCode(hashCode) + Dir.CONFIG.getDir();
		return s;
	}
	public static String getRelativePathRun(int hashCode){
		String s = getRelativePathHashCode(hashCode) + Dir.RUN.getDir();
		return s;
	}
	public static String getRelativePathIntrumentation(int hashCode){
		String s = getRelativePathHashCode(hashCode) + Dir.INSTRUMENTATION.getDir();
		return s;
	}
}
