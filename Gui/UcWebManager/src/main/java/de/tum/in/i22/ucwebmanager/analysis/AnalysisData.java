package de.tum.in.i22.ucwebmanager.analysis;

import java.util.List;

public class AnalysisData {
	private String analysisName;
	private String mode;
	private List<String> classpath;
	private List<String> thirdPartyLibs;
	private String stubs;
	private String entrypoint;
	private String pointsto_policy;
	private String pointsto_fallback;
	private List<String> pointsto_includeclasses;
	private List<String> pointsto_excludeclasses;
	private String ignoreIndirectFlows;
	private String multiThreaded;
	private String sdgFile;
	private String cgFile;
	private String logFile;
	private String pruningPolicy;
	private String omitIFC;
	private String reportFile;
	private String statistics;
	private String systemout;
	private String computeChops;
	private String objectsensitivenes;
	private List<SourcesSinks> sourcesSinks;
	private List<String> sourcesSinksFiles;
	
	
	
	public String getAnalysisName() {
		return analysisName;
	}



	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}



	public String getMode() {
		return mode;
	}



	public void setMode(String mode) {
		this.mode = mode;
	}



	public List<String> getClasspath() {
		return classpath;
	}



	public void setClasspath(List<String> classpath) {
		this.classpath = classpath;
	}



	public List<String> getThirdPartyLibs() {
		return thirdPartyLibs;
	}



	public void setThirdPartyLibs(List<String> thirdPartyLibs) {
		this.thirdPartyLibs = thirdPartyLibs;
	}



	public String getStubs() {
		return stubs;
	}



	public void setStubs(String stubs) {
		this.stubs = stubs;
	}



	public String getEntrypoint() {
		return entrypoint;
	}



	public void setEntrypoint(String entrypoint) {
		this.entrypoint = entrypoint;
	}



	public String getPointsto_policy() {
		return pointsto_policy;
	}



	public void setPointsto_policy(String pointsto_policy) {
		this.pointsto_policy = pointsto_policy;
	}



	public String getPointsto_fallback() {
		return pointsto_fallback;
	}



	public void setPointsto_fallback(String pointsto_fallback) {
		this.pointsto_fallback = pointsto_fallback;
	}



	public List<String> getPointsto_includeclasses() {
		return pointsto_includeclasses;
	}



	public void setPointsto_includeclasses(List<String> pointsto_includeclasses) {
		this.pointsto_includeclasses = pointsto_includeclasses;
	}



	public List<String> getPointsto_excludeclasses() {
		return pointsto_excludeclasses;
	}



	public void setPointsto_excludeclasses(List<String> pointsto_excludeclasses) {
		this.pointsto_excludeclasses = pointsto_excludeclasses;
	}



	public String getIgnoreIndirectFlows() {
		return ignoreIndirectFlows;
	}



	public void setIgnoreIndirectFlows(String ignoreIndirectFlows) {
		this.ignoreIndirectFlows = ignoreIndirectFlows;
	}



	public String getMultiThreaded() {
		return multiThreaded;
	}



	public void setMultiThreaded(String multiThreaded) {
		this.multiThreaded = multiThreaded;
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



	public String getLogFile() {
		return logFile;
	}



	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}



	public String getPruningPolicy() {
		return pruningPolicy;
	}



	public void setPruningPolicy(String pruningPolicy) {
		this.pruningPolicy = pruningPolicy;
	}



	public String getOmitIFC() {
		return omitIFC;
	}



	public void setOmitIFC(String omitIFC) {
		this.omitIFC = omitIFC;
	}



	public String getReportFile() {
		return reportFile;
	}



	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}



	public String getStatistics() {
		return statistics;
	}



	public void setStatistics(String statistics) {
		this.statistics = statistics;
	}



	public String getSystemout() {
		return systemout;
	}



	public void setSystemout(String systemout) {
		this.systemout = systemout;
	}



	public String getComputeChops() {
		return computeChops;
	}



	public void setComputeChops(String computeChops) {
		this.computeChops = computeChops;
	}



	public String getObjectsensitivenes() {
		return objectsensitivenes;
	}



	public void setObjectsensitivenes(String objectsensitivenes) {
		this.objectsensitivenes = objectsensitivenes;
	}



	public List<SourcesSinks> getSourcesSinks() {
		return sourcesSinks;
	}



	public void setSourcesSinks(List<SourcesSinks> sourcesSinks) {
		this.sourcesSinks = sourcesSinks;
	}



	public List<String> getSourcesSinksFiles() {
		return sourcesSinksFiles;
	}



	public void setSourcesSinksFiles(List<String> sourcesSinksFiles) {
		this.sourcesSinksFiles = sourcesSinksFiles;
	}


	public enum TYPES{SINK,SOURCE};

	public class SourcesSinks{
		private TYPES type;
		private String clazz;
		private String selector;
		private String params;
		private String includeSubClasses;
		private String indirectCalls;
		public String getClazz() {
			return clazz;
		}
		public void setClazz(String clazz) {
			this.clazz = clazz;
		}
		public String getSelector() {
			return selector;
		}
		public void setSelector(String selector) {
			this.selector = selector;
		}
		public String getParams() {
			return params;
		}
		public void setParams(String params) {
			this.params = params;
		}
		public String getIncludeSubClasses() {
			return includeSubClasses;
		}
		public void setIncludeSubClasses(String includeSubClasses) {
			this.includeSubClasses = includeSubClasses;
		}
		public String getIndirectCalls() {
			return indirectCalls;
		}
		public void setIndirectCalls(String indirectCalls) {
			this.indirectCalls = indirectCalls;
		}
		public TYPES getType() {
			return type;
		}
		public void setType(TYPES type) {
			this.type = type;
		}
	}
}
