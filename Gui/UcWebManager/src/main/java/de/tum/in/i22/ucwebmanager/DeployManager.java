package de.tum.in.i22.ucwebmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.TomcatConfig;

public class DeployManager{

	//Deploy a java web application on a running Tomcat server
	
    private CredentialsProvider credsProvider = new BasicCredentialsProvider();
    private final String host;
    private final String port;
//    private final String username;
//    private final String password;
    
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

    	
    	final String url = "http://"+host+":"+port+"/manager/text/deploy?path=/"+contextName+"&war=file:"+warUrl+"&update=true";
		HttpGet request = new HttpGet(url);
		String response = executeRequest (request, credsProvider);
        return response;
    }
    
    
    public String undeploy(String contextName) throws ClientProtocolException, IOException{
    	final String url = "http://"+host+":"+port+"/manager/text/undeploy?path=/"+contextName;
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

    
    
    private class WarPackager extends Thread {
    	
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

        return res;
    }
}