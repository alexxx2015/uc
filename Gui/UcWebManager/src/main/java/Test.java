import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.methods.HttpGet;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s1 = "C:\\Users\\Administrator\\Documents\\InfoFlow\\pdp\\Gui\\UcWebManager\\src\\main\\webapp\\apps\\fa23ea3d74ecdb95c3ce42107e5316c3\\code\\META-INF";
		String s2 = "C:\\Users\\Administrator\\Documents\\InfoFlow\\pdp\\Gui\\UcWebManager\\src\\main\\webapp\\apps\\fa23ea3d74ecdb95c3ce42107e5316c3\\code";
		String cur = "./";
		String split = File.separator;
		
		String url="";
		try {
			url = "http://localhost:8080/manager/text/deploy?path=/Prova_20170216145402&amp;war=file:"+URLEncoder.encode("C:\\Users\\Administrator\\apache-tomcat-9.0.0.M17\\webapps\\UcWebManager\\apps\\fa23ea3d74ecdb95c3ce42107e5316c3\\instrumentations\\20170216145402\\Prova.war","UTF-8")+"&amp;update=true";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	HttpGet request = new HttpGet(url);
		
		System.out.println(s1.replace(s2+File.separator, cur));
	}

}
