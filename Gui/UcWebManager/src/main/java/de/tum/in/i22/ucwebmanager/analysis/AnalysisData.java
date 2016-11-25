package de.tum.in.i22.ucwebmanager.analysis;

import java.util.List;

public class AnalysisData {
	private String analysisName;
	private String mode;
	private List<String> classpath;
	private List<String> thirdPartyLibs;
	private String stubs;
	private String entrypoint;
	private String pointsToPolicy;
	private String pointsToFallback;
	private List<String> pointsToIncludeClasses;
	private List<String> pointsToExcludeClasses;
	private String ignoreIndirectFlows;
	private String multiThreaded;
	private String sdgFile;
	private String cgFile;
	private String logFile;
	private String pruningPolicy;
	private String omitIFC;
	private String reportFile;
	private String statisticsFile;
	private String systemOut;
	private String computeChops;
	private String objectSensitiveness;
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



	public String getPointsToPolicy() {
		return pointsToPolicy;
	}



	public void setPointsToPolicy(String pointsToPolicy) {
		this.pointsToPolicy = pointsToPolicy;
	}



	public String getPointsToFallback() {
		return pointsToFallback;
	}



	public void setPointsToFallback(String pointsToFallback) {
		this.pointsToFallback = pointsToFallback;
	}



	public List<String> getPointsToIncludeclasses() {
		return pointsToIncludeClasses;
	}



	public void setPointsToIncludeClasses(List<String> pointsToIncludeClasses) {
		this.pointsToIncludeClasses = pointsToIncludeClasses;
	}



	public List<String> getPointsToExcludeClasses() {
		return pointsToExcludeClasses;
	}



	public void setPointsToExcludeClasses(List<String> pointsToExcludeClasses) {
		this.pointsToExcludeClasses = pointsToExcludeClasses;
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



	public String getStatisticsFile() {
		return statisticsFile;
	}



	public void setStatisticsFile(String statistics) {
		this.statisticsFile = statistics;
	}



	public String getSystemOut() {
		return systemOut;
	}



	public void setSystemOut(String systemOut) {
		this.systemOut = systemOut;
	}



	public String getComputeChops() {
		return computeChops;
	}



	public void setComputeChops(String computeChops) {
		this.computeChops = computeChops;
	}



	public String getObjectSensitiveness() {
		return objectSensitiveness;
	}



	public void setObjectSensitiveness(String objectSensitiveness) {
		this.objectSensitiveness = objectSensitiveness;
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
