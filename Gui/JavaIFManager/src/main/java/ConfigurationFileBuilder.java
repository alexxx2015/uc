import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import edu.kit.joana.flowanalyzer.POIDescriptor;

public class ConfigurationFileBuilder {
	private boolean multithreaded;
	private boolean computeChops;
	private boolean systemOut;
	private boolean objectsensitiveness;
	private boolean indirectFlows;
	private String mode;
	private String classpath;
	private String thirdPartyLibs;
	private String entryPoint;
	private String sdgFile;
	private String cgFile;
	private String reportFile;
	private String ptPolicy;
	private String ptFallback;
	private String ptIncludeClasses;
	private String ptExcludeClasses;
	private String snsFile;
	private String statisticsFile;
	private String logfile;
	private String analysisname;

	public String generateReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		sb.append("<analysises>\n");
		sb.append("\t<analysis name=\"" + this.analysisname + "\">\n");
		sb.append("\t\t<mode value=\"" + this.mode + "\"/>\n");
		sb.append("\t\t<classpath value=\"" + this.classpath + "\"/>\n");
		sb.append("\t\t<thirdPartyLibs value=\"" + this.thirdPartyLibs+ "\"/>\n");
		sb.append("\t\t<stubs value=\"JRE_14\"/>\n");
		sb.append("\t\t<entrypoint value=\"" + this.entryPoint + "\"/>\n");
		sb.append("\t\t<ignoreIndirectFlows value=\"" + this.indirectFlows
				+ "\"/>\n");
		sb.append("\t\t<points-to policy=\"" + this.ptPolicy + "\" fallback=\""
				+ this.ptFallback + "\">\n");
		if (!"".equals(this.ptIncludeClasses) && (this.ptIncludeClasses != null)) {
			for (String s : this.ptIncludeClasses.split("\n")) {
				if (!"".equals(s))
					sb.append("\t\t\t<include-classes value=\"" + s + "\"/>\n");
			}
		}
		if (!"".equals(this.ptExcludeClasses) && (this.ptExcludeClasses != null)) {
			for (String s : this.ptExcludeClasses.split("\n")) {
				if (!"".equals(s))
					sb.append("\t\t\t<exclude-classes value=\"" + s + "\"/>\n");
			}
		}
		sb.append("\t\t</points-to>\n");
		sb.append("\t\t<multithreaded value=\"" + this.multithreaded + "\"/>\n");
		sb.append("\t\t<sdgfile value=\"" + this.sdgFile + "\"/>\n");
		sb.append("\t\t<cgfile value=\"" + this.cgFile + "\"/>\n");
		sb.append("\t\t<reportfile value=\"" + this.reportFile + "\"/>\n");
		sb.append("\t\t<statistics value=\"" + this.statisticsFile + "\"/>\n");
		sb.append("\t\t<logFile value=\"" + this.logfile + "\"/>\n");
		sb.append("\t\t<computeChops value=\"" + this.computeChops + "\"/>\n");
		sb.append("\t\t<systemout value=\"" + this.systemOut + "\"/>\n");
		sb.append("\t\t<objectsensitivenes value=\"" + this.objectsensitiveness+"\"/>\n");
		sb.append("\t\t<sourcesandsinks>\n");
		sb.append("\t\t\t<file value=\"" + this.snsFile + "\"/>\n");
		sb.append("\t\t</sourcesandsinks>\n");
		sb.append("\t</analysis>\n");
		sb.append("</analysises>\n");

		return sb.toString();
	}
	
	public File generateSinkSourceFile(ArrayList<POIDescriptor> sinks, ArrayList<POIDescriptor> sources, File appname){

		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		sb.append("<sourcesandsinks>\n");
		Iterator<POIDescriptor> sinksIt = sinks.iterator();
		while(sinksIt.hasNext()){
			POIDescriptor p = sinksIt.next();
			String[] s = POIDescriptor.translateParameters(p.getParameters());
			String params = "";
			for(String s1:s){
				params += s1;
			}
			sb.append("\t<sink class=\""+p.getClass()+"\" selector=\""+p.getMethodSelector()+"\" params=\""+params+"\"/>\n");
		}
		Iterator<POIDescriptor> sourseIt = sources.iterator();
		while(sourseIt.hasNext()){
			POIDescriptor p = sourseIt.next();
			String[] s = POIDescriptor.translateParameters(p.getParameters());
			String params = "";
			for(String s1:s){
				params += s1;
			}
			sb.append("\t<source class=\""+p.getClass()+"\" selector=\""+p.getMethodSelector()+"\" params=\""+params+"\"/>\n");
		}
		sb.append("</sourcesandsinks>");
		File f = new File("./tmp_"+(int)(Math.random()*10000)+".xml");
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}

	public boolean isMultithreaded() {
		return multithreaded;
	}

	public void setMultithreaded(boolean multithreaded) {
		this.multithreaded = multithreaded;
	}

	public boolean isComputeChops() {
		return computeChops;
	}

	public void setComputeChops(boolean computeChopw) {
		this.computeChops = computeChopw;
	}

	public boolean isSystemOut() {
		return systemOut;
	}

	public void setSystemOut(boolean systemOut) {
		this.systemOut = systemOut;
	}

	public boolean isObjectsensitiveness() {
		return objectsensitiveness;
	}

	public void setObjectsensitiveness(boolean objectsensitiveness) {
		this.objectsensitiveness = objectsensitiveness;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	public String getThirdPartyLibs() {
		return thirdPartyLibs;
	}

	public void setThirdPartyLibs(String thirdPartyLibs) {
		this.thirdPartyLibs = thirdPartyLibs;
	}

	public String getEntryPoint() {
		return entryPoint;
	}

	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
	}

	public String getSdgFile() {
		return sdgFile;
	}

	public void setSdgFile(String sdgFile) {
		this.sdgFile = sdgFile;
	}

	public String getCgFile() {
		return cgFile;
	}

	public void setCgFile(String cgFile) {
		this.cgFile = cgFile;
	}

	public String getReportFile() {
		return reportFile;
	}

	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	public String getPtPolicy() {
		return ptPolicy;
	}

	public void setPtPolicy(String ptPolicy) {
		this.ptPolicy = ptPolicy;
	}

	public String getPtFallback() {
		return ptFallback;
	}

	public void setPtFallback(String ptFallback) {
		this.ptFallback = ptFallback;
	}

	public String getPtIncludeClasses() {
		return ptIncludeClasses;
	}

	public void setPtIncludeClasses(String ptIncludeClasses) {
		this.ptIncludeClasses = ptIncludeClasses;
	}

	public String getSnsFile() {
		return snsFile;
	}

	public void setSnsFile(String snsFile) {
		this.snsFile = snsFile;
	}

	public boolean isIndirectFlows() {
		return indirectFlows;
	}

	public void setIndirectFlows(boolean indirectFlows) {
		this.indirectFlows = indirectFlows;
	}

	public String getStatisticsFile() {
		return statisticsFile;
	}

	public void setStatisticsFile(String statisticsFile) {
		this.statisticsFile = statisticsFile;
	}

	public String getLogfile() {
		return logfile;
	}

	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	public String getAnalysisname() {
		return analysisname;
	}

	public void setAnalysisname(String analysisName) {
		this.analysisname = analysisName;
	}

	public String getPtExcludeClasses() {
		return ptExcludeClasses;
	}

	public void setPtExcludeClasses(String ptExcludeClasses) {
		this.ptExcludeClasses = ptExcludeClasses;
	}

}
