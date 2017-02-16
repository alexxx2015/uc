package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.Status.Status;
import de.tum.in.i22.ucwebmanager.analysis.AnalysisData;
import de.tum.in.i22.ucwebmanager.analysis.DocBuilder;
import de.tum.in.i22.ucwebmanager.dashboard.DashboardViewType;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class StaticAnalysisView extends VerticalLayout implements View {
	private UploadHandler uploadHandler = new UploadHandler(this);
	
	ComboBox cmbListApp;
	TextField txtAnalysisName, txtAppName;
	Window subWindow;
	
	String appName, staticAnalysisPath, sourceAndSinksFile;
	int appId;
	App app;
	String strBaseFolders;

	// create global variables
	String classpath, thirdpartylib, stubs, temppath;
	String sdgvalue, cgvalue;
	String strUploadFilePathCG;
	Object selecteditemidsns;
	OutputStream output = null;

	Table  tblThirdPartyLib, tblPointsToInclude,
		   tblPointsToExclude, tblsourcensinks, tblClassPath;

	CheckBox chkMultithreaded, chkcomputechops, chkObjectsensitivenes,
			chkindirectflows, chkSystemOut,chkOmitIFC;

	TextField txtSDGFile, txtCGFile, txtReportFile, txtPointstoFallback,
			txtLogFile,txtStatistics, txtFldSnSFile;
	//TextField txtFldEntryPoint;
	ComboBox cmbmode, cmbStub, cmbPointstoPolicy,cmbPruningPolicy, cmbEntryPoint, cmbClassFiles;
	Upload uploadSDGFile, uploadCGFile;
	OptionGroup optSrcAndSinks;
	Button btnsave, btnrun, btnselectsnsFile;

	private static final String CLASSPATH_PROPERTY_ID = "ClassPath";
	private static final String THIRD_PARTY_LIBRARY_PROPERTY_ID = "ThirdPartyLibrary";
	private static final String POINTS_TO_INCLUDE_PROPERTY_ID = "Points To Include";
	private static final String POINTS_TO_EXCLUDE_PROPERTY_ID = "Points To Exclude";
	
	private static final String DEFAULT_SDG_FILE = "sdg.file";
	private static final String DEFAULT_CDG_FILE = "cdg.file";
	private static final String DEFAULT_REPORT_FILE = "report.xml";
	private static final String DEFAULT_STATISTICS_FILE = "statistics.file";
	private static final String DEFAULT_LOG_FILE = "log.file";
	
	private static final String CURRENT_FOLDER = "./";
	private static final String INDEX_CLASS_FILE_SEPARATOR = ": ";
	
	private List<String> absPathSubDirOfCode;
	private List<String> subDirectoriesOfCode;
	private List<String> jarFilesInCode;
	private List<File> classFilesInCode;

	public StaticAnalysisView() {
		//this.addStyleName("myborder");
		// try {
		// InitialiseUIComponents();
		// prop.load(inputStream);
		// output = new FileOutputStream("config.properties");
		// Staticanalysispath =
		// prop.getProperty("StaticAnalysisoutputpath");
		// Staticanalysispath = new File("").getAbsolutePath();
		// staticAnalysisPath = VaadinServlet.getCurrent().getServletContext()
		// .getRealPath("/");
		//
		// strBaseFolders = prop.getProperty("BaseFolders");
		// strUploadFilePathCG = prop.getProperty("UploadFilePath");
		// save properties to project root folder
		// prop.store(output, null);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		uploadSDGFile = new Upload("SDGFile", uploadHandler);
		uploadCGFile = new Upload("CGFile", uploadHandler);

		txtAnalysisName = new TextField("Analysis Name");
		txtAnalysisName.setWidth("100%");
		
		txtAppName = new TextField("App's Name");
		txtAppName.setWidth("100%");
		txtAppName.setValue(appName);
		cmbmode = new ComboBox("Mode");
		cmbmode.setWidth("100%");
		cmbmode.setNullSelectionAllowed(false);
		cmbmode.addItem("load");
		cmbmode.addItem("build");
		cmbmode.setValue("build");
		if (cmbmode.getValue() != null && (cmbmode.getValue() == "build")) {
			uploadSDGFile.setVisible(false);
			uploadCGFile.setVisible(false);

		} else {
			uploadSDGFile.setVisible(true);
			uploadCGFile.setVisible(true);

		}
		cmbmode.addValueChangeListener(new Property.ValueChangeListener() {
			// private static final long serialVersionUID =
			// -5188369735622627751L;

			public void valueChange(ValueChangeEvent event) {
				if (cmbmode.getValue() != null
						&& (cmbmode.getValue() == "build")) {
					uploadSDGFile.setVisible(false);
					uploadCGFile.setVisible(false);

				} else {
					uploadSDGFile.setVisible(true);
					uploadCGFile.setVisible(true);

				}
			}

		});
		
		cmbStub = new ComboBox("Stubs");
		cmbStub.setWidth("100%");
		cmbStub.setNullSelectionAllowed(false);
		cmbStub.addItem("JRE_14");
		cmbStub.addItem("JRE_15");
		cmbStub.addItem("NO_STUBS");
		cmbStub.setValue("NO_STUBS");
		
		cmbPruningPolicy = new ComboBox("Pruning Policy");
		cmbPruningPolicy.setWidth("100%");
		cmbPruningPolicy.setNullSelectionAllowed(false);
		cmbPruningPolicy.addItem("app");
		cmbPruningPolicy.addItem("off");
		cmbPruningPolicy.setValue("off");

		//txtFldEntryPoint = new TextField("Entry Point");
		//txtFldEntryPoint.setWidth("100%");
		
		cmbClassFiles = new ComboBox("Class Files");
		cmbClassFiles.setWidth("100%");
		cmbClassFiles.addValueChangeListener(event -> fillComboBoxEntryPoint(Integer.parseInt(
				cmbClassFiles.getValue().toString().split(INDEX_CLASS_FILE_SEPARATOR)[0])));
		cmbEntryPoint = new ComboBox("Entry Point");
		cmbEntryPoint.setWidth("100%");
		cmbEntryPoint.setNullSelectionAllowed(true);
		cmbEntryPoint.setTextInputAllowed(true);
		cmbEntryPoint.setNewItemsAllowed(true);
		
		//Generate class table
		tblClassPath = new Table("ClassPath");
		tblClassPath.addContainerProperty(CLASSPATH_PROPERTY_ID, ComboBox.class, "");
		Property prop = tblClassPath.getPropertyDataSource();
		if (prop!=null) System.out.println(prop.getValue().toString());
		tblClassPath.setPageLength(3);
		tblClassPath.setWidth("100%");
		tblClassPath.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					List<String> elements = new ArrayList<String>();
					if (subDirectoriesOfCode!=null && app!=null && jarFilesInCode!=null) {
						elements.addAll(subDirectoriesOfCode);
						String root = FileUtil.getPathCode(app.getHashCode());
						for (String s: jarFilesInCode)
							elements.add(s);
					}

					ComboBox cmbSubDirectories = addComboBoxToTable(tblClassPath, CLASSPATH_PROPERTY_ID,
												 					elements,"");
					cmbSubDirectories.focus();
				} else if (action.getCaption() == "Delete") {
					tblClassPath.removeItem(target);
				}
			}
		});
		
		// Generate classpath table
