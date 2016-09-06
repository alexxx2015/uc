package de.tum.in.i22.ucwebmanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.server.VaadinServlet;

import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;

@WebServlet(urlPatterns = "/rest/*", name = "UcWebManagerRestServlet", asyncSupported = true)
//@VaadinServletConfiguration(ui = UcWebManagerRestUI.class, productionMode = false)
public class UcWebManagerRestServlet extends VaadinServlet {
	private enum REQPARAM {
		APPID, MSG
	}

	private String pathSeparator = File.separator;//System.getProperty("file.separator");
	private String path;
	private String graphFile = pathSeparator + "graph.txt";
	private String runtimePath = File.separator + "runtime";
	private ServletConfig config;
	public void init(ServletConfig config) throws ServletException{
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
			}
		}
		if (!"".equals(appid) && !"".equals(msg)) {
			myPath += pathSeparator + appid;
			File runtimeGraph = new File(myPath + runtimePath + graphFile);
			if(!runtimeGraph.exists()){
				runtimeGraph.getParentFile().mkdirs();
				runtimeGraph.createNewFile();
			}
			FileWriter fw = new FileWriter(runtimeGraph);
			fw.write(msg);
			fw.flush();
			fw.close();
		}
	}
}
