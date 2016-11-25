package de.tum.in.i22.ucwebmanager.analysis;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tum.in.i22.ucwebmanager.Configuration;
import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.StaticAnalysisConfig;
import de.tum.in.i22.ucwebmanager.DB.StaticAnalysisConfigDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;

public class DocBuilder {
	public static final String TAG_ANALYSISES = "analysises";
	public static final String TAG_ANALYSIS = "analysis";
	public static final String TAG_MODE = "mode";
	public static final String TAG_CLASSPATH = "classpath";
	public static final String TAG_THIRDPARTYLIBS = "thirdPartyLibs";
	public static final String TAG_STUBS = "stubs";
	public static final String TAG_ENTRYPOINT = "entrypoint";
	public static final String TAG_POINTSTO = "points-to";
	public static final String TAG_INCLUDECLASSES = "include-classes";
	public static final String TAG_EXCLUDECLASSES = "exclude-classes";
	public static final String TAG_IGNOREINDIRECTFLOWS = "ignoreIndirectFlows";
	public static final String TAG_OMITIFC = "omitIFC";
	public static final String TAG_MULTITHREADED = "multithreaded";
	public static final String TAG_PRUNINGPOLICY = "pruningPolicy";
	public static final String TAG_SDGFILE = "sdgfile";
	public static final String TAG_CGFILE = "cgfile";
	public static final String TAG_REPORTFILE = "reportfile";
	public static final String TAG_STATISTICS = "statistics";
	public static final String TAG_LOGFILE = "logFile";
	public static final String TAG_COMPUTECHOPS = "computeChops";
	public static final String TAG_SYSTEMOUT = "systemout";
	public static final String TAG_OBJECTSENSITIVENES = "objectsensitivenes";
	public static final String TAG_SOURCESANDSINKS = "sourcesandsinks";
	public static final String TAG_SOURCE = "source";
	public static final String TAG_SINK = "sink";
	public static final String TAG_FILE = "file";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_POLICY = "policy";
	public static final String ATTR_FALLBACK = "fallback";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_SELECTOR = "selector";
	public static final String ATTR_PARAMS = "params";
	public static final String ATTR_INCLUDESUBCLASSES = "includeSubClasses";
	public static final String ATTR_INDIRECTCALLS = "indirectCalls";

	public String generateAnalysisConfigFile(AnalysisData data, App app, String analysisName) {
//		String analysisName = "/Static_analysis";
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String stringDate = dateFormat.format(date);
		File analysisOutput = new File(FileUtil.getPathOutput(app.getHashCode()) + "/" + stringDate);
		analysisOutput.mkdirs();
		String pathConfigOfApp = FileUtil.getPathConfig(app.getHashCode());
		File directory = new File(pathConfigOfApp);
		String configName = "";
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(DocBuilder.TAG_ANALYSISES);
			doc.appendChild(rootElement);

			// staff elements
			Element analysis = doc.createElement(DocBuilder.TAG_ANALYSIS);
			rootElement.appendChild(analysis);

			// set attribute to staff element
			Attr attrname = doc.createAttribute(DocBuilder.ATTR_NAME);
			attrname.setValue(".."+FileUtil.Dir.JOANAOUTPUT.getDir() + "/" + stringDate + "/" + analysisName);	
			analysis.setAttributeNode(attrname);

			// mode elements
			Element mode = doc.createElement(DocBuilder.TAG_MODE);

			analysis.appendChild(mode);

			Attr attrvalue = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrvalue.setValue(data.getMode());
			mode.setAttributeNode(attrvalue);

			List<String> listtabledata = data.getClasspath();
			String strClassPath = "";
			for (int i = 0; i < listtabledata.size(); i++) {
				if (i != 0)
					strClassPath += Configuration.LOCAL_FILE_SEPARATOR + listtabledata.get(i);
				else
					strClassPath += listtabledata.get(i);
			}
			// classpath
//			String strClasspath = strTableData;
//			String applicationname =   "./:" + strClasspath;
//			String applicationname = strClasspath;
			Element classpath = doc.createElement(DocBuilder.TAG_CLASSPATH);

			analysis.appendChild(classpath);

			Attr attrvaluepath = doc.createAttribute(DocBuilder.ATTR_VALUE);

			attrvaluepath.setValue(strClassPath);
			classpath.setAttributeNode(attrvaluepath);
			// temp
			// thirdpartylib elements
			Element thirdPartyLibs = doc
					.createElement(DocBuilder.TAG_THIRDPARTYLIBS);

			analysis.appendChild(thirdPartyLibs);

			Attr attrvaluetpl = doc.createAttribute(DocBuilder.ATTR_VALUE);
			listtabledata = data.getThirdPartyLibs();
			String strThirdPartyLibrary = "";
			for (int i = 0; i < listtabledata.size(); i++) {
				if (i != 0)
					strThirdPartyLibrary += Configuration.LOCAL_FILE_SEPARATOR + listtabledata.get(i);
				else
					strThirdPartyLibrary += listtabledata.get(i);
			}
			attrvaluetpl.setValue(strThirdPartyLibrary);
			thirdPartyLibs.setAttributeNode(attrvaluetpl);

			// stubs elements
			Element stubs = doc.createElement(DocBuilder.TAG_STUBS);
			// stubs.appendChild(doc.createTextNode("100000"));
			analysis.appendChild(stubs);

			Attr attrvaluestub = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrvaluestub.setValue(data.getStubs());
			stubs.setAttributeNode(attrvaluestub);

			Element entrypoint = doc.createElement(DocBuilder.TAG_ENTRYPOINT);

			analysis.appendChild(entrypoint);

			Attr attrvalueentry = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrvalueentry.setValue(data.getEntrypoint());
			entrypoint.setAttributeNode(attrvalueentry);
			Element pointsto = doc.createElement(DocBuilder.TAG_POINTSTO);

			analysis.appendChild(pointsto);
			Attr attrpolicy = doc.createAttribute(DocBuilder.ATTR_POLICY);

			attrpolicy.setValue(data.getPointsToPolicy());
			pointsto.setAttributeNode(attrpolicy);
			Attr attrfallback = doc.createAttribute(DocBuilder.ATTR_FALLBACK);
			attrfallback.setValue(data.getPointsToFallback());
			pointsto.setAttributeNode(attrfallback);
			// read data from points to include and exclude
			listtabledata = data.getPointsToIncludeclasses();
			strClassPath = "";

			for (int i = 0; i < listtabledata.size(); i++) {
				Element includeclass = doc
						.createElement(DocBuilder.TAG_INCLUDECLASSES);
				pointsto.appendChild(includeclass);
				Attr attriincludeclass = doc
						.createAttribute(DocBuilder.ATTR_VALUE);
				attriincludeclass.setValue(listtabledata.get(i));
				includeclass.setAttributeNode(attriincludeclass);
			}

			listtabledata = data.getPointsToExcludeClasses();
			strClassPath = "";
			for (int i = 0; i < listtabledata.size(); i++) {
				Element excludeclass = doc
						.createElement(DocBuilder.TAG_EXCLUDECLASSES);
				pointsto.appendChild(excludeclass);
				Attr attriexcludeclass = doc
						.createAttribute(DocBuilder.ATTR_VALUE);
				attriexcludeclass.setValue(listtabledata.get(i));
				excludeclass.setAttributeNode(attriexcludeclass);
			}
			// ends

			Element ignoreIndirectFlows = doc
					.createElement(DocBuilder.TAG_IGNOREINDIRECTFLOWS);
			analysis.appendChild(ignoreIndirectFlows);
			Attr attrignoreindirect = doc
					.createAttribute(DocBuilder.ATTR_VALUE);
			attrignoreindirect.setValue(data.getIgnoreIndirectFlows());
			ignoreIndirectFlows.setAttributeNode(attrignoreindirect);

			Element omitIFC = doc.createElement(DocBuilder.TAG_OMITIFC);
			analysis.appendChild(omitIFC);
			Attr attrOmitIFC = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrOmitIFC.setValue(data.getOmitIFC());
			omitIFC.setAttributeNode(attrOmitIFC);
			
			Element multithreaded = doc
					.createElement(DocBuilder.TAG_MULTITHREADED);

			analysis.appendChild(multithreaded);
			Attr attrmultith = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrmultith.setValue(data.getMultiThreaded());
			multithreaded.setAttributeNode(attrmultith);
			
			Element pruningPolicy = doc.createElement(DocBuilder.TAG_PRUNINGPOLICY);
			analysis.appendChild(pruningPolicy);
			Attr attrPrunningPolicy = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrPrunningPolicy.setValue(data.getPruningPolicy());
			pruningPolicy.setAttributeNode(attrPrunningPolicy);
			
			Element sdgfile = doc.createElement(DocBuilder.TAG_SDGFILE);

			analysis.appendChild(sdgfile);
			Attr attrsdg = doc.createAttribute(DocBuilder.ATTR_VALUE);

			sdgfile.setAttributeNode(attrsdg);

			Element cgfile = doc.createElement(DocBuilder.TAG_CGFILE);

			analysis.appendChild(cgfile);
			Attr attrcgfile = doc.createAttribute(DocBuilder.ATTR_VALUE);

			cgfile.setAttributeNode(attrcgfile);

			Element reportfile = doc.createElement(DocBuilder.TAG_REPORTFILE);	
			Element logfile = doc.createElement(DocBuilder.TAG_LOGFILE);
			analysis.appendChild(reportfile);
			analysis.appendChild(logfile);
			Attr attrreport = doc.createAttribute(DocBuilder.ATTR_VALUE);
			Attr attrlog = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrreport.setValue(data.getReportFile());
			attrlog.setValue(data.getLogFile());
			attrcgfile.setValue(data.getCgFile());
			attrsdg.setValue(data.getSdgFile());
			reportfile.setAttributeNode(attrreport);
			logfile.setAttributeNode(attrlog);
			
			Element statistics = doc.createElement(DocBuilder.TAG_STATISTICS);
			analysis.appendChild(statistics);
			Attr attrStatistics = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrStatistics.setValue(data.getStatisticsFile());
			statistics.setAttributeNode(attrStatistics);
			
			Element computeChops = doc.createElement(DocBuilder.TAG_COMPUTECHOPS);

			analysis.appendChild(computeChops);
			Attr attrcompute = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrcompute.setValue(data.getComputeChops());
			computeChops.setAttributeNode(attrcompute);

			Element systemout = doc.createElement(DocBuilder.TAG_SYSTEMOUT);

			analysis.appendChild(systemout);
			Attr attrsystemout = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrsystemout.setValue(data.getSystemOut());
			systemout.setAttributeNode(attrsystemout);

			Element objectsensitivenes = doc
					.createElement(DocBuilder.TAG_OBJECTSENSITIVENES);

			analysis.appendChild(objectsensitivenes);
			Attr attrsensitive = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrsensitive.setValue(data.getObjectSensitiveness());
			objectsensitivenes.setAttributeNode(attrsensitive);

			Element sourcesandsinks = doc.createElement(DocBuilder.TAG_SOURCESANDSINKS);
			analysis.appendChild(sourcesandsinks);
			// read tablesourcensinks data
			List<AnalysisData.SourcesSinks> listdatasourcensinks = data.getSourcesSinks();

			for (int i = 0; i < listdatasourcensinks.size(); i++) {
				if (listdatasourcensinks.get(i).getType().equals(AnalysisData.TYPES.SOURCE)) {
					Element source = doc.createElement(DocBuilder.TAG_SOURCE);
					sourcesandsinks.appendChild(source);
					Attr classvalue = doc.createAttribute(DocBuilder.ATTR_CLASS);
					classvalue.setValue(listdatasourcensinks.get(i).getClazz());
					source.setAttributeNode(classvalue);
					Attr selectorvalue = doc.createAttribute(DocBuilder.ATTR_SELECTOR);
					selectorvalue.setValue(listdatasourcensinks.get(i).getSelector());
					source.setAttributeNode(selectorvalue);
					Attr paramsvalue = doc.createAttribute(DocBuilder.ATTR_PARAMS);
					paramsvalue.setValue(listdatasourcensinks.get(i).getParams());
					source.setAttributeNode(paramsvalue);
					if (!"".equals(listdatasourcensinks.get(i).getIncludeSubClasses())) {
						Attr includeclassvalue = doc
								.createAttribute(DocBuilder.ATTR_INCLUDESUBCLASSES);
						includeclassvalue.setValue(listdatasourcensinks.get(i).getIncludeSubClasses());
						source.setAttributeNode(includeclassvalue);
					}
					if (!"".equals(listdatasourcensinks.get(i).getIndirectCalls())) {
						Attr indirectcallsvalue = doc
								.createAttribute(DocBuilder.ATTR_INDIRECTCALLS);
						indirectcallsvalue.setValue(listdatasourcensinks.get(i).getIndirectCalls());
						source.setAttributeNode(indirectcallsvalue);
					}
				} else if (listdatasourcensinks.get(i).getType().equals(AnalysisData.TYPES.SINK)) {
					Element sink = doc.createElement(DocBuilder.TAG_SINK);
					sourcesandsinks.appendChild(sink);
					Attr classvalue = doc.createAttribute(DocBuilder.ATTR_CLASS);
					classvalue.setValue(listdatasourcensinks.get(i).getClazz());
					sink.setAttributeNode(classvalue);
					Attr selectorvalue = doc.createAttribute(DocBuilder.ATTR_SELECTOR);
					selectorvalue.setValue(listdatasourcensinks.get(i).getSelector());
					sink.setAttributeNode(selectorvalue);
					Attr paramsvalue = doc.createAttribute(DocBuilder.ATTR_PARAMS);
					paramsvalue.setValue(listdatasourcensinks.get(i).getParams());
					sink.setAttributeNode(paramsvalue);
					Attr includeclassvalue = doc
							.createAttribute(DocBuilder.ATTR_INCLUDESUBCLASSES);
					includeclassvalue.setValue(listdatasourcensinks.get(i).getIncludeSubClasses());
					sink.setAttributeNode(includeclassvalue);
					Attr indirectcallsvalue = doc
							.createAttribute(DocBuilder.ATTR_INDIRECTCALLS);
					indirectcallsvalue.setValue(listdatasourcensinks.get(i).getIndirectCalls());
					sink.setAttributeNode(indirectcallsvalue);

				}
			}
			List<String> list = data.getSourcesSinksFiles();
			if (list.size() > 0) {
				for (String s : list){
					Element Fileelement = doc.createElement(DocBuilder.TAG_FILE);
					sourcesandsinks.appendChild(Fileelement);
					Attr attrFileSave = doc.createAttribute(DocBuilder.ATTR_VALUE);
					attrFileSave.setValue(s);
					Fileelement.setAttributeNode(attrFileSave);
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			// add linebreak into xml file
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			configName = stringDate + ".config.xml";
			String configDir = directory + System.getProperty("file.separator") + configName;
			File dest = new File(
//					Configuration.WebAppRoot + System.getProperty("file.separator") + appName + System.getProperty("file.separator")+ data.getAnalysisName() + System.getProperty("file.separator")+appName + ".xml");
					configDir);
			StreamResult result = new StreamResult(dest);

			transformer.transform(source, result);
			StaticAnalysisConfig staticConfig = new StaticAnalysisConfig(configName, app.getId());
			try {
				// save config file to database
				StaticAnalysisConfigDAO.saveToDB(staticConfig);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			System.out.println("File saved! "+dest.getAbsolutePath());
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		return configName;
	}
	
public static AnalysisData readConfigFromFile(String configFolderPath, String reportFileName) {
		
		if ("".equals(configFolderPath)) return null;
		
		AnalysisData data=null;
		
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			File configFile;
			if (!"".equals(reportFileName))
				configFile = new File(configFolderPath+File.separator+reportFileName);
			else {
				//Open last modified config file
				File configFolder = new File(configFolderPath);
				List<File> configFilesList = Arrays.asList(configFolder.listFiles());
				if (configFilesList.isEmpty())
					return null;
				
				//Latest file first
				Collections.sort(configFilesList, 
						(f1,f2) -> new Long(f2.lastModified()).compareTo(f1.lastModified()));
				
				configFile = configFilesList.get(0);
				
			}
			
			Document doc = docBuilder.parse(configFile);
			doc.getDocumentElement().normalize();

			data = new AnalysisData();
			
			//Only one ANALYSIS_TAG per report
			if (doc.getElementsByTagName(TAG_ANALYSIS).item(0).getNodeType() == Node.ELEMENT_NODE) {
				Element analysisElement = (Element) doc.getElementsByTagName(TAG_ANALYSIS).item(0);
				
				//get the last string separated by "/"
				String pathAsArrayString[] = analysisElement.getAttribute(ATTR_NAME).split(File.separator);
				data.setAnalysisName(pathAsArrayString[pathAsArrayString.length - 1]);
				
				Element mode = (Element) analysisElement.getElementsByTagName(TAG_MODE).item(0);
				data.setMode(mode.getAttribute(ATTR_VALUE));
				
				//Read classPath tag
				Element classPath = (Element) analysisElement.getElementsByTagName(TAG_CLASSPATH).item(0);
				List<String> classPathsList = new ArrayList<String>();
				if (classPath!=null)
					for (String s : classPath.getAttribute(ATTR_VALUE).split(Configuration.LOCAL_FILE_SEPARATOR))
						classPathsList.add(s);
				data.setClasspath(classPathsList);
				
				//Read thirdPartyLibraries tag
				Element thirdPartyLibraries = (Element) analysisElement.getElementsByTagName(TAG_THIRDPARTYLIBS).item(0);
				List<String> thirdPartyLibrariesList = new ArrayList<String>();
				if (thirdPartyLibraries!=null)
					for (String s : thirdPartyLibraries.getAttribute(ATTR_VALUE).split(Configuration.LOCAL_FILE_SEPARATOR))
						thirdPartyLibrariesList.add(s);
				data.setThirdPartyLibs(thirdPartyLibrariesList);
				
				//Read stubs
				Element stubs = (Element) analysisElement.getElementsByTagName(TAG_STUBS).item(0);
				if (stubs!=null)
					data.setStubs(stubs.getAttribute(ATTR_VALUE));
				
				//Read entry point tag
				Element entryPoint = (Element) analysisElement.getElementsByTagName(TAG_ENTRYPOINT).item(0);
				if (entryPoint!=null)
					data.setEntrypoint(entryPoint.getAttribute(ATTR_VALUE));
				
				//Read pruning policy
				Element pruningPolicy = (Element) analysisElement.getElementsByTagName(TAG_PRUNINGPOLICY).item(0);
				if (pruningPolicy!=null)
					data.setPruningPolicy(pruningPolicy.getAttribute(ATTR_VALUE));
				
				//Read sdg file tag
				Element sdgFile = (Element) analysisElement.getElementsByTagName(TAG_SDGFILE).item(0);
				if (sdgFile!=null)
					data.setSdgFile(sdgFile.getAttribute(ATTR_VALUE));
				
				//Read cg file tag
				Element cgFile = (Element) analysisElement.getElementsByTagName(TAG_CGFILE).item(0);
				if (cgFile!=null)
					data.setCgFile(cgFile.getAttribute(ATTR_VALUE));
				
				//Read report file tag
				Element reportFile = (Element) analysisElement.getElementsByTagName(TAG_REPORTFILE).item(0);
				if (reportFile!=null)
					data.setReportFile(reportFile.getAttribute(ATTR_VALUE));
				
				//Read statistics file tag
				Element statisticsFile = (Element) analysisElement.getElementsByTagName(TAG_STATISTICS).item(0);
				if (statisticsFile!=null)
					data.setStatisticsFile(statisticsFile.getAttribute(ATTR_VALUE));
				
				//Read log file tag
				Element logFile = (Element) analysisElement.getElementsByTagName(TAG_LOGFILE).item(0);
				if (logFile!=null)
					data.setLogFile(logFile.getAttribute(ATTR_VALUE));
				
				//Read points to policy and fallback, include and exclude classes
				Element pointsTo = (Element) analysisElement.getElementsByTagName(TAG_POINTSTO).item(0);
				List<String> pointsToIncludeList = new ArrayList<String>();
				List<String> pointsToExcludeList = new ArrayList<String>();
				if (pointsTo!=null) {
					data.setPointsToPolicy(pointsTo.getAttribute(ATTR_POLICY));
					data.setPointsToFallback(pointsTo.getAttribute(ATTR_FALLBACK));
					
					//Read include classes as subchilds of points-to
					NodeList includeClasses = pointsTo.getElementsByTagName(TAG_INCLUDECLASSES);
					for (int i=0; i<includeClasses.getLength(); i++) {
						Element includeClass = (Element) includeClasses.item(i);
						pointsToIncludeList.add(includeClass.getAttribute(ATTR_VALUE));
					}
					
					//Read exclude classes as subchilds of points-to
					NodeList excludeClasses = pointsTo.getElementsByTagName(TAG_EXCLUDECLASSES);
					for (int i=0; i<excludeClasses.getLength(); i++) {
						Element excludeClass = (Element) excludeClasses.item(i);
						pointsToExcludeList.add(excludeClass.getAttribute(ATTR_VALUE));
					}
				}
				data.setPointsToIncludeClasses(pointsToIncludeList);
				data.setPointsToExcludeClasses(pointsToExcludeList);
		
				//Read sources and sinks
				Element sourcesAndSinks = (Element) analysisElement.getElementsByTagName(TAG_SOURCESANDSINKS).item(0);
				List<AnalysisData.SourcesSinks> listDataSourcesAndSinks = new ArrayList<AnalysisData.SourcesSinks>();
				List<String> sourcesAndSinksFiles = new ArrayList<String>();
				if (sourcesAndSinks!=null) {
					NodeList sources = sourcesAndSinks.getElementsByTagName(TAG_SOURCE);
					for (int i=0; i<sources.getLength(); i++) {
						Element sourceElement = (Element) sources.item(i);
						AnalysisData.SourcesSinks source = data.new SourcesSinks();
						source.setType(AnalysisData.TYPES.SOURCE);
						source.setClazz(sourceElement.getAttribute(ATTR_CLASS));
						source.setSelector(sourceElement.getAttribute(ATTR_SELECTOR));
						source.setParams(sourceElement.getAttribute(ATTR_PARAMS));
						source.setIncludeSubClasses(sourceElement.getAttribute(ATTR_INCLUDESUBCLASSES));
						source.setIndirectCalls(sourceElement.getAttribute(ATTR_INDIRECTCALLS));
						
					}
					NodeList sinks = sourcesAndSinks.getElementsByTagName(TAG_SINK);
					for (int i=0; i<sinks.getLength(); i++) {
						Element sinkElement = (Element) sinks.item(i);
						AnalysisData.SourcesSinks sink = data.new SourcesSinks();
						sink.setType(AnalysisData.TYPES.SINK);
						sink.setClazz(sinkElement.getAttribute(ATTR_CLASS));
						sink.setSelector(sinkElement.getAttribute(ATTR_SELECTOR));
						sink.setParams(sinkElement.getAttribute(ATTR_PARAMS));
						sink.setIncludeSubClasses(sinkElement.getAttribute(ATTR_INCLUDESUBCLASSES));
						sink.setIndirectCalls(sinkElement.getAttribute(ATTR_INDIRECTCALLS));
					}
					NodeList files = sourcesAndSinks.getElementsByTagName(TAG_FILE);
					for (int i=0; i<files.getLength(); i++) {
						Element file = (Element) files.item(i);
						sourcesAndSinksFiles.add(file.getAttribute(ATTR_VALUE));
					}
				}
				data.setSourcesSinks(listDataSourcesAndSinks);
				data.setSourcesSinksFiles(sourcesAndSinksFiles);
				
				//Read flags
				Element multiThreaded = (Element) analysisElement.getElementsByTagName(TAG_MULTITHREADED).item(0);
				if (multiThreaded!=null)
					data.setMultiThreaded(multiThreaded.getAttribute(ATTR_VALUE));
				
				Element objectSens = (Element) analysisElement.getElementsByTagName(TAG_OBJECTSENSITIVENES).item(0);
				if (objectSens!=null)
					data.setObjectSensitiveness(objectSens.getAttribute(ATTR_VALUE));
				
				Element ignoreIndirectFlows = (Element) analysisElement.getElementsByTagName(TAG_IGNOREINDIRECTFLOWS).item(0);
				if (ignoreIndirectFlows!=null)
					data.setIgnoreIndirectFlows(ignoreIndirectFlows.getAttribute(ATTR_VALUE));
				
				Element computeChops = (Element) analysisElement.getElementsByTagName(TAG_COMPUTECHOPS).item(0);
				if (computeChops!=null)
					data.setComputeChops(computeChops.getAttribute(ATTR_VALUE));
				
				Element systemOut = (Element) analysisElement.getElementsByTagName(TAG_SYSTEMOUT).item(0);
				if (systemOut!=null)
					data.setSystemOut(systemOut.getAttribute(ATTR_VALUE));
				
				Element omitIFC = (Element) analysisElement.getElementsByTagName(TAG_OMITIFC).item(0);
				if (omitIFC!=null)
					data.setOmitIFC(omitIFC.getAttribute(ATTR_VALUE));
			
			
			}
	
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return data;
		
	}
}
