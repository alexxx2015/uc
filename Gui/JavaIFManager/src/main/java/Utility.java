import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.io.Files;


public class Utility {

	public static String generateClassPath(String workingdir) {
		String _return = workingdir+"/slf4j-api-1.7.7.jar"
				+ System.getProperty("path.separator") + workingdir+"/guava-17.0.jar"
				+ System.getProperty("path.separator") + workingdir+"/ThriftStubs-1.0.jar"
				+ System.getProperty("path.separator") + workingdir+"/ThriftDefinitions-1.0.jar"
				+ System.getProperty("path.separator") + workingdir+"/CommunicationBase-1.0.jar"
				+ System.getProperty("path.separator") + workingdir+"/spring-instrument-tomcat-3.2.1.RELEASE.jar"
				+ System.getProperty("path.separator") + workingdir+"/catalina.jar"
				+ System.getProperty("path.separator") + workingdir+"/tomcat-api.jar"
				+ System.getProperty("path.separator") + workingdir+"/java-pep-asm-2.3.jar"
				+ System.getProperty("path.separator") + workingdir+"/commons-validator-1.4.0.jar"
				+ System.getProperty("path.separator") + workingdir+"/libthrift-0.9.1.jar"
				+ System.getProperty("path.separator") + workingdir+"/json-simple-1.1.1.jar";
		return _return;
	}
	
	public static void moveJars2Workingdir(String workingdir){
		File f = new File(workingdir);
		f.mkdirs();
		try {
			URL url = Utility.class.getResource("/runtimelib/slf4j-api-1.7.7.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/slf4j-api-1.7.7.jar"));
			url = Utility.class.getResource("/runtimelib/ThriftStubs-1.0.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/ThriftStubs-1.0.jar"));
			url = Utility.class.getResource("/runtimelib/ThriftDefinitions-1.0.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/ThriftDefinitions-1.0.jar"));
			url = Utility.class.getResource("/runtimelib/CommunicationBase-1.0.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/CommunicationBase-1.0.jar"));
			url = Utility.class.getResource("/runtimelib/spring-instrument-tomcat-3.2.1.RELEASE.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/spring-instrument-tomcat-3.2.1.RELEASE.jar"));
			url = Utility.class.getResource("/runtimelib/catalina.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/catalina.jar"));
			url = Utility.class.getResource("/runtimelib/tomcat-api.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/tomcat-api.jar"));
			url = Utility.class.getResource("/runtimelib/java-pep-asm-2.3.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/java-pep-asm-2.3.jar"));
			url = Utility.class.getResource("/runtimelib/commons-validator-1.4.0.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/commons-validator-1.4.0.jar"));
			url = Utility.class.getResource("/runtimelib/libthrift-0.9.1.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/libthrift-0.9.1.jar"));
			url = Utility.class.getResource("/runtimelib/json-simple-1.1.1.jar");
			Files.copy(new File(url.getFile()), new File(workingdir+"/json-simple-1.1.1.jar"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
