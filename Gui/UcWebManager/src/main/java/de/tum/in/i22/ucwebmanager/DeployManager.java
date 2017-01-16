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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.TomcatConfig;

public class DeployManager{

	//Deploy a java web application on a running Tomcat server
	
    private CredentialsProvider credsProvider = new BasicCredentialsProvider();
    private final String host;
    private final String port;
    
    public DeployManager() {
    	
		TomcatConfig tc = new TomcatConfig();
		
    	File file = new File (FileUtil.getPathTomcatConfFile());
    	if (!file.exists()) {
    		tc.setUsername("tomcat");
    		tc.setPassword("pass");
    		tc.setHost("localhost");
    		tc.setPort("8181");
    		tc.save(file.getPath());
    	} 
    	else {
    		tc.load(file.getPath());
    	}
    	this.host = tc.getHost();	
    	this.port = tc.getPort();
    	
        this.credsProvider.setCredentials(AuthScope.ANY,
        						new UsernamePasswordCredentials(tc.getUsername(), tc.getPassword()));

    }
    
    public DeployManager(String host, String port) {
    	
        /*
         * warning only ever AuthScope.ANY while debugging
         * with these settings the tomcat username and pw are added to EVERY request
         */
        this.credsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("tomcat", "pass"));
        this.host=host;
        this.port=port;
    	
    }

	//private final String urlCorretto = "http://localhost:8181/manager/text/deploy?path=/Prova&war=file:/Users/cataldocalo/Downloads/Prova.war";

    public String deploy(String contextName, String warUrl) throws ClientProtocolException, IOException {
    	
        String url = "http://"+host+":"+port+
        			 "/manager/text/deploy?path=/"+contextName+"&update=true";
        
        File file = new File(warUrl);
        //File file = new File ("/Users/cataldocalo/Downloads/Prova.war") ;

        HttpPut req = new HttpPut(url) ;
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
        meb.addTextBody("fileDescription", "war file to deploy");
        //"application/octect-stream"
        meb.addBinaryBody("attachment", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());

        req.setEntity(meb.build()) ;
        String response = executeRequest (req, credsProvider);

        return response;
    }

    public String undeploy(String contextName) throws ClientProtocolException, IOException{
        //String url = "http://localhost:8181/manager/text/undeploy?path=/Prova";
    	String url = "http://"+host+":"+port+"/manager/text/undeploy?path=/"+contextName;
        HttpGet req = new HttpGet(url) ;
        String response = executeRequest (req, credsProvider);
        return response;
    } 

//    private static void deploy() throws ClientProtocolException, IOException {
//    	
//        String url = "http://localhost:8080/manager/text/deploy?path=/deployMe&update=true";
//        File file = new File ("deployMe.war") ;
//
//        HttpPut req = new HttpPut(url) ;
//        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
//        meb.addTextBody("fileDescription", "war file to deploy");
//        //"application/octect-stream"
//        meb.addBinaryBody("attachment", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
//
//        req.setEntity(meb.build()) ;
//        String response = executeRequest (req, credsProvider);
//
//        System.out.println("Response : "+response);
//    }
//
//    public static void undeploy() throws ClientProtocolException, IOException{
//        String url = "http://localhost:8080/manager/text/undeploy?path=/deployMe";
//        HttpGet req = new HttpGet(url) ;
//        String response = executeRequest (req, credsProvider);
//        System.out.println("Response : "+response);
//    } 

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