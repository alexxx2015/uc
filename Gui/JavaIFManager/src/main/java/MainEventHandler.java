import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import console.DialogConsoleEventHandler;
import edu.kit.joana.flowanalyzer.AnalysisXMLReader;
import edu.kit.joana.flowanalyzer.AnalysisXMLReader.TestRunData;
import edu.kit.joana.flowanalyzer.MyObjSensReportTestRuns;
import edu.kit.joana.flowanalyzer.ObjContextAnalysis;
import edu.tum.uc.jvm.utility.ConfigProperties;

public class MainEventHandler {
	@FXML
	private Label labProgram;
	private File programFile;
	@FXML
	private Label labReport;
	@FXML
	private Button btnProgram;
	@FXML
	private Pane ch1;
	@FXML
	private Node ucConfig1;
	@FXML
	private Node ucConfig2;
	@FXML
	private ToggleButton btnInstExec;
	@FXML
	private Label labSnSFile;
	@FXML
	private CheckBox cbMultithreaded;
	@FXML
	private CheckBox cbChops;
	@FXML
	private CheckBox cbSystemout;
	@FXML
	private CheckBox cbObjectsens;
	@FXML
	private CheckBox cbIndirectflows;
	@FXML
	private TextField tfMode;
	@FXML
	private TextField tfClasspath;
	@FXML
	private TextField tfThirdPartyLibs;
	@FXML
	private TextField tfEntrypoint;
	@FXML
	private TextField tfSDGFile;
	@FXML
	private TextField tfCGFile;
	@FXML
	private TextField tfReportfile;
	@FXML
	private TextField tfPtPolicy;
	@FXML
	private TextField tfStatisticsfile;
	@FXML
	private TextField tfLogfile;
	@FXML
	private TextField tfPtFallback;
	@FXML
	private TextArea taPtIncludeClasses;
	@FXML
	private TextArea taPtExcludeClasses;
	@FXML
	private TextField tfAnalysisname;
	@FXML
	private Label labStatInfo;
	@FXML
	private TitledPane tpStaticAnalysis;
	@FXML
	private TitledPane tpRunProgAnalysis;
	@FXML
	private TitledPane tpRuntimeMonitoring;
	@FXML
	private Button btnRunExec;
	@FXML
	private Button btnBlacklist;
	@FXML
	private Button btnInstClasspath;
	@FXML
	private TextField tfBlacklist;
	@FXML
	private TextField tfInstClasspath;
	@FXML
	private TextField tfAnalysisReport;
	@FXML
	private TextField tfUcConfigStatistics;
	@FXML
	private Button btnLoadUcConfig;
	@FXML
	private TextField tfPipHost;
	@FXML
	private TextField tfPipPort;
	@FXML
	private TextField tfPdpHost;
	@FXML
	private TextField tfPdpPort;
	@FXML
	private	TextField tfPmpHost;
	@FXML
	private TextField tfPmpPort;
	@FXML
	private CheckBox cbInst;
	@FXML
	private CheckBox cbEnf;
	@FXML
	private CheckBox cbT1;
	@FXML
	private CheckBox cbT2;
	@FXML
	private CheckBox cbT3;
	@FXML
	private CheckBox cbT4;
	@FXML
	private CheckBox cbT5;

	private DialogEventHandler dm;

	public MainEventHandler() {
		// TODO Auto-generated constructor stub
		dm = new DialogEventHandler();
	}

	@FXML
	protected void handleBrowseProgram(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select path to app under analysis");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		programFile = fc.showOpenDialog(btnProgram.getScene().getWindow());
		if (programFile != null) {
			labProgram.setText(programFile.getAbsolutePath());
			this.tpRunProgAnalysis.setDisable(false);
			this.tpRuntimeMonitoring.setDisable(false);
			this.tpStaticAnalysis.setDisable(false);
		}
	}

	@FXML
	protected void handleBrowseReport(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select report of app under analysis");
		File f = fc.showSaveDialog(btnProgram.getScene().getWindow());
		if (f != null) {
			tfAnalysisReport.setText(f.getAbsolutePath());
		}
	}

