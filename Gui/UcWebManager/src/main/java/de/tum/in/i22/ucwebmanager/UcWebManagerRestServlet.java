package de.tum.in.i22.ucwebmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

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

import de.tum.in.i22.ucwebmanager.JSON.JSONUtil;

@WebServlet(urlPatterns = "/rest/*", name = "UcWebManagerRestServlet", asyncSupported = true)
// @VaadinServletConfiguration(ui = UcWebManagerRestUI.class, productionMode =
// false)
public class UcWebManagerRestServlet extends VaadinServlet {
	private enum REQPARAM {
		APPID, MSG
	}

	public static String MSGNODES = "NODES";
	public static String MSGLINKS = "LINKS";
	
	public static enum JSONMsgNODES {
		 FQNAME, OFFSET, OPCODE, MISC
	}
	
	public static enum JSONMsgLINKS {
		SOURCE, TARGET
	}

	private String pathSeparator = File.separator;// System.getProperty("file.separator");
	private String path;
//	private String graphFileTxt = pathSeparator + "graph.txt";
	private String graphFileJSON = pathSeparator + "graph.json";
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
				File runtimeGraph = new File(myPath + runtimePath + graphFileJSON);
				if (!runtimeGraph.exists()) {
					runtimeGraph.getParentFile().mkdirs();
					runtimeGraph.createNewFile();
				} else {
					try {
						cnt = (JSONObject) parser.parse(new FileReader(runtimeGraph));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				//---------- NEW CODE
				
				JSONArray cntLinks = new JSONArray();
				if (cnt.containsKey(JSONUtil.LINKS))
					cntLinks = (JSONArray) cnt.get(JSONUtil.LINKS);
				
				JSONArray cntNodes = new JSONArray();
				if (cnt.containsKey(JSONUtil.NODES))
					cntNodes = (JSONArray) cnt.get(JSONUtil.NODES);
				//----------
//				JSONArray cntLinks = new JSONArray();
//				if (cnt.containsKey(LINKS)){
//					String o = (String)cnt.get(LINKS);
//					cntLinks = (JSONArray) parser.parse(o);
//				}
				
//				JSONArray cntNodes = new JSONArray();
//				if (cnt.containsKey(NODES)){
//					String o = (String)cnt.get(NODES);
//					cntNodes = (JSONArray) parser.parse(o);
//				}

//				Add received links and nodes and dump data to file
				JSONObject msgCnt = (JSONObject) parser.parse(msg);
				if(msgCnt.containsKey(MSGNODES)){
					JSONArray nodes = (JSONArray) msgCnt.get(MSGNODES);
					Iterator<JSONArray> iterator = nodes.iterator();
					while (iterator.hasNext()){
						JSONObject tempObj = new JSONObject();
						JSONArray j = iterator.next();
						Iterator<JSONObject> iter2 = j.iterator();
						while (iter2.hasNext()){
							JSONObject json2 = iter2.next();
							
							for (Iterator iter3 = json2.keySet().iterator();iter3.hasNext();){
								String key = (String) iter3.next();
								tempObj.put(key, json2.get(key).toString());
							}
							
						}
						// ----------- NEW CODE ----------------
						//insertNodeID2(tempObj);
						// -------------------------------------
						if (!cntNodes.contains(tempObj))
							cntNodes.add(tempObj);
						else
							System.out.println("Nodes list already contains node " + tempObj.get("id"));
					}
				}		
				if(msgCnt.containsKey(MSGLINKS)){
					JSONArray links = (JSONArray) msgCnt.get(MSGLINKS);
					Iterator<JSONObject> iter = links.iterator();
					while (iter.hasNext()){
						JSONObject tempObj = new JSONObject();
						JSONObject link = iter.next();
						for (Iterator iter3 = link.keySet().iterator(); iter3.hasNext();){
							String key = (String) iter3.next();
							tempObj.put(key.toLowerCase(), link.get(key).toString());
						}
						
						if (!cntLinks.contains(tempObj) && isLinkValid(tempObj, cntNodes))
							cntLinks.add(tempObj);
						
					}
				}
//					cntLinks.add(msgCnt.get(LINKS));
				
//				cnt.put(LINKS, cntLinks.toString());
//				cnt.put(NODES, cntNodes.toString());
				// -------------- NEW CODE -----------------------
				cnt.put(JSONUtil.LINKS.toLowerCase(), cntLinks);
				cnt.put(JSONUtil.NODES.toLowerCase(), cntNodes);
				// -------------- NEW CODE -----------------------
				FileWriter fw = new FileWriter(runtimeGraph);
				fw.write(cnt.toJSONString());
				fw.flush();
				fw.close();
				//Create .json file from the txt file
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void insertNodeID2(JSONObject node) {
		String opcode = (String) node.get(JSONMsgNODES.OPCODE.toString());
		String offset = (String) node.get(JSONMsgNODES.OFFSET.toString());
		String fqname = (String) node.get(JSONMsgNODES.FQNAME.toString());
		
		node.put("id", opcode+":"+fqname+"."+offset);
	}
	
	private static boolean isLinkValid(JSONObject link, JSONArray nodes) {
		String source = (String) link.get(JSONMsgLINKS.SOURCE.toString().toLowerCase());
		String target = (String) link.get(JSONMsgLINKS.TARGET.toString().toLowerCase());
		boolean srcFnd=false; boolean trgFnd=false;
		
		for (Iterator iter = nodes.iterator(); iter.hasNext() && (!srcFnd || !trgFnd);) {
			JSONObject node = (JSONObject) iter.next();
			String id = (String)node.get("id");
			if (!srcFnd) srcFnd= id.equals(source);
			if (!trgFnd) trgFnd= id.equals(target);
		}
		if (srcFnd && trgFnd) System.out.println("Link valid: " + source + " -> " 
							  + target);
		else System.out.println("Link not valid: "+ source + " -> " 
				  				+ target);
		return srcFnd && trgFnd;
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
