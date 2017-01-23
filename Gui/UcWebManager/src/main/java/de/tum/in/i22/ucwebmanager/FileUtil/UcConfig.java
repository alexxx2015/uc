package de.tum.in.i22.ucwebmanager.FileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class UcConfig {
	public static final String pip_host = "PIP_HOST";
	public static final String pip_port = "PIP_PORT";
	public static final String pdp_host = "PDP_HOST";
	public static final String pdp_port = "PDP_PORT";
	public static final String pmp_host = "PMP_HOST";
	public static final String pmp_port = "PMP_PORT";
	public static final String mypep_host = "MYPEP_HOST";
	public static final String mypep_port = "MYPEP_PORT";
	public static final String analysis_report = "ANALYSIS_REPORT";
	public static final String instrumented_class_path = "INSTRUMENTED_CLASS_PATH";
	public static final String enforcement = "ENFORCEMENT";
	public static final String blacklist = "BLACKLIST";
	public static final String whitelist ="WHITELIST";
	public static final String instrumentation = "INSTRUMENTATION";
	public static final String statistics = "STATISTICS";
	public static final String timer_t1 = "TIMER_T1";
	public static final String timer_t2 = "TIMER_T2";
	public static final String timer_t3 = "TIMER_T3";
	public static final String timer_t4 = "TIMER_T4";
	public static final String timer_t5 = "TIMER_T5";
	public static final String netcom = "NETCOM";
	public static final String log_chop_nodes = "LOGCHOPNODES";
	public static final String uc_wb_mgm_url = "UCWEBMGMURL";
	public static final String uc_properties = "UC_PROPERTIES";
	public static final String pdp_asyncom = "PDP_ASYNCOM";
	public static final String uc4win_autostart = "UC4WIN_AUTOSTART";
	public static final String ift = "IFT";
	public static final String timermethods = "TIMERMETHODS";
	public static final String appid = "APPID";
	
	Properties prop;
	public UcConfig() {
		this.prop = new Properties();
	}
	public void setPip_host(String s){
		prop.setProperty(pip_host, s);
	}
	public String getPip_host(){
		return prop.getProperty(pip_host);
	}
	public void setPip_port(String s){
		prop.setProperty(pip_port, s);
	}
	public String getPip_port(){
		return prop.getProperty(pip_port);
	}
	public void setPdp_host(String s){
		prop.setProperty(pdp_host, s);
	}
	public String getPdp_host(){
		return prop.getProperty(pdp_host);
	}
	public void setPdp_port(String s){
		prop.setProperty(pdp_port, s);
	}
	public String getPdp_port(){
		return prop.getProperty(pdp_port);
	}
	public void setPmp_host(String s){
		prop.setProperty(pmp_host, s);
	}
	public String getPmp_host(){
		return prop.getProperty(pmp_host);
	}
	public void setPmp_port(String s){
		prop.setProperty(pmp_port, s);
	}
	public String getPmp_port(){
		return prop.getProperty(pmp_port);
	}
	public void setMypep_host(String s){
		prop.setProperty(mypep_host, s);
	}
	public String getMypep_host(){
		return prop.getProperty(mypep_host);
	}
	public void setMypep_port(String s){
		prop.setProperty(mypep_port, s);
	}
	public String getMypep_port(){
		return prop.getProperty(mypep_port);
	}
	public void setAnalysis_report(String s){
		prop.setProperty(analysis_report, s);
	}
	public String getAnalysis_report(){
		return prop.getProperty(analysis_report);
	}
	public void setInstrumented_class_path(String s){
		prop.setProperty(instrumented_class_path, s);
	}
	public String getInstrumented_class_path(){
		return prop.getProperty(instrumented_class_path);
	}
	public void setEnforcement(String s){
		prop.setProperty(enforcement, s);
	}
	public String getEnforcement(){
		return prop.getProperty(enforcement);
	}
	public void setBlacklist(String s){
		prop.setProperty(blacklist, s);
	}
	public String getBlacklist(){
		return prop.getProperty(blacklist);
	}
	public void setWhitelist(String s){
		prop.setProperty(whitelist, s);
	}
	public String getWhitelist(){
		return prop.getProperty(whitelist);
	}
	public void setInstrumentation(String s){
		prop.setProperty(instrumentation, s);
	}
	public String getInstrumentation(){
		return prop.getProperty(instrumentation);
	}
	
	public void setStatistics(String s){
		prop.setProperty(statistics, s);
	}
	public String getStatistics(){
		return prop.getProperty(statistics);
	}
	public void setTimer_t1(String s){
		prop.setProperty(timer_t1, s);
	}
	public String getTimer_t1(){
		return prop.getProperty(timer_t1);
	}
	public void setTimer_t2(String s){
		prop.setProperty(timer_t2, s);
	}
	public String getTimer_t2(){
		return prop.getProperty(timer_t2);
	}
	public void setTimer_t3(String s){
		prop.setProperty(timer_t3, s);
	}
	public String setTimer_t3(){
		return prop.getProperty(timer_t3);
	}
	public void setTimer_t4(String s){
		prop.setProperty(timer_t4, s);
	}
	public String getTimer_t4(){
		return prop.getProperty(timer_t4);
	}
	public void setTimer_t5(String s){
		prop.setProperty(timer_t5, s);
	}
	public String getTimer_t5(){
		return prop.getProperty(timer_t5);
	}
	public void setNetcom(String s){
		prop.setProperty(netcom, s);
	}
	public String getNetcom(){
		return prop.getProperty(netcom);
	}
	public void setLogChopNodes(String s) {
		prop.setProperty(log_chop_nodes, s);
	}
	public String getLogChopNodes() {
		return prop.getProperty(log_chop_nodes);
	}
	public void setUcWebMgmUrl(String s) {
		prop.setProperty(uc_wb_mgm_url, s);
	}
	public String getUcWebMgmUrl() {
		return prop.getProperty(uc_wb_mgm_url);
	}
	public void setUc_properties(String s){
		prop.setProperty(uc_properties, s);
	}
	public String getUc_properties(){
		return prop.getProperty(uc_properties);
	}
	public void setPdp_asyncom(String s){
		prop.setProperty(pdp_asyncom, s);
	}
	public String getPdp_asyncom(){
		return prop.getProperty(pdp_asyncom);
	}
	public void setUc4win_autostart(String s){
		prop.setProperty(uc4win_autostart, s);
	}
	public String getUc4win_autostart(){
		return prop.getProperty(uc4win_autostart);
	}
	public void setIft(String s){
		prop.setProperty(ift, s);
	}
	public String getIft(){
		return prop.getProperty(ift);
	}
	public void setTimermethods(String s){
		prop.setProperty(timermethods, s);
	}
	public String getTimermethods(String s){
		return prop.getProperty(timermethods);
	}
	public void setAppId(String s){
		prop.setProperty(appid, s);
	}
	public String getAppId(){
		return prop.getProperty(appid);
	}
	public void load(String path){
		InputStream input = null;
		try {
			input = new FileInputStream(path);
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void save(String path){
		try {
			OutputStream output = new FileOutputStream(path);
			prop.store(output, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