	@FXML
	protected void handleRunExec(ActionEvent event) {
		if (programFile == null) {
			dm.showDialog(btnProgram.getScene().getWindow());
		} else {
			
			if(Main.jPipCommunicationManager == null){
				Main.jPipCommunicationManager = new JavaPipCommunicationManager();
			}
//			Main.jPipCommunicationManager.connect();
			
			ProcessBuilder pb = new ProcessBuilder();
//			pb.command("/usr/bin/xterm","-e","java","-jar",programFile.getAbsolutePath());
			if(this.btnInstExec.isSelected()){
				//Check if workind directory exist
				String workingdir = "/workingdir";
				if(this.getClass().getResource(workingdir) == null){
					Utility.moveJars2Workingdir(System.getProperty("user.dir")+workingdir);
				}
				this.generateUcConfigFile(workingdir);
				pb.command("java","-javaagent:uc-java-pep-0.0.1-SNAPSHOT.jar","-Xbootclasspath/a:"+Utility.generateClassPath(workingdir),"-classpath",Utility.generateClassPath(workingdir),"-jar",programFile.getAbsolutePath());
			}else{
				pb.command("java","-jar",programFile.getAbsolutePath());
			}
			pb.redirectInput();
			pb.redirectOutput();
			try {
				Process p = pb.start();
				Field f = p.getClass().getDeclaredField("pid");//On windows this field is called "handle"
				f.setAccessible(true);
				int pid = f.getInt(p);
//				Main.jPipCommunicationManager.getClient().addListener(JavaPipCommunicationManager.SERVER_HOST, JavaPipCommunicationManager.SERVER_PORT, "JavaPip"," ");// "PID:"+pid);
				DialogConsoleEventHandler dcev = DialogConsoleEventHandler.showDialog(btnRunExec.getScene().getWindow());
				dcev.start(p);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void generateUcConfigFile(String workingdir){
		URL url = MainEventHandler.class.getResource(workingdir);
		String filename = url.getFile()+"/uc.config";
		File f = new File(filename);
		try {
			StringBuilder content = new StringBuilder();
			content.append("PIP_HOST="+tfPipHost.getText()+"\n");
			content.append("PIP_PORT="+tfPipPort.getText()+"\n");
			content.append("PDP_HOST="+tfPdpHost.getText()+"\n");
			content.append("PDP_PORT="+tfPdpPort.getText()+"\n");
			content.append("PMP_HOST="+tfPmpHost.getText()+"\n");
			content.append("PMP_PORT="+tfPmpPort.getText()+"\n");
			content.append("ANALYSIS_REPORT="+tfAnalysisReport.getText()+"\n");
			content.append("INSTRUMENTED_CLASS_PATH="+tfInstClasspath.getText()+"\n");
			content.append("ENFORCEMENT="+cbEnf.isSelected()+"\n");
			content.append("BLACKLIST="+tfBlacklist.getText()+"\n");
			content.append("INSTRUMENTATION="+cbInst.isSelected()+"\n");
			content.append("STATISTICS="+tfStatisticsfile.getText()+"\n");
			content.append("TIMER_T1="+cbT1.isSelected());
			content.append("TIMER_T2="+cbT2.isSelected());
			content.append("TIMER_T3="+cbT3.isSelected());
			content.append("TIMER_T4="+cbT4.isSelected());
			content.append("TIMER_T5="+cbT5.isSelected());
			
			FileWriter fw = new FileWriter(f);
			fw.write(content.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML
	protected void handleBrowseBlacklist(ActionEvent event){
		FileChooser fc = new FileChooser();
		fc.setTitle("Select the blacklistfile");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showSaveDialog(btnBlacklist.getScene().getWindow());
		if (file != null) {
			tfBlacklist.setText(file.getAbsolutePath());
		}
	}
	
	@FXML
	protected void handleBrowseStatistics(ActionEvent event){
		FileChooser fc = new FileChooser();
		fc.setTitle("Select the statistics-file");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showSaveDialog(btnBlacklist.getScene().getWindow());
		if (file != null) {
			tfUcConfigStatistics.setText(file.getAbsolutePath());
		}
	}
	
	@FXML
	protected void handleBrowseInstClasspath(ActionEvent event){
		DirectoryChooser fc = new DirectoryChooser();
		fc.setTitle("Select the direcotry for instrumented classes");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showDialog(btnBlacklist.getScene().getWindow());
		if (file != null) {
			tfInstClasspath.setText(file.getAbsolutePath());
		}
	}

	@FXML
	protected void turnOnOffUcConfig(ActionEvent event) {
		if (btnInstExec.isSelected()) {
			ucConfig1.setDisable(false);
			ucConfig2.setDisable(false);
			btnLoadUcConfig.setDisable(false);
		} else {
			ucConfig1.setDisable(true);
			ucConfig2.setDisable(true);
			btnLoadUcConfig.setDisable(true);
		}
	}

	@FXML
	protected void handleBrowseLoadStatConf(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Load static analysis");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showOpenDialog(btnProgram.getScene().getWindow());
		if (file != null) {
			System.out.println(file.getName());
			try {
				AnalysisXMLReader reader = new AnalysisXMLReader(
						file.getAbsolutePath());
				TestRunData[] trd = reader.getTestRunData();
				for (TestRunData t : trd) {
					cbMultithreaded.setSelected(t.multiThreaded);
					cbChops.setSelected(t.computeChops);
					cbSystemout.setSelected(t.systemOut);
					if (t.oca instanceof ObjContextAnalysis)
						cbObjectsens.setSelected(true);
					cbIndirectflows.setSelected(t.ignoreIndirectFlows);
					tfAnalysisname.setText(t.name);
					tfMode.setText(t.mode.name());
					tfClasspath.setText(this.programFile.getAbsolutePath()
							+ "::" + t.classpath);
					tfThirdPartyLibs.setText(t.thirdPartyLibs);
					tfEntrypoint.setText(t.entryPoint);
					tfSDGFile.setText(t.sdgFile);
					tfCGFile.setText(t.cgFile);
					tfReportfile.setText(t.reportFile);
					tfStatisticsfile.setText(t.statsFile);
					tfLogfile.setText(t.logFile);
					tfPtPolicy.setText(t.pointsTo.name());
					tfPtFallback.setText(String.valueOf(t.ptsFallBack));
					Set<String> s1 = t.ptsToInclusionPatterns;
					Iterator<String> s1It = s1.iterator();
					String includeClasses = "";
					while (s1It.hasNext()) {
						includeClasses += s1It.next() + " \n";
					}
					taPtIncludeClasses.setText(includeClasses);
					Set<String> s2 = t.ptsToExclusionPatterns;
					Iterator<String> s2It = s2.iterator();
					String excludeClasses = "";
					while (s2It.hasNext()) {
						excludeClasses += s2It.next() + "\n";
					}
					taPtExcludeClasses.setText(excludeClasses);
					ConfigurationFileBuilder cfb = new ConfigurationFileBuilder();
					File f = cfb.generateSinkSourceFile(t.getSinks(),
							t.getSources(), this.programFile);
					labSnSFile.setText(f.getAbsolutePath());
					break;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@FXML
	protected void handleBrowseSDGFile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select SDGFile");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showSaveDialog(btnProgram.getScene().getWindow());
		if (file != null) {
			tfSDGFile.setText(file.getAbsolutePath());
		}
	}

	@FXML
	protected void handleBrowseCGFile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select CGFile");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showSaveDialog(btnProgram.getScene().getWindow());
		if (file != null) {
			tfCGFile.setText(file.getAbsolutePath());
		}
	}

	@FXML
	protected void handleBrowseReportfile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Report File");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showSaveDialog(btnProgram.getScene().getWindow());
		if (file != null) {
			tfReportfile.setText(file.getAbsolutePath());
		}
	}

	@FXML
	protected void handleBrowseSnSFile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select the Sink and Source File");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showOpenDialog(btnProgram.getScene().getWindow());
		if (file != null) {
			labSnSFile.setText(file.getName());
		}
	}

	@FXML
	protected void handleBrowseStatisticsfile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select statistics file");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showSaveDialog(btnProgram.getScene().getWindow());
		if (file != null) {
			tfStatisticsfile.setText(file.getName());
		}
	}

	@FXML
	protected void handleBrowseLogfile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select log file");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showSaveDialog(btnProgram.getScene().getWindow());
		if (file != null) {
			tfLogfile.setText(file.getName());
		}
	}

	@FXML
	protected void handleBrowseSaveStatConf(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Save current configuration settings");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showSaveDialog(btnProgram.getScene().getWindow());
		if (file != null) {
			this.saveConfiguration(file);
		}
	}

	private void saveConfiguration(File f) {
		ConfigurationFileBuilder cfb = new ConfigurationFileBuilder();
		cfb.setMultithreaded(cbMultithreaded.isSelected());
		cfb.setComputeChops(cbChops.isSelected());
		cfb.setSystemOut(cbSystemout.isSelected());
		cfb.setObjectsensitiveness(cbObjectsens.isSelected());
		cfb.setMode(tfMode.getText());
		cfb.setClasspath(tfClasspath.getText());
		cfb.setThirdPartyLibs(tfThirdPartyLibs.getText());
		cfb.setEntryPoint(tfEntrypoint.getText());
		cfb.setIndirectFlows(cbIndirectflows.isSelected());
		cfb.setSdgFile(tfSDGFile.getText());
		cfb.setCgFile(tfCGFile.getText());
		cfb.setReportFile(tfReportfile.getText());
		cfb.setStatisticsFile(tfStatisticsfile.getText());
		cfb.setLogfile(tfLogfile.getText());
		cfb.setPtPolicy(tfPtPolicy.getText());
		cfb.setPtFallback(tfPtFallback.getText());
		cfb.setPtIncludeClasses(taPtIncludeClasses.getText());
		cfb.setAnalysisname(tfAnalysisname.getText());
		cfb.setSnsFile(labSnSFile.getText());
		String report = cfb.generateReport();
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(report);
			fw.close();
			labStatInfo.setText("Configuration saved");
			FadeTransition ft = new FadeTransition(Duration.millis(1000),
					labStatInfo);
			ft.setFromValue(1.0);
			ft.setToValue(0.0);
			ft.setAutoReverse(true);
			ft.play();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	protected void browseHandleLoadUcConfig(ActionEvent event){
		FileChooser fc = new FileChooser();
		fc.setTitle("Select the blacklistfile");
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		File file = fc.showOpenDialog(btnBlacklist.getScene().getWindow());
		if (file != null) {
			ConfigProperties.setConfigFile(file.getAbsolutePath());
			tfPdpHost.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.PDP_HOST));
			tfPdpPort.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.PDP_PORT));
			tfPipHost.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.PIP_HOST));
			tfPipPort.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.PIP_PORT));
			tfPmpHost.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.PMP_HOST));
			tfPmpPort.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.PMP_PORT));
			tfAnalysisReport.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.ANALYSIS_REPORT));
			tfBlacklist.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.BLACKLIST));
			tfInstClasspath.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.INSTRUMENTED_CLASS_PATH));
			tfUcConfigStatistics.setText(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.STATISTICS));
			cbInst.setSelected(new Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.INSTRUMENTATION)));
			cbEnf.setSelected(new Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.ENFORCEMENT)));
			cbT1.setSelected(new Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.TIMER_T1)));
			cbT2.setSelected(new Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.TIMER_T2)));
			cbT3.setSelected(new Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.TIMER_T3)));
			cbT4.setSelected(new Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.TIMER_T4)));
			cbT5.setSelected(new Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.TIMER_T5)));
		}
	}

	@FXML
	protected void runStaticAnalysis(ActionEvent event) {
		if (programFile == null) {
			dm.showDialog(btnProgram.getScene().getWindow());
		} else {
			final File file = new File("./analysis_"
					+ this.programFile.getName() + ".xml");
			this.saveConfiguration(file);
			if (file != null) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							MyObjSensReportTestRuns.main(new String[] { file
									.getAbsolutePath() });
						} catch (ClassHierarchyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsoundGraphException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (CancelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TransformerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				t.start();

			}
		}
	}
}

