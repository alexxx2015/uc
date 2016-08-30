package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Position;
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

public class StaticAnalysisView extends VerticalLayout implements View {
	private UploadHandler uploadHandler = new UploadHandler(this);
	
	ComboBox cmbListApp;
	TextField txtAnalysisName, txtAppName;
	Window subWindow;
	
	String appName, staticAnalysisPath, sourceAndSinksFile;
	int appId;
	App app;
	String strBaseFolders;

	// Properties prop = new Properties();
	// String propFileName = "Config.properties";
	// InputStream inputStream =
	// getClass().getClassLoader().getResourceAsStream(
	// propFileName);
	// create global variables
	String classpath, thirdpartylib, stubs, temppath;
	String sdgvalue, cgvalue;
	String strUploadFilePathCG;
	Object selecteditemidsns;
	OutputStream output = null;

	Table gridClassPath, gridThirdPartyLib, tblpointstoinclude,
			tblpointstoexclude, tblsourcensinks;

	CheckBox chkMultithreaded, chkcomputechops, chkObjectsensitivenes,
			chkindirectflows, chkSystemOut,chkOmitIFC;

	TextField txtSDGFile, txtCGFile, txtReportFile, txtPointstoFallback,
			txtLogFile, txtFldEntryPoint,txtStatistics, txtFldSnSFile;
	ComboBox cmbmode, cmbStub, cmbPointstoPolicy,cmbPruningPolicy;
	Upload uploadSDGFile, uploadCGFile;
	OptionGroup optSrcAndSinks;
	Button btnsave, btnrun, btnselectsnsFile;

	StaticAnalyser analyser;

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
		
		cmbPruningPolicy = new ComboBox("Pruning Policy");
		cmbPruningPolicy.setWidth("100%");
		cmbPruningPolicy.setNullSelectionAllowed(false);
		cmbPruningPolicy.addItem("app");
		cmbPruningPolicy.addItem("off");
		cmbPruningPolicy.setValue("off");

		cmbStub = new ComboBox("Stubs");
		cmbStub.setWidth("100%");
		cmbStub.setNullSelectionAllowed(false);
		cmbStub.addItem("JRE_14");
		cmbStub.addItem("JRE_15");
		cmbStub.addItem("NO_STUBS");
		cmbStub.setValue("NO_STUBS");

		txtFldEntryPoint = new TextField("Entry Point");
		txtFldEntryPoint.setWidth("100%");
		
		txtStatistics = new TextField("Statistics");
		txtStatistics.setWidth("100%");
		
		// Generate classpath table
		gridClassPath = new Table("ClassPath");
		gridClassPath.addContainerProperty("Classpath", TextField.class, null);
		gridClassPath.setPageLength(5);
		gridClassPath.setWidth("100%");
		gridClassPath.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					gridClassPath.select(event.getItemId());
				}
			}

		});

		gridClassPath.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = gridClassPath.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					gridClassPath.getItem(newItemId)
							.getItemProperty("Classpath").setValue(t);
				} else if (action.getCaption() == "Delete") {
					gridClassPath.removeItem(target);
				}
			}
		});

		// Generate third party library table
		gridThirdPartyLib = new Table("Third Party Library");
		gridThirdPartyLib.setWidth("100%");
		gridThirdPartyLib.addContainerProperty("ThirdPartyLibrary",
				TextField.class, null);
		gridThirdPartyLib.setPageLength(5);
		gridThirdPartyLib.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					Object newItemId = gridThirdPartyLib.getValue();
					newItemId = event.getItemId();
					TextField txt = (TextField) gridThirdPartyLib
							.getItem(newItemId)
							.getItemProperty("ThirdPartyLibrary").getValue();

					txt.setEnabled(true);
				}
			}

		});
		gridThirdPartyLib.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					TextField txt = new TextField();
					txt.setWidth("100%");
					Object newItemId = gridThirdPartyLib.addItem();
					gridThirdPartyLib.getItem(newItemId)
							.getItemProperty("ThirdPartyLibrary").setValue(txt);
				} else if (action.getCaption() == "Delete") {
					gridThirdPartyLib.removeItem(target);
				}

			}
		});

		txtSDGFile = new TextField("SDGFile");
		txtSDGFile.setWidth("100%");

		txtCGFile = new TextField("CGFile");
		txtCGFile.setWidth("100%");

		txtReportFile = new TextField("Report File");
		txtReportFile.setWidth("100%");

		txtLogFile = new TextField("Log File");
		txtLogFile.setWidth("100%");

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
		tblpointstoinclude = new Table("Points To Include");
		tblpointstoinclude.setWidth("100%");
		tblpointstoinclude.addContainerProperty("Points To Include",
				TextField.class, null);
		tblpointstoinclude.setPageLength(5);
		tblpointstoinclude.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					tblpointstoinclude.select(event.getItemId());
				}
			}
		});
		tblpointstoinclude.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					TextField txt = new TextField();
					txt.setWidth("100%");
					Object newItemId = tblpointstoinclude.addItem();
					tblpointstoinclude.getItem(newItemId)
							.getItemProperty("Points To Include").setValue(txt);
				} else if (action.getCaption() == "Delete") {
					tblpointstoinclude.removeItem(target);
				}
			}
		});

		// genrate points-to exclude table
		tblpointstoexclude = new Table("Points To Exclude");
		tblpointstoexclude.setWidth("100%");
		tblpointstoexclude.addContainerProperty("Points To Exclude",
				TextField.class, null);
		tblpointstoexclude.setPageLength(5);
		tblpointstoexclude.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					tblpointstoexclude.select(event.getItemId());
				}
			}

		});
		tblpointstoexclude.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					TextField txt = new TextField("textfield");
					txt.setWidth("100%");
					Object newItemId = tblpointstoexclude.addItem();
					tblpointstoexclude.getItem(newItemId)
							.getItemProperty("Points To Exclude").setValue(txt);
				} else if (action.getCaption() == "Delete") {
					tblpointstoexclude.removeItem(target);
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
		tblsourcensinks.setColumnWidth("Types", 130);
		tblsourcensinks.setColumnWidth("Include SubClasses", 130);
		tblsourcensinks.setColumnWidth("Classes", 190);
		tblsourcensinks.setColumnWidth("Selector", 190);
		tblsourcensinks.setColumnWidth("Param", 190);
		tblsourcensinks.setColumnWidth("Indirect Calls", 130);
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
		fillOptionGroup(optSrcAndSinks);
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
		fl.addComponent(txtFldEntryPoint);
		fl.addComponent(gridClassPath);
		fl.addComponent(gridThirdPartyLib);
		fl.addComponent(txtSDGFile);
		fl.addComponent(uploadSDGFile);
		fl.addComponent(txtCGFile);
		fl.addComponent(uploadCGFile);

		fl.addComponent(txtReportFile);
		fl.addComponent(txtStatistics);
		fl.addComponent(txtLogFile);
		fl.addComponent(cmbPointstoPolicy);
		fl.addComponent(txtPointstoFallback);
		fl.addComponent(tblpointstoinclude);
		fl.addComponent(tblpointstoexclude);

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
        subWindow.setContent(subContent);
        cmbListApp = new ComboBox("List of avaialbe Apps");
        Button btnSubWindowOK = new Button("OK");
        btnSubWindowOK.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String s = (String) cmbListApp.getValue();
				String[] temp = s.split(" ");
				try {
					app = AppDAO.getAppById(Integer.parseInt(temp[0]));
					fillStaticAnalysisTextboxes(FileUtil.getPathConfig(app.getHashCode()));
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
		data.setClasspath(readDataFromTable(gridClassPath));
		data.setThirdPartyLibs(readDataFromTable(gridThirdPartyLib));
		data.setStubs(cmbStub.getValue().toString());
		data.setEntrypoint(txtFldEntryPoint.getValue());
		data.setMode(cmbmode.getValue().toString());
		data.setSdgFile(txtSDGFile.getValue());
		data.setCgFile(txtCGFile.getValue());
		data.setMultiThreaded(String.valueOf(chkMultithreaded.isEnabled()));
		data.setPruningPolicy(cmbPruningPolicy.getValue().toString());
		data.setPointsto_policy(cmbPointstoPolicy.getValue().toString());
		data.setPointsto_fallback(txtPointstoFallback.getValue());
		data.setPointsto_includeclasses(readDataFromTable(tblpointstoinclude));
		data.setPointsto_excludeclasses(readDataFromTable(tblpointstoexclude));
		//data.setSourcesSinksFiles(Arrays.asList(new String[] { txtFldSnSFile.getValue() }));
		data.setMultiThreaded(String.valueOf(chkMultithreaded.getValue()));
		data.setObjectsensitivenes(String.valueOf(chkObjectsensitivenes
				.getValue()));
		data.setIgnoreIndirectFlows(String.valueOf(chkindirectflows.isEnabled()));
		data.setComputeChops(String.valueOf(chkcomputechops.isEnabled()));
		data.setSystemout(String.valueOf(chkSystemOut.getValue()));
		data.setOmitIFC(String.valueOf(chkOmitIFC.getValue()));
		data.setReportFile(txtReportFile.getValue());
		data.setStatistics(txtStatistics.getValue());
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
		if ("".equals(data.getAnalysisName())){
			strError.append("Analysis name must be specified");
			strError.append("<br/>");
		}
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
		if ("".equals(data.getStatistics())){
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
		if ("".equals(data.getPointsto_policy())){
			strError.append("Points to Policy must be specified");
			strError.append("<br/>");
		}
		if ("".equals(data.getPointsto_fallback())){
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
	private List<String> readDataFromTable(Table tablename) {

		List<String> strpaths = new ArrayList<>();
		int newItemId = tablename.size();
		try {
			for (int i = 1; i <= newItemId; i++) {
				Item row = tablename.getItem(i);
				TextField temp = new TextField();
				if (tablename.getCaption().toLowerCase().equals("classpath")) {
					temp = (TextField) row.getItemProperty("Classpath")
							.getValue();

				} else if (tablename.getCaption().toLowerCase()
						.equals("third party library"))
					temp = (TextField) row.getItemProperty("ThirdPartyLibrary")
							.getValue();
				else if (tablename.getCaption().toLowerCase()
						.equals("points to include"))
					temp = (TextField) row.getItemProperty("Points To Include")
							.getValue();
				else if (tablename.getCaption().toLowerCase()
						.equals("points to exclude"))
					temp = (TextField) row.getItemProperty("Points To Exclude")
							.getValue();

				strpaths.add(temp.getValue());
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return strpaths;

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
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
				}
			}
			
			if (app != null)
				fillStaticAnalysisTextboxes(FileUtil.getPathConfig(app.getHashCode()));
		}
		else {
			fillCmbListApp();
			UI.getCurrent().addWindow(subWindow);
		}
	}

	private void fillStaticAnalysisTextboxes(String path) {
		txtAppName.setValue(app.getName());
		txtAppName.setReadOnly(true);
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			File appFolder = new File(path);
			File[] fileslist = appFolder.listFiles();

			List<File> listConfigfiles1 = new ArrayList<>();
			if (path != null) {
				for (File name : fileslist) {
//					if (name.isDirectory()) {
//						String temp = name.getName();
//						if (!(temp.equals("Source")))
							listConfigfiles1.add(name);
//					}
				}
				//latest file first
				Collections.sort(listConfigfiles1, new Comparator<File>() {
					public int compare(File f1, File f2) {
						return Long.valueOf(f2.lastModified()).compareTo(
								f1.lastModified());
					}
				});
			}
			String configfilepath = listConfigfiles1.get(0).getAbsolutePath();

			Document doc = docBuilder.parse(new File(configfilepath));
			doc.getDocumentElement().normalize();

			NodeList listOffile = doc.getElementsByTagName("analysis");
			Element AppNameElement = (Element) listOffile.item(0);
			//get the last string seperated by "/"
			String pathAsArrayString[] = AppNameElement.getAttribute("name").split("/");
			txtAnalysisName.setValue(pathAsArrayString[pathAsArrayString.length - 1]);

			for (int s = 0; s < listOffile.getLength(); s++) {

				Node FileNode = listOffile.item(s);
				if (FileNode.getNodeType() == Node.ELEMENT_NODE) {

					Element FileElement = (Element) FileNode;

					NodeList NameList = FileElement
							.getElementsByTagName("mode");
					Element NameElement = (Element) NameList.item(0);
					cmbmode.setValue(NameElement.getAttribute("value"));

					NodeList ClasspathlistList = FileElement
							.getElementsByTagName("classpath");
					gridClassPath.removeAllItems();
					Element ClasspathElement = (Element) ClasspathlistList
							.item(0);
					TextField txt = new TextField("textfield");
					txt.setValue(ClasspathElement.getAttribute("value"));
					txt.setWidth(24.6f, ComboBox.UNITS_EM);
					Object newItemId = gridClassPath.addItem();
					Item row = gridClassPath.getItem(newItemId);
					row.getItemProperty("Classpath").setValue(txt);
					gridClassPath.addItem(new Object[] { txt }, newItemId);

					NodeList ThirdPartyLibList = FileElement
							.getElementsByTagName("thirdPartyLibs");
					Element ThirdPartyLibElement = (Element) ThirdPartyLibList
							.item(0);
					TextField txttpl = new TextField("textfield");
					txttpl.setValue(ThirdPartyLibElement.getAttribute("value"));
					txttpl.setWidth(24.6f, ComboBox.UNITS_EM);
					gridThirdPartyLib.removeAllItems();
					Object newItemIdtpl = gridThirdPartyLib.addItem();
					Item rowtpl = gridThirdPartyLib.getItem(newItemIdtpl);
					rowtpl.getItemProperty("ThirdPartyLibrary")
							.setValue(txttpl);
					gridThirdPartyLib.addItem(new Object[] { txttpl },
							newItemIdtpl);

					NodeList StubList = FileElement
							.getElementsByTagName("stubs");
					if (StubList != null) {
						Element StubElement = (Element) StubList.item(0);
						cmbStub.setValue(StubElement.getAttribute("value"));
					}
					NodeList EntryPointList = FileElement
							.getElementsByTagName("entrypoint");
					if (EntryPointList != null) {
						Element EntryPointElement = (Element) EntryPointList
								.item(0);
						txtFldEntryPoint.setValue((EntryPointElement
								.getAttribute("value")));
					}

					NodeList IndirectList = FileElement
							.getElementsByTagName("ignoreIndirectFlows");
					if (IndirectList != null) {
						Element IndirectElement = (Element) IndirectList
								.item(0);
						chkindirectflows
								.setValue(Boolean.valueOf(IndirectElement
										.getAttribute("value")));
					}

					NodeList sdgList = FileElement
							.getElementsByTagName("sdgfile");

					Element sdgElement = (Element) sdgList.item(0);
					txtSDGFile.setValue((sdgElement.getAttribute("value")));
					NodeList logList = FileElement
							.getElementsByTagName("logFile");
					if (logList != null) {
						Element logElement = (Element) logList.item(0);
						txtLogFile.setValue((logElement.getAttribute("value")));
						NodeList cgfileList = FileElement
								.getElementsByTagName("cgfile");
						Element cgfileElement = (Element) cgfileList.item(0);
						txtCGFile
								.setValue((cgfileElement.getAttribute("value")));
					}
					NodeList reportList = FileElement
							.getElementsByTagName("reportfile");
					if (reportList != null) {
						Element reportElement = (Element) reportList.item(0);
						txtReportFile.setValue(reportElement
								.getAttribute("value"));
					}
					NodeList statisticsList = FileElement.getElementsByTagName("statistics");
					if(statisticsList != null) {
						Element statisticsElement = (Element) statisticsList.item(0);
						txtStatistics.setValue(statisticsElement.getAttribute("value"));
					}
					NodeList chopsList = FileElement
							.getElementsByTagName("computeChops");
					if (chopsList != null) {
						Element chopsElement = (Element) chopsList.item(0);
						chkcomputechops.setValue((Boolean.valueOf(chopsElement
								.getAttribute("value"))));
					}
					NodeList omitIFC = FileElement.getElementsByTagName("omitIFC");
					if (omitIFC != null) {
						Element omit = (Element) omitIFC.item(0);
						chkOmitIFC.setValue((Boolean.valueOf(omit.getAttribute("value"))));
					}
					NodeList fallBackList = FileElement.getElementsByTagName("points-to");
					if (fallBackList != null) {
						Element fallBack = (Element) fallBackList.item(0);
						txtPointstoFallback.setValue(fallBack.getAttribute("fallback"));
					}
					NodeList pointsToPolicy = FileElement.getElementsByTagName("points-to");
					if (pointsToPolicy != null) {
						Element ptPolicy = (Element) pointsToPolicy.item(0);
						cmbPointstoPolicy.setValue(ptPolicy.getAttribute("policy"));
					}
					NodeList sysoutList = FileElement
							.getElementsByTagName("systemout");
					if (sysoutList != null) {
						Element sysoutElement = (Element) sysoutList.item(0);
						chkSystemOut.setValue(Boolean.valueOf(sysoutElement
								.getAttribute("value")));
					}

					NodeList sensitivenessList = FileElement
							.getElementsByTagName("objectsensitivenes");
					if (sensitivenessList != null) {
						Element sensitivenessElement = (Element) sensitivenessList
								.item(0);
						chkObjectsensitivenes.setValue((Boolean
								.valueOf(sensitivenessElement
										.getAttribute("value"))));
					}

				}

			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.out.println(e.getLocalizedMessage() + e.getMessage());
		}
	}
	private void fillOptionGroup(OptionGroup optg) {
		File f = new File(FileUtil.getPathSourceAndSinks());
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
		for (String name : names) optg.addItem(name);
	}
	private void fillCmbListApp(){
		List<App> apps = AppDAO.getAppByStatus(Status.STATICANALYSIS.getStage());
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

}