//		gridClassPath = new Table("ClassPath");
//		
//		gridClassPath.addContainerProperty(CLASSPATH_PROPERTY_ID, TextField.class, null);
//		gridClassPath.getPropertyDataSource();
//		gridClassPath.setPageLength(5);
//		gridClassPath.setWidth("100%");
//		gridClassPath.addItemClickListener(new ItemClickListener() {
//			public void itemClick(ItemClickEvent event) {
//				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
//					gridClassPath.select(event.getItemId());
//				}
//			}
//
//		});

//		gridClassPath.addActionHandler(new Action.Handler() {
//			public Action[] getActions(Object target, Object sender) {
//				return new Action[] { new Action("New"), new Action("Delete") };
//			}
//
//			@Override
//			public void handleAction(Action action, Object sender, Object target) {
//				if (action.getCaption() == "New") {
//					addTextFieldToTable(gridClassPath, CLASSPATH_PROPERTY_ID,"").focus();
//				} else if (action.getCaption() == "Delete") {
//					gridClassPath.removeItem(target);
//				}
//			}
//		});

		//OLD CODE
		//-----------------------
		// Generate third party library table
//		gridThirdPartyLib = new Table("Third Party Library");
//		gridThirdPartyLib.setWidth("100%");
//		gridThirdPartyLib.addContainerProperty(THIRD_PARTY_LIBRARY_PROPERTY_ID,
//				TextField.class, null);
//		gridThirdPartyLib.setPageLength(5);
//		gridThirdPartyLib.addActionHandler(new Action.Handler() {
//			public Action[] getActions(Object target, Object sender) {
//				return new Action[] { new Action("New"), new Action("Delete") };
//			}
//
//			@Override
//			public void handleAction(Action action, Object sender, Object target) {
//				if (action.getCaption() == "New") {
//					addTextFieldToTable(gridThirdPartyLib, THIRD_PARTY_LIBRARY_PROPERTY_ID, "").focus();
//				} else if (action.getCaption() == "Delete") {
//					gridThirdPartyLib.removeItem(target);
//				}
//
//			}
//		});
		//OLD CODE
		//-----------------------
		
		//NEW CODE
		//-----------------------
		
		
		tblThirdPartyLib = new Table("Third Party Library");
		tblThirdPartyLib.setWidth("100%");
		tblThirdPartyLib.addContainerProperty(THIRD_PARTY_LIBRARY_PROPERTY_ID,
											   ComboBox.class, null);
		tblThirdPartyLib.setPageLength(5);

		tblThirdPartyLib.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					ComboBox cmbJarFiles = addComboBoxToTable(tblThirdPartyLib, THIRD_PARTY_LIBRARY_PROPERTY_ID,
												 			  jarFilesInCode,"");
					cmbJarFiles.focus();
				} else if (action.getCaption() == "Delete") {
					tblThirdPartyLib.removeItem(target);
				}
			}
		});
		//NEW CODE - END
		//-----------------------

		txtSDGFile = new TextField("SDGFile");
		txtSDGFile.setWidth("100%");
		txtSDGFile.setValue(DEFAULT_SDG_FILE);

		txtCGFile = new TextField("CGFile");
		txtCGFile.setWidth("100%");
		txtCGFile.setValue(DEFAULT_CDG_FILE);

		txtReportFile = new TextField("Report File");
		txtReportFile.setWidth("100%");
		txtReportFile.setValue(DEFAULT_REPORT_FILE);

		txtStatistics = new TextField("Statistics");
		txtStatistics.setWidth("100%");
		txtStatistics.setValue(DEFAULT_STATISTICS_FILE);
		
		txtLogFile = new TextField("Log File");
		txtLogFile.setWidth("100%");
		txtLogFile.setValue(DEFAULT_LOG_FILE);

		cmbPointstoPolicy = new ComboBox("Points To Policy");
		cmbPointstoPolicy.setWidth("100%");
		cmbPointstoPolicy.setNullSelectionAllowed(false);
		cmbPointstoPolicy.addItem("RTA");
		cmbPointstoPolicy.addItem("TYPE_BASED");
		cmbPointstoPolicy.addItem("INSTANCE_BASED");
		cmbPointstoPolicy.addItem("OBJECT_SENSITIVE");
		cmbPointstoPolicy.addItem("N1_OBJECT_SENSITIVE");
		cmbPointstoPolicy.addItem("UNLIMITED_OBJECT_SENSITIVE");
		cmbPointstoPolicy.addItem("N1_CALL_STACK");
		cmbPointstoPolicy.addItem("N2_CALL_STACK");
		cmbPointstoPolicy.addItem("N3_CALL_STACK");
		cmbPointstoPolicy.setValue("RTA");

		txtPointstoFallback = new TextField("Points To Fallback");
		txtPointstoFallback.setWidth("100%");

		// Generate points-to include table
		tblPointsToInclude = new Table("Points To Include");
		tblPointsToInclude.setWidth("100%");
		tblPointsToInclude.addContainerProperty("Points To Include",
				TextField.class, null);
		tblPointsToInclude.setPageLength(5);
		tblPointsToInclude.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					TextField txtPointToInclude = addTextFieldToTable(tblPointsToInclude, 
												  POINTS_TO_INCLUDE_PROPERTY_ID, "");
					txtPointToInclude.focus();
				} else if (action.getCaption() == "Delete") {
					tblPointsToInclude.removeItem(target);
				}
			}
		});

		// genrate points-to exclude table
		tblPointsToExclude = new Table("Points To Exclude");
		tblPointsToExclude.setWidth("100%");
		tblPointsToExclude.addContainerProperty("Points To Exclude",
				TextField.class, null);
		tblPointsToExclude.setPageLength(5);
		tblPointsToExclude.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					TextField txtPointToExclude = addTextFieldToTable(tblPointsToExclude,
												  POINTS_TO_EXCLUDE_PROPERTY_ID,"");
					txtPointToExclude.focus();
				} else if (action.getCaption() == "Delete") {
					tblPointsToExclude.removeItem(target);
				}

			}
		});

		// Source and sink table
		tblsourcensinks = new Table("Source and Sinks");
		tblsourcensinks.setWidth("100%");
		tblsourcensinks.addContainerProperty("Types", TextField.class, null);
		tblsourcensinks.addContainerProperty("Classes", TextField.class, null);
		tblsourcensinks.addContainerProperty("Selector", TextField.class, null);
		tblsourcensinks.addContainerProperty("Param", TextField.class, null);
		tblsourcensinks.addContainerProperty("Include SubClasses",
				CheckBox.class, null);
		tblsourcensinks.addContainerProperty("Indirect Calls", CheckBox.class,
				null);
		tblsourcensinks.setPageLength(5);
		tblsourcensinks.setColumnWidth("Types", -1);
		tblsourcensinks.setColumnWidth("Include SubClasses", -1);
		tblsourcensinks.setColumnWidth("Classes", -1);
		tblsourcensinks.setColumnWidth("Selector", -1);
		tblsourcensinks.setColumnWidth("Param", -1);
		tblsourcensinks.setColumnWidth("Indirect Calls", -1);
		// context menu for table sourcensinks
		tblsourcensinks.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					tblsourcensinks.select(event.getItemId());
				} else if (event.isDoubleClick()) {
					UI.getCurrent().addWindow(createSourceSinkWindow());
					selecteditemidsns = event.getItemId();
				}
			}

		});
		tblsourcensinks.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					UI.getCurrent().addWindow(createSourceSinkWindow());
				} else if (action.getCaption() == "Delete") {
					tblsourcensinks.removeItem(target);
				}

			}
		});

		uploadCGFile.addSucceededListener(uploadHandler);
		uploadSDGFile.addSucceededListener(uploadHandler);
		// uploadReportFile.addSucceededListener(this);

		optSrcAndSinks = new OptionGroup("Source and Sink Files");
		optSrcAndSinks.setMultiSelect(true);
		fillOptionGroupSourceAndSinks(optSrcAndSinks);
		// Source and Sinks Upload files
		Upload.Receiver sAndSReceiver = createSourceAndSinksReceiver();
		Upload uploadSnSfile = new Upload("", sAndSReceiver);
		uploadSnSfile.addSucceededListener(new SucceededListener() {
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				optSrcAndSinks.addItem(sourceAndSinksFile);
			}
		});
		uploadSnSfile.setWidth("100%");

		chkMultithreaded = new CheckBox("Multithreaded");
		chkcomputechops = new CheckBox("Compute Chops");
		chkObjectsensitivenes = new CheckBox("Object Sensitiveness");
		chkindirectflows = new CheckBox("Indirect Flows");
		chkSystemOut = new CheckBox("System Out");
		chkOmitIFC = new CheckBox("OmitIFC");
		// IndexedContainer cmbocontainer = new IndexedContainer();
		// cmbocontainer.addContainerProperty("name", String.class, null);

		Button btnsave = new Button("Save Configuration");
		Button btnrun = new Button("Run Analysis");

		// Build layout
		VerticalLayout parent = new VerticalLayout();
		Label titleLabel = new Label("Static Analysis");
		titleLabel.setSizeUndefined();
		titleLabel.addStyleName(ValoTheme.LABEL_H1);
		titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		parent.addComponent(titleLabel);

		FormLayout fl = new FormLayout();
		fl.addComponent(txtAnalysisName);
		fl.addComponent(txtAppName);
		fl.addComponent(cmbmode);
		fl.addComponent(cmbStub);
		fl.addComponent(cmbPruningPolicy);
		fl.addComponent(cmbClassFiles);
		fl.addComponent(cmbEntryPoint);
		//fl.addComponent(txtFldEntryPoint);
		fl.addComponent(tblClassPath);
		fl.addComponent(tblThirdPartyLib);
		fl.addComponent(txtSDGFile);
		fl.addComponent(uploadSDGFile);
		fl.addComponent(txtCGFile);
		fl.addComponent(uploadCGFile);

		fl.addComponent(txtReportFile);
		fl.addComponent(txtStatistics);
		fl.addComponent(txtLogFile);
		fl.addComponent(cmbPointstoPolicy);
		fl.addComponent(txtPointstoFallback);
		fl.addComponent(tblPointsToInclude);
		fl.addComponent(tblPointsToExclude);

		fl.addComponent(tblsourcensinks);
		fl.addComponent(optSrcAndSinks);
		fl.addComponent(uploadSnSfile);
		// fl.addComponent(txterror);+
		
		fl.addComponent(chkMultithreaded);
		fl.addComponent(chkObjectsensitivenes);
		fl.addComponent(chkindirectflows);
		fl.addComponent(chkcomputechops);
		fl.addComponent(chkSystemOut);
		fl.addComponent(chkOmitIFC);	
		
		subWindow = new Window("No App selected, please choose an app!");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setModal(true);
        subWindow.setContent(subContent);
        cmbListApp = new ComboBox("List of available Apps");
        Button btnSubWindowOK = new Button("OK");
        btnSubWindowOK.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String s = (String) cmbListApp.getValue();
				if (s==null) return;
				String[] temp = s.split(" ");
				try {
					app = AppDAO.getAppById(Integer.parseInt(temp[0]));
					initializeSubDirectoriesOfCode();
					initializeJarFilesInCode();
					fillComboBoxClassFiles();
					fillStaticAnalysisFields(FileUtil.getPathConfig(app.getHashCode()));
					subWindow.close();
				} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
		});
        // Put some components in it
        subContent.addComponent(cmbListApp);
        subContent.addComponent(btnSubWindowOK);
        // Center it in the browser window
        subWindow.center();
		// fl.setMargin(true);
		fl.setSizeFull();
		parent.addComponent(fl);

		HorizontalLayout tmpParent = new HorizontalLayout();
		btnsave.addStyleName("mybutton");
		tmpParent.addComponent(btnsave);
		tmpParent.addComponent(btnrun);
		parent.addComponent(tmpParent);

		btnsave.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				saveConfigurationxml("");
			}
		});
		
		btnrun.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String xmlFile = saveConfigurationxml("");
				if (xmlFile != "") {
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.MAIN.getViewName()
							+ "/" + DashboardViewType.STATANALYSIS.getViewName()
							+ "/" + String.valueOf(app.getId()) + "/" + xmlFile);
					//Analyser.StaticAnalyser(app, xmlFile);
				}
			}
		});
		parent.setMargin(true);
		addComponent(parent);
	}

	private Window createSourceSinkWindow() {
		// open new subwindow for tblsubclasses
		final Window subWindow = new Window("Sub-window");
		FormLayout subContent = new FormLayout();
		subContent.setMargin(true);
		subWindow.setModal(true);
		subWindow.setSizeUndefined();
		subWindow.setContent(subContent);

		// Put some components in it
		final ComboBox cmbtypes = new ComboBox("Types");
		cmbtypes.addItem("Source");
		cmbtypes.addItem("Sink");
		cmbtypes.setNullSelectionAllowed(false);
		cmbtypes.setValue("Source");
		final TextField txtclasses = new TextField("Classes");
		final TextField txtselector = new TextField("Selector");
		final TextField txtparam = new TextField("Param");
		final CheckBox chkinclude = new CheckBox("Include SubClasses");
		final CheckBox chkindirectcalls = new CheckBox("Indirect Calls");

		subContent.addComponent(cmbtypes);
		subContent.addComponent(txtclasses);
		subContent.addComponent(txtselector);
		subContent.addComponent(txtparam);
		subContent.addComponent(chkinclude);
		subContent.addComponent(chkindirectcalls);
		Button btnsavetotable = new Button("Save");
		btnsavetotable.addClickListener(new Button.ClickListener() {
			@SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) {
				// subWindow.close();
				try {

					TextField tempClass = new TextField();
					TextField txtTypes = new TextField();
					TextField tempSelector = new TextField();
					TextField tempParam = new TextField();
					CheckBox tempIncludeSubClass = new CheckBox();
					CheckBox tempIndirectCalls = new CheckBox();

					String text = txtclasses.getValue();
					tempClass.setValue(text);
					text = txtselector.getValue();
					tempSelector.setValue(text);
					text = txtparam.getValue();
					tempParam.setValue(text);

					if (cmbtypes.getValue() != null) {
						txtTypes.setValue(cmbtypes.getValue().toString());
					}

					boolean tempbool = chkinclude.getValue();
					tempIncludeSubClass.setValue(tempbool);
					tempbool = chkindirectcalls.getValue();
					tempIndirectCalls.setValue(tempbool);

					boolean isduplicate = false, isMandatoryfieldsfilled = true;
					if (("".equalsIgnoreCase(tempClass.getValue()))
							|| ("".equalsIgnoreCase(tempSelector.getValue()))
							|| ("".equalsIgnoreCase(tempParam.getValue())))
						isMandatoryfieldsfilled = false;

					for (int i = 1; i < tblsourcensinks.size() + 1; i++) {
						Item row = tblsourcensinks.getItem(i);

						TextField txtClasses = (TextField) row.getItemProperty(
								"Classes").getValue();
						String strClasses = txtClasses.getValue();

						TextField txtSelector = (TextField) row
								.getItemProperty("Selector").getValue();

						String strSelector = txtSelector.getValue();
						TextField txtParam = (TextField) row.getItemProperty(
								"Param").getValue();
						String strParam = txtParam.getValue();
						TextField txtretTypes = (TextField) row
								.getItemProperty("Types").getValue();
						String strTypes = txtretTypes.getValue();
						CheckBox chkSubclassIncluded = (CheckBox) row
								.getItemProperty("Include SubClasses")
								.getValue();
						boolean isSubclassIncluded = chkSubclassIncluded
								.getValue();
						if ((strClasses.equalsIgnoreCase(tempClass.getValue()))
								&& (strSelector.equalsIgnoreCase(tempSelector
										.getValue()))
								&& (strParam.equalsIgnoreCase(tempParam
										.getValue()))
								&& (strTypes.equalsIgnoreCase(txtTypes
										.getValue()))
								&& (isSubclassIncluded == tempIncludeSubClass
										.getValue()))
							isduplicate = true;

						if (isduplicate)
							break;
					}

					if (!isduplicate && isMandatoryfieldsfilled) {
						Object newItemId = tblsourcensinks.addItem();
						Item row = tblsourcensinks.getItem(newItemId);

						row.getItemProperty("Classes").setValue(tempClass);

						row.getItemProperty("Selector").setValue(tempSelector);
						row.getItemProperty("Param").setValue(tempParam);
						row.getItemProperty("Include SubClasses").setValue(
								tempIncludeSubClass);
						row.getItemProperty("Indirect Calls").setValue(
								tempIndirectCalls);
						row.getItemProperty("Types").setValue(txtTypes);

						tempClass.setVisible(true);
						tempClass.setEnabled(false);
						tempSelector.setVisible(true);
						tempSelector.setEnabled(false);
						tempParam.setVisible(true);
						tempParam.setEnabled(false);
						tempIncludeSubClass.setVisible(true);
						tempIncludeSubClass.setEnabled(false);
						tempIndirectCalls.setVisible(true);
						tempIndirectCalls.setEnabled(false);
						txtTypes.setVisible(true);
						txtTypes.setEnabled(false);

						subWindow.close();
					} else if (isMandatoryfieldsfilled == false) {

						Window msgbox = new Window("MessageBox");
						// msgbox.addStyleName("Messageboxdesign");

						FormLayout subContent = new FormLayout();
						Label lblmessage = new Label(
								"Please fill all the mandatory fields");
						lblmessage.addStyleName("Messageboxdesign");
						subContent.setMargin(true);
						subContent.addComponent(lblmessage);
						msgbox.setModal(true);
						msgbox.setSizeUndefined();
						msgbox.setContent(subContent);
						UI.getCurrent().addWindow(msgbox);
					} else {
						Window msgbox = new Window("MessageBox");
						FormLayout subContent = new FormLayout();
						Label lblmessage = new Label(
								"You cannot add the values to the table since another duplicate row is already present");
						subContent.setMargin(true);
						subContent.addComponent(lblmessage);
						msgbox.setModal(true);
						msgbox.setSizeUndefined();
						msgbox.setContent(subContent);
						UI.getCurrent().addWindow(msgbox);

						// boolean contains = tblsourcensinks
						// .containsId(newItemId);
						// if (contains == true) {
						// tblsourcensinks.removeItem(newItemId);
						// }
					}
				} catch (Exception ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
		subContent.addComponent(btnsavetotable);
		// Center it in the browser window
		subWindow.center();
		return subWindow;
	}

	protected String saveConfigurationxml(String name) {
		String xmlFile = "";
		AnalysisData data = new AnalysisData();
		data.setAnalysisName(txtAnalysisName.getValue());
		data.setClasspath(readDataFromTable(tblClassPath, CLASSPATH_PROPERTY_ID));
		data.setThirdPartyLibs(readDataFromTable(tblThirdPartyLib, THIRD_PARTY_LIBRARY_PROPERTY_ID));
		data.setStubs(cmbStub.getValue().toString());
		data.setEntrypoint(cmbEntryPoint.getValue().toString());
		data.setMode(cmbmode.getValue().toString());
		data.setSdgFile(txtSDGFile.getValue());
		data.setCgFile(txtCGFile.getValue());
		data.setMultiThreaded(String.valueOf(chkMultithreaded.isEnabled()));
		data.setPruningPolicy(cmbPruningPolicy.getValue().toString());
		data.setPointsToPolicy(cmbPointstoPolicy.getValue().toString());
		data.setPointsToFallback(txtPointstoFallback.getValue());
		data.setPointsToIncludeClasses(readDataFromTable(tblPointsToInclude, POINTS_TO_INCLUDE_PROPERTY_ID));
		data.setPointsToExcludeClasses(readDataFromTable(tblPointsToExclude, POINTS_TO_EXCLUDE_PROPERTY_ID));
		//data.setSourcesSinksFiles(Arrays.asList(new String[] { txtFldSnSFile.getValue() }));
		data.setMultiThreaded(String.valueOf(chkMultithreaded.getValue()));
		data.setObjectSensitiveness(String.valueOf(chkObjectsensitivenes
				.getValue()));
		data.setIgnoreIndirectFlows(String.valueOf(chkindirectflows.getValue()));
		data.setComputeChops(String.valueOf(chkcomputechops.getValue()));
		data.setSystemOut(String.valueOf(chkSystemOut.getValue()));
		data.setOmitIFC(String.valueOf(chkOmitIFC.getValue()));
		data.setReportFile(txtReportFile.getValue());
		data.setStatisticsFile(txtStatistics.getValue());
		data.setLogFile(txtLogFile.getValue());
		List<AnalysisData.SourcesSinks> sourcesAndSinks = new LinkedList<AnalysisData.SourcesSinks>();
		data.setSourcesSinks(sourcesAndSinks);

		for (int i = 1; i <= tblsourcensinks.size(); i++) {
			AnalysisData.SourcesSinks sourceSink = data.new SourcesSinks();
			sourcesAndSinks.add(sourceSink);

			Item row = tblsourcensinks.getItem(i);
			TextField temp = new TextField();
			CheckBox chktemp = new CheckBox();

			temp = (TextField) row.getItemProperty("Types").getValue();
			sourceSink.setType(AnalysisData.TYPES.SOURCE);

			temp = (TextField) row.getItemProperty("Classes").getValue();
			sourceSink.setClazz((String) temp.getValue());

			temp = (TextField) row.getItemProperty("Selector").getValue();
			sourceSink.setSelector((String) temp.getValue());

			temp = (TextField) row.getItemProperty("Param").getValue();
			sourceSink.setParams((String) temp.getValue());

			chktemp = (CheckBox) row.getItemProperty("Include SubClasses")
					.getValue();
			sourceSink.setIncludeSubClasses(String.valueOf(chktemp.getValue()));

			chktemp = (CheckBox) row.getItemProperty(
					"Indirect CagridClassPathlls").getValue();
			sourceSink.setIndirectCalls(String.valueOf(chktemp.getValue()));
		}

		data.setSourcesSinksFiles(getSelectedSnSFiles());

		
		StringBuilder strError = new StringBuilder();
//		if ("".equals(data.getAnalysisName())){
//			strError.append("Analysis name must be specified");
//			strError.append("<br/>");
//		}
		if ("".equals(data.getMode())){
			strError.append("Mode must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getStubs())){
			strError.append("Stubs must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getPruningPolicy())){
			strError.append("Prunning Policy must be specified");
			strError.append("<br/>");
		}
		if("".equals(data.getEntrypoint())){
			strError.append("Entrypoint must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getSdgFile())){
			strError.append("SDG file name must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getCgFile())){
			strError.append("CgFile name must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getStatisticsFile())){
			strError.append("Statistics file name must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getReportFile()))
		{
			strError.append("Report file's name must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getLogFile())){
			strError.append("Log file's name must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getPointsToPolicy())){
			strError.append("Points to Policy must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getPointsToFallback())){
			strError.append("Points to Fallback must be specified");
			strError.append("<br/>");
		}
		if(!"".equals(strError.toString())){
			Notification notification = new Notification(
	                "Message Box");
	        notification.setDescription(strError.toString());
	        notification.setHtmlContentAllowed(true);
	        notification.setStyleName("tray dark small closable login-help");
	        notification.setPosition(Position.BOTTOM_RIGHT);
	        notification.setDelayMsec(5000);
	        notification.show(Page.getCurrent());
		}
		else{
			DocBuilder docBuilder = new DocBuilder();
			xmlFile = docBuilder.generateAnalysisConfigFile(data, app, name);
		}
		return xmlFile;
	        
	}
	private List<String> getSelectedSnSFiles(){
		Collection<?> selectedItems = (Collection<?>) optSrcAndSinks.getValue();
		Iterator<?> iterator = selectedItems.iterator();
		String relativePath = "../../../sourceandsinks/";
		List<String> snSList = new ArrayList<>();
		while (iterator.hasNext()) {
			snSList.add(relativePath + iterator.next());
//		    System.out.println(iterator.next());                            
		}
		return snSList;
	}
	
	private void setSourceAndSinksFiles(List<String> absoluteFilePaths) {
		List<String> filesCollection = new ArrayList<String>();
		for (String file : absoluteFilePaths) {
			filesCollection.add(file.replace("../../../sourceandsinks/", ""));
		}
			
		optSrcAndSinks.setValue(filesCollection);
	}
	
	private List<String> readDataFromTable(Table table, String propertyID) {
		List<String> strRows = new ArrayList<String>();
		
		for (Object itemID : table.getContainerDataSource().getItemIds()) {
			AbstractField field = (AbstractField) table.getItem(itemID).getItemProperty(propertyID).getValue();

			if (field.getValue()!=null && !"".equals(field.getValue().toString()))
				strRows.add(field.getValue().toString());
		}
		
		return strRows;
	}
	

	@Override
	public void enter(ViewChangeEvent event) {
		if (event.getParameters() != null && event.getParameters() != "") {
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			for (String msg : msgs) {
				appId = 0;
				if (msg != null){
						appId = Integer.parseInt(msg);
						System.out.println("enter view changeevent " + appId);
					
					try {
						app = AppDAO.getAppById(appId);
						initializeSubDirectoriesOfCode();
						initializeJarFilesInCode();
						fillComboBoxClassFiles();
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
					
				}
			}
			
			if (app != null)
				fillStaticAnalysisFields(FileUtil.getPathConfig(app.getHashCode()));
			
		}
		else {
			fillCmbListApp();
			if (!subWindow.isAttached())
				UI.getCurrent().addWindow(subWindow);
		}
	}
	
	private static ComboBox addComboBoxToTable(Table table, String propertyName, 
											   Collection<String> elements, String selectedValue) {
		Object newItemId = table.addItem();
		ComboBox c = new ComboBox();
		c.setWidth("100%");
		if (elements!= null)
			for (String s:elements)
				c.addItem(s);
		if (!"".equals(selectedValue))
			c.setValue(selectedValue);
		//c.setNullSelectionAllowed(false);
		table.getItem(newItemId).getItemProperty(propertyName).setValue(c);
		return c;
	}
	
	private static TextField addTextFieldToTable(Table table, String propertyName, String newText) {
		Object newItemId = table.addItem();
		TextField t = new TextField();
		t.setWidth("100%");
		t.setValue(newText);
		table.getItem(newItemId).getItemProperty(propertyName).setValue(t);
		return t;
	}
	
	private void fillStaticAnalysisFields(String configPath) {
		txtAppName.setReadOnly(false);
		txtAppName.setValue(app.getName());
		txtAppName.setReadOnly(true);
		AnalysisData data = DocBuilder.readConfigFromFile(configPath, "");

		if (data==null) return;
		
		cmbmode.setValue(data.getMode());
		cmbStub.setValue(data.getStubs());
		cmbPruningPolicy.setValue(data.getPruningPolicy());
		if (!cmbEntryPoint.containsId(data.getEntrypoint()))
				cmbEntryPoint.addItem(data.getEntrypoint());
		cmbEntryPoint.setValue(data.getEntrypoint());
		
		tblClassPath.removeAllItems();
		List<String> lstElements = new ArrayList<String>();
		lstElements.addAll(subDirectoriesOfCode);
		lstElements.addAll(jarFilesInCode);
		for (String classpath : data.getClasspath())
			addComboBoxToTable(tblClassPath, CLASSPATH_PROPERTY_ID,
							   lstElements, classpath);

		tblThirdPartyLib.removeAllItems();
		for (String thirdPartyLib : data.getThirdPartyLibs())
			addComboBoxToTable(tblThirdPartyLib, THIRD_PARTY_LIBRARY_PROPERTY_ID,
							   jarFilesInCode, thirdPartyLib);
	
		
		txtSDGFile.setValue(data.getSdgFile());
		txtCGFile.setValue(data.getCgFile());
		txtReportFile.setValue(data.getReportFile());
		txtStatistics.setValue(data.getStatisticsFile());
		txtLogFile.setValue(data.getLogFile());
		cmbPointstoPolicy.setValue(data.getPointsToPolicy());
		txtPointstoFallback.setValue(data.getPointsToFallback());
		
		tblPointsToInclude.removeAllItems();
		for (String pointToInclude : data.getPointsToIncludeclasses())
			addTextFieldToTable(tblPointsToInclude, POINTS_TO_INCLUDE_PROPERTY_ID,
								pointToInclude);

		tblPointsToExclude.removeAllItems();
		for (String pointToExclude : data.getPointsToExcludeClasses())
			addTextFieldToTable(tblPointsToExclude, POINTS_TO_EXCLUDE_PROPERTY_ID,
								pointToExclude);
		
		setSourceAndSinksFiles(data.getSourcesSinksFiles());
		
		chkMultithreaded.setValue(Boolean.valueOf(data.getMultiThreaded()));
		chkObjectsensitivenes.setValue(Boolean.valueOf(data.getObjectSensitiveness()));
		chkindirectflows.setValue(Boolean.valueOf(data.getIgnoreIndirectFlows()));
		chkcomputechops.setValue(Boolean.valueOf(data.getComputeChops()));
		chkSystemOut.setValue(Boolean.valueOf(data.getSystemOut()));
		chkOmitIFC.setValue((Boolean.valueOf(data.getOmitIFC())));		

	}
	

	private void fillOptionGroupSourceAndSinks(OptionGroup optg) {
		File f = new File(FileUtil.getPathSourceAndSinks());
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
		for (String name : names) optg.addItem(name);
	}
	private void fillCmbListApp(){
		List<App> apps = AppDAO.getAppByStatus(Status.STATICANALYSIS.getStage(), Status.INSTRUMENTATION.getStage());
		for (App app : apps){
			cmbListApp.addItem(app.getId() + " " + app.getName());
		}
	}
	private Upload.Receiver createSourceAndSinksReceiver() {
		Upload.Receiver receiver = new Upload.Receiver() {
			FileOutputStream fos = null;

			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					sourceAndSinksFile = filename;
					File f = new File(FileUtil.Dir.SOURCEANDSINKS.getDir() + filename);
					fos = new FileOutputStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				return fos;
			}

		};
		return receiver;
	}
	
	private void initializeSubDirectoriesOfCode () {
		String initialPath = FileUtil.getPathCode(app.getHashCode());
		List <String> absPathSubDir = FileUtil.getSubDirectories(initialPath);
		//The first element in the getSubDirectories (corresponding to the folder "code")
		//always exists
		this.absPathSubDirOfCode = new ArrayList<String>();
		this.subDirectoriesOfCode = new ArrayList<String>();
		
		this.absPathSubDirOfCode.add(initialPath);
		this.subDirectoriesOfCode.add(CURRENT_FOLDER);
		for (int i=1; i<absPathSubDir.size(); i++) {
			this.subDirectoriesOfCode.add(absPathSubDir.get(i).replace(initialPath+File.separator,CURRENT_FOLDER));
			this.absPathSubDirOfCode.add(absPathSubDir.get(i));
		}		
		
	}
	
	private void initializeJarFilesInCode () {

		String pathCode = FileUtil.getPathCode(app.getHashCode());
		List<File> jarFilesInCode = FileUtil.getFiles(this.absPathSubDirOfCode, ".jar");
		this.jarFilesInCode = new ArrayList<String>();
		for (File f: jarFilesInCode)
			this.jarFilesInCode.add(f.getPath().replace(pathCode+File.separator, CURRENT_FOLDER));
	
		// Add shared library folder
		List<String> sharedLibFolder = new ArrayList<String>();
		sharedLibFolder.add(FileUtil.getPathLibShared());
		List<File> sharedLibraries = FileUtil.getFiles(sharedLibFolder, ".jar");
		for (File f : sharedLibraries)
			this.jarFilesInCode.add(f.getPath().replace(VaadinService.getCurrent().getBaseDirectory().getPath(), ".."+File.separator+".."+File.separator+".."));
	}
	
	private void fillComboBoxEntryPoint(int classFileIndex) {
		File classFile = classFilesInCode.get(classFileIndex);
		CtClass myclass;
		
		try {
			InputStream stream = new FileInputStream(classFile);
			
			ClassPool cp = new ClassPool();
			try {
				
				myclass = cp.makeClass(stream);
				
				cmbEntryPoint.removeAllItems();
				for (CtMethod m: myclass.getMethods()) {
					StringBuilder mySignature = new StringBuilder();
					mySignature.append(m.getLongName().substring(0, m.getLongName().indexOf("(")));
					mySignature.append(m.getSignature());
					cmbEntryPoint.addItem(mySignature.toString());
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	
	}
	
	private void fillComboBoxClassFiles() {
		String pathCode = FileUtil.getPathCode(app.getHashCode());
		this.classFilesInCode = FileUtil.getFiles(FileUtil.getSubDirectories(pathCode), ".class" );
		for (int i=0; i<this.classFilesInCode.size(); i++) {
			String relativePath = classFilesInCode.get(i).getPath().replace(pathCode+File.separator, CURRENT_FOLDER);
			cmbClassFiles.addItem(i+INDEX_CLASS_FILE_SEPARATOR+relativePath);
		}
			
	}
	
	
}