// protected void handleNativeExec(ActionEvent event){
// ProcessBuilder pb = new ProcessBuilder();
// pb.command("/bin/sh","-c","java -jar "+programFile.getAbsolutePath());
// try {
// Process p = pb.start();
// InputStream shellIn = p.getInputStream();
// BufferedReader reader = new BufferedReader(new
// InputStreamReader(shellIn));statisticsFile
// String line;
// while((line = reader.readLine()) != null){
// System.out.println(line);
// }
//
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }

// @FXML
// protected void handleInstExec(ActionEvent event) {
// FileChooser fc = new FileChooser();
// fc.setTitle("Select uc.config");
// fc.setInitialDirectory(new File("/home/alex"));
// File configFile = fc.showOpenDialog(btnProgram.getScene().getWindow());
// if (configFile != null) {
// labProgram.setText(programFile.getName());
// }
//
// ProcessBuilder pb = new ProcessBuilder();
// pb.command(
// "/usr/bin/xterm",
// "-e",
// "java -Xdebug -javaagent:uc-java-pep-0.0.1-SNAPSHOT.jar -Xbootclasspath/a:"
// + generateClassPath() + " -classpath "
// + generateClassPath() + " -jar "
// + programFile.getAbsolutePath());
// try {
// Process p = pb.start();
// // InputStream shellIn = p.getInputStream();
// // BufferedReader reader = new BufferedReader(new
// // InputStreamReader(shellIn));
// // String line;
// // while((line = reader.readLine()) != null){
// // System.out.println(line);
// // }
// // System.out.println(p.exitValue());
//
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }