package de.tum.in.i22.ucwebmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.util.uri.UriComponent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.vaadin.server.VaadinServlet;

@WebServlet(urlPatterns = "/rest/*", name = "UcWebManagerRestServlet", asyncSupported = true)
// @VaadinServletConfiguration(ui = UcWebManagerRestUI.class, productionMode =
// false)
public class UcWebManagerRestServlet extends VaadinServlet {
	private enum REQPARAM {
		APPID, MSG
	}

	public static enum JSONMsg {
		NODES, LINKS, FQNAME, OFFSET, OPCODE, MISC, SOURCE, TARGET
	}

	private String pathSeparator = File.separator;// System.getProperty("file.separator");
	private String path;
	private String graphFile = pathSeparator + "graph.txt";
	private String runtimePath = File.separator + "runtime";
	private ServletConfig config;

	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		this.init();

		this.path = this.config.getServletContext().getRealPath("/") + File.separator + "apps";
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String appid = "";
		String msg = "";
		String myPath = path;
		for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			if (key.toUpperCase().equals(REQPARAM.APPID.toString())) {
				appid = request.getParameter(key);
			} else if (key.toUpperCase().equals(REQPARAM.MSG.toString())) {
				msg = request.getParameter(REQPARAM.MSG.toString());
				msg = UriComponent.decode(msg,UriComponent.Type.QUERY_PARAM);
			}
		}
		if (!"".equals(appid) && !"".equals(msg)) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject cnt = new JSONObject();
				myPath += pathSeparator + appid;
				File runtimeGraph = new File(myPath + runtimePath + graphFile);
				if (!runtimeGraph.exists()) {
					runtimeGraph.getParentFile().mkdirs();
					runtimeGraph.createNewFile();
				} else {
					try {
						cnt = (JSONObject) parser.parse(new FileReader(runtimeGraph));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				JSONArray cntLinks = new JSONArray();
				if (cnt.containsKey(JSONMsg.LINKS.toString())){
					String o = (String)cnt.get(JSONMsg.LINKS.toString());
					cntLinks = (JSONArray) parser.parse(o);
				}
				
				JSONArray cntNodes = new JSONArray();
				if (cnt.containsKey(JSONMsg.NODES.toString())){
					String o = (String)cnt.get(JSONMsg.NODES.toString());
					cntNodes = (JSONArray) parser.parse(o);
				}

//				Add received links and nodes and dump data to file
				JSONObject msgCnt = (JSONObject) parser.parse(msg);
				if(msgCnt.containsKey(JSONMsg.LINKS.toString()))
					cntLinks.add(msgCnt.get(JSONMsg.LINKS.toString()));
				if(msgCnt.containsKey(JSONMsg.NODES.toString()))
					cntNodes.add(msgCnt.get(JSONMsg.NODES.toString()));
				
				cnt.put(JSONMsg.LINKS.toString(), cntLinks.toString());
				cnt.put(JSONMsg.NODES.toString(), cntNodes.toString());
				FileWriter fw = new FileWriter(runtimeGraph);
				fw.write(cnt.toString());
				fw.flush();
				fw.close();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String readFile(File file) throws IOException {
		StringBuilder _return = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null)
			_return.append(line);
		return _return.toString();
	}
}
