package de.tum.in.i22.ucwebmanager.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.vaadin.server.VaadinService;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.TomcatConfig;

/**
 * A simple manager to deploy/undeploy an instrumented java web application
 * on/from a running instance of Tomcat server
 */

public class DeployManager{
	
    private CredentialsProvider credsProvider = new BasicCredentialsProvider();
    private final String host;
    private final String port;
    public final String UTF8="UTF-8";
//    private final String username;
//    private final String password;
    
	private static final String UC_JAVA_PEP_LIBRARY_NAME = "uc-java-pep-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
    
    public DeployManager(TomcatConfig configuration) {
    	
        /*
         * warning only ever AuthScope.ANY while debugging
         * with these settings the tomcat username and pw are added to EVERY request
         */
        this.credsProvider.setCredentials(AuthScope.ANY, 
        				new UsernamePasswordCredentials(configuration.getUsername(), configuration.getPassword()));
//        this.username=configuration.getUsername();
//        this.password=configuration.getPassword();
        this.host=configuration.getHost();
        this.port=configuration.getPort();
    	
    }

    
    public String deploy(App app, String report) throws ClientProtocolException, IOException {
		joinCodeWithInstrumentedClasses(app, report);
		addUcJavaPepLibraryToInstrumentedCode(app, report);
		removeUnnecessaryLibraries(app, report);
		moveUcConfigToClassPath(app, report);
		
		String webappPath = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + File.separator + report;
		String warName = FilenameUtils.getBaseName(app.getName()) + ".war";
		String warPath = packageWar(warName, webappPath);
		String contextName = FilenameUtils.getBaseName(app.getName()) + "_" + report;
		
		return deploy(contextName, warPath);
		
    }
    
    public String deploy(String contextName, String warUrl) throws ClientProtocolException, IOException {
    	

        // This method does not work for large war files! 
        // Use the file:{} method in a get request
    	
//        String url = "http://"+host+":"+port+
//   			 "/manager/text/deploy?path=/"+contextName+"&update=true";
//        File file = new File(warUrl);
//        HttpPut req = new HttpPut(url) ;
//        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
//        meb.addTextBody("fileDescription", "war file to deploy");
//        //"application/octect-stream"
//        meb.addBinaryBody("attachment", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
//        //meb.addBinaryBody("attachment", file, ContentType.DEFAULT_BINARY, file.getName());
//        req.setEntity(meb.build()) ;
//        String response = executeRequest (req, credsProvider);

    	
    	final String url = "http://"+URLEncoder.encode(host,UTF8)+":"+URLEncoder.encode(port,UTF8)+"/manager/text/deploy?path=/"+URLEncoder.encode(contextName,UTF8)+"&war=file:"+URLEncoder.encode(warUrl,UTF8)+"&update=true";
//    	final String url = "http://"+username+":"+password+"@"+host+":"+port+"/manager/text/deploy?path=/"+contextName+"&war=file:"+warUrl+"&update=true";

    	HttpGet request = new HttpGet(url);
		String response = executeRequest (request, credsProvider);
        return response;
    }
    
    
    public String undeploy(String contextName) throws ClientProtocolException, IOException{
    	final String url = "http://"+URLEncoder.encode(host,UTF8)+":"+URLEncoder.encode(port,UTF8)+"/manager/text/undeploy?path=/"+URLEncoder.encode(contextName,UTF8);
        HttpGet req = new HttpGet(url) ;
        String response = executeRequest (req, credsProvider);
        return response;
    } 
    
    public String packageWar(String warName, String webappPath) {
    	WarPackager wp = new WarPackager(warName, webappPath);
    	wp.start();
    	try {
			wp.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return webappPath + File.separator + warName;
    }


    private static String executeRequest(HttpRequestBase requestBase, CredentialsProvider credsProvider) throws ClientProtocolException, IOException {
        CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        InputStream responseStream = null;
        String res = null;
        HttpResponse response = client.execute(requestBase) ;
        HttpEntity responseEntity = response.getEntity() ;
        responseStream = responseEntity.getContent() ;

        BufferedReader br = new BufferedReader (new InputStreamReader (responseStream)) ;
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        br.close() ;
        res = sb.toString();

        client.close();
        
        return res;
    }
    
	private void moveUcConfigToClassPath(App app, String report) {
		
		String ucConfigPath = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + File.separator + 
			   	   			  report + File.separator + "uc.config";
		
		File ucConfig = new File(ucConfigPath);
		if (ucConfig.exists()) {
			String classesFolderPath = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + File.separator + 
								   	   report + File.separator + "WEB-INF" + File.separator +
								       "classes";
			
			File classesFolder = new File(classesFolderPath);
			try {
				FileUtils.moveFileToDirectory(ucConfig, classesFolder, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void removeUnnecessaryLibraries(App app, String report) {
		
		String libFolderPath = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + File.separator + 
						   	   report + File.separator + "WEB-INF" + File.separator +
						       "lib";
		
		//These libraries are only necessary for the static analysis
		File catalinaLib = new File(libFolderPath+File.separator+"catalina.jar");
		File servletLib = new File(libFolderPath+File.separator+"servlet-api.jar");
		
		if (catalinaLib.exists())
			catalinaLib.delete();
		if (servletLib.exists())
			servletLib.delete();
	}
	
	private void joinCodeWithInstrumentedClasses(App app, String report) {
		
		String codePath = FileUtil.getPathCode(app.getHashCode());
		String instrumentedCodePath = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + File.separator + report;
		try {
			// Files of the source overwrite files with the same name in the destDir
			// --> Backup of the instrumented classes
			File instrumentedCode = new File(instrumentedCodePath);
			File instrumentedCodeBackup = new File(instrumentedCodePath+"_BACKUP");
//			instrumentedCode.renameTo(instrumentedCodeBackup);
			Files.move(instrumentedCode.toPath(), instrumentedCodeBackup.toPath(), StandardCopyOption.ATOMIC_MOVE);
			// Copy original code into instrumentation subfolder
			FileUtils.copyDirectory(new File(codePath), instrumentedCode);
			
			// Substitute original class files with instrumented ones
			FileUtils.copyDirectory(instrumentedCodeBackup, instrumentedCode);
			
			FileUtils.deleteDirectory(instrumentedCodeBackup);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void addUcJavaPepLibraryToInstrumentedCode(App app, String report) {
		
		String libFolderPath = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + File.separator + 
						   	   report + File.separator + "WEB-INF" + File.separator +
						       "lib";
		File libFolder = new File(libFolderPath);
		if (!libFolder.exists())
			libFolder.mkdirs();
		
		String ucjavapepLibPath = VaadinService.getCurrent().getBaseDirectory().getPath() + File.separator + "WEB-INF"+ File.separator+
								  "lib" + File.separator + UC_JAVA_PEP_LIBRARY_NAME;
		File ucjavapepLib = new File(ucjavapepLibPath);
		
		try {
			FileUtils.copyFileToDirectory(ucjavapepLib, libFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}