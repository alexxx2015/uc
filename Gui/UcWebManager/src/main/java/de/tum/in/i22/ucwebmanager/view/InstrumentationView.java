package de.tum.in.i22.ucwebmanager.view;

import java.awt.Checkbox;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.BlackAndWhiteList;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.UcConfig;
import de.tum.in.i22.ucwebmanager.Status.Status;
import de.tum.in.i22.ucwebmanager.dashboard.DashboardViewType;
import edu.tum.uc.jvm.Instrumentor;
public class InstrumentationView extends VerticalLayout implements View {
	App app;
	File file;
	Window subWindow;
	ComboBox cmbListApp;
	String appName, arg0, arg1, arg2;
	private static final String BLACK_LIST_PROPERTY = "black list";
	private static final String WHITE_LIST_PROPERTY = "white list";
	String blackListName = "/blacklist.list", whiteListName = "/whitelist.list";
	private int appId;
	private TextArea textArea;
	private Table gridBlackList, gridWhiteList;
	private Button btnrun;
	private TextField txtPipH, txtPipP, txtPdpP, txtPdpH, txtPmpH, txtPmpP, txtUcWebMgmUrl,
					  txtMypmpP, txtMypmpH, txtIns_class_path, txtStatistics, txtUc_Prop,
					  txtUc_4win_autostart, txtTimermethods, txtSrcFolder, txtDestFolder;
	private CheckBox chkEnforcement, chkInstrumentation, chkTimerT1, chkTimerT2, chkTimerT3, chkTimerT4, chkTimerT5,
					 chkNetcom, chkLogChopNodes, chkPdp_asyncom, chkIFT;
	private ComboBox cmbReportFile;
	
	public InstrumentationView() {
		init();
	}
	@Override
	public void enter(ViewChangeEvent event) {
		gridBlackList.removeAllItems();
		gridWhiteList.removeAllItems();
		fillBlackAndWhiteList();
		if (event.getParameters() != null) {
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			for (String msg : msgs) {
				appId = 0;
				if (msg != null && msg != "") {
					appId = Integer.parseInt(msg);
					System.out.println("enter Instrumentation view " + appId);

					try {
						app = AppDAO.getAppById(appId);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
					if (app != null) {
						appName = app.getName();
						fillCmbReportFile(app);
						addClassesToWhiteList(app);
						fillSrcAndDest(app);
					}

				}
				else {
					fillCmbListApp();
					if (!subWindow.isAttached())
						UI.getCurrent().addWindow(subWindow);
				}
			}
		}
	}
	private void fillCmbListApp(){
		List<App> apps = AppDAO.getAppByStatus(Status.STATICANALYSIS.getStage(), Status.INSTRUMENTATION.getStage());
		for (App app : apps){
			cmbListApp.addItem(app.getId() + " " + app.getName());
		}
	}
	 private String readXmlFile(File file){
			String xml = "";
			try (BufferedReader br = new BufferedReader(new FileReader(file))){
				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
					System.out.println(sCurrentLine);
					xml = xml + "\n" + sCurrentLine;
				} 
			}catch (IOException e) {
				e.printStackTrace();
			}
			return xml;
		}
	 private void fillCmbReportFile(App app){
		 cmbReportFile.removeAllItems();
		 File staticAnalysisOutput = new File(FileUtil.getPathOutput(app.getHashCode()));
		 ArrayList<String> names = new ArrayList<String>(Arrays.asList(staticAnalysisOutput.list()));
		 for (String name : names) cmbReportFile.addItem(name);
	 }
	 private void fillSrcAndDest(App app){
		 String srcFolder = FileUtil.getPathCode(app.getHashCode());
		 String destFolder = FileUtil.getPathInstrumentationOfApp(app.getHashCode());
		 this.txtSrcFolder.setValue(srcFolder);
		 this.txtSrcFolder.setReadOnly(true);
		 this.txtDestFolder.setValue(destFolder);
		 this.txtDestFolder.setReadOnly(true);
		 
	 }

	 private void fillBlackAndWhiteList(){
		 List<String> blackList = BlackAndWhiteList.read(FileUtil.getPathBlackAndWhiteList() + blackListName);
		 List<String> whiteList = BlackAndWhiteList.read(FileUtil.getPathBlackAndWhiteList() + whiteListName);
		 fillTable(blackList, gridBlackList, BLACK_LIST_PROPERTY);
		 fillTable(whiteList, gridWhiteList, WHITE_LIST_PROPERTY);
	 }
	 
	 private void fillTable(List<String> list, Table table, String property){
		 for (String s : list){
			Object newItemId = table.addItem();
			TextField txtField = new TextField();
			txtField.setWidth("100%");
			txtField.setValue(s);
			table.getItem(newItemId).getItemProperty(property).setValue(txtField);
		 } 
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
	 
	 private void init() {
		txtPipH = new TextField("PIP_HOST", "localhost");
		txtPipH.setWidth("100%");
		//txtPipP = new TextField("PIP_PORT", "40011");
		txtPipP = new TextField("PIP_PORT", "21002");
		txtPipP.setWidth("100%");
		
		txtPdpH = new TextField("PDP_HOST", "localhost");
		txtPdpH.setWidth("100%");
		//txtPdpP = new TextField("PDP_PORT", "9090");
		txtPdpP = new TextField("PDP_PORT", "21003");
		txtPdpP.setWidth("100%");
		
		txtPmpH = new TextField("PMP_HOST", "localhost");
		txtPmpH.setWidth("100%");
		txtPmpP = new TextField("PMP_PORT", "40012");
		txtPmpP.setWidth("100%");
		
		txtMypmpH = new TextField("MYPEP_HOST", "localhost");
		txtMypmpH.setWidth("100%");
		txtMypmpP = new TextField("MYPEP_PORT", "9091");
		txtMypmpP.setWidth("100%");
		
		txtIns_class_path = new TextField("INSTRUMENTED_CLASS_PATH");
		txtIns_class_path.setWidth("100%");
		
		chkEnforcement = new CheckBox("ENFORCEMENT", false);
		
		chkInstrumentation = new CheckBox("INSTRUMENTATION", true);
		
		txtStatistics = new TextField("STATISTICS");
		txtStatistics.setWidth("100%");
		
		chkTimerT1 = new CheckBox("TIMER_T1", false);
		chkTimerT2 = new CheckBox("TIMER_T2", false);
		chkTimerT3 = new CheckBox("TIMER_T3", false);
		chkTimerT4 = new CheckBox("TIMER_T4", false);
		chkTimerT5 = new CheckBox("TIMER_T5", false);
		chkIFT = new CheckBox("IFT", true);
		chkNetcom = new CheckBox("NETCOM", true);
		chkLogChopNodes = new CheckBox("LOGCHOPNODES", true);
		
		txtUcWebMgmUrl = new TextField("UC_WEBMGM_URL", "http://localhost:8080/rest");
		txtUcWebMgmUrl.setWidth("100%");
		
		txtUc_Prop = new TextField("UC_PROPERTIES");
		txtUc_Prop.setWidth("100%");
		
		chkPdp_asyncom = new CheckBox("PDP_ASYNCOM", false);
		
		txtUc_4win_autostart = new TextField("UC4WIN_AUTOSTART");
		txtUc_4win_autostart.setWidth("100%");
		
		txtTimermethods = new TextField("TIMERMETHODS", "{\"methods\":[]}");
		txtTimermethods.setWidth("100%");
		
		Label lab = new Label("Instrumentation");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		lab.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		//Combobox
		cmbReportFile = new ComboBox("Select Report File");
//		blackbox = new TableGrid("Black Box", "blackbox");
//		whitebox = new TableGrid("White box", "whitebox");
		gridBlackList = new Table("Black List");
		gridBlackList.addContainerProperty("black list", TextField.class, null);
		gridBlackList.setPageLength(5);
		gridBlackList.setWidth("100%");
		gridBlackList.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					gridBlackList.select(event.getItemId());
				}
			}

		});

		gridBlackList.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = gridBlackList.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					gridBlackList.getItem(newItemId)
							.getItemProperty("black list").setValue(t);
				} else if (action.getCaption() == "Delete") {
					gridBlackList.removeItem(target);
				}
			}
		});
		gridWhiteList = new Table("White List");
		gridWhiteList.addContainerProperty("white list", TextField.class, null);
		gridWhiteList.setPageLength(5);
		gridWhiteList.setWidth("100%");
		gridWhiteList.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					gridWhiteList.select(event.getItemId());
				}
			}

		});

		gridWhiteList.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = gridWhiteList.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					gridWhiteList.getItem(newItemId)
							.getItemProperty("white list").setValue(t);
				} else if (action.getCaption() == "Delete") {
					gridWhiteList.removeItem(target);
				}
			}
		});
		textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setVisible(false);
		txtSrcFolder = new TextField("Source Folder");
		txtSrcFolder.setWidth("100%");
//		txtSrcFolder.setReadOnly(true);
		
		txtDestFolder = new TextField("Destination Folder");
		txtDestFolder.setWidth("100%");
//		txtDestFolder.setReadOnly(true);
		
		btnrun = new Button("Run");
		btnrun.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
//				System.out.println(getReportFileFromComboBox());
				try {
					String hashCode = app.getHashCode();
					arg0 = FileUtil.getPathCode(hashCode);
					String reportFolder = (String) cmbReportFile.getValue();
					String reportFile = FileUtil.getPathOutput(hashCode) + File.separator + reportFolder + File.separator +"report.xml";
					
					arg1 = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + File.separator + reportFolder;
					File f = new File(arg1);
					f.mkdirs();
					
					// create blacklist.list
					List<String> bl = readDataFromTable(gridBlackList, BLACK_LIST_PROPERTY);
					BlackAndWhiteList.saveAndWrite(bl, arg1 + blackListName);
					
					//create whitelist.list
					List<String> wl = readDataFromTable(gridWhiteList, WHITE_LIST_PROPERTY);
					BlackAndWhiteList.saveAndWrite(wl, arg1 + whiteListName);
					//create uc.config
					UcConfig uc = createUcFile(reportFile, arg1 + blackListName, arg1 + whiteListName);
					uc.save(arg1 + File.separator + "uc.config");
					
					arg2 = arg1 + File.separator + "uc.config";
					Instrumentor.main(new String[]{arg0, arg1, arg2});
					
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.MAIN.getViewName()
							+ "/" + DashboardViewType.INSTRUMENT.getViewName()
							+ "/" + String.valueOf(app.getId()));
					
				} catch (IOException | IllegalClassFormatException e) {
					e.printStackTrace();
				}
				
			}
		});
		VerticalLayout parent = new VerticalLayout();
		parent.addComponent(lab);
		
		FormLayout fl = new FormLayout();
		
		fl.addComponent(cmbReportFile);
		fl.addComponent(textArea);
		fl.addComponent(txtPipH);
		fl.addComponent(txtPipP);
		fl.addComponent(txtPdpH);
		fl.addComponent(txtPdpP);
		fl.addComponent(txtPmpH);
		fl.addComponent(txtPmpP);
		fl.addComponent(txtMypmpH);
		fl.addComponent(txtMypmpP);
		fl.addComponent(txtIns_class_path);
		fl.addComponent(chkEnforcement);
		fl.addComponent(gridBlackList);
		fl.addComponent(gridWhiteList);
		fl.addComponent(chkInstrumentation);
		fl.addComponent(txtStatistics);
		fl.addComponent(chkTimerT1);
		fl.addComponent(chkTimerT2);
		fl.addComponent(chkTimerT3);
		fl.addComponent(chkTimerT4);
		fl.addComponent(chkTimerT5);
		fl.addComponent(chkIFT);
		fl.addComponent(chkNetcom);
		fl.addComponent(chkLogChopNodes);
		fl.addComponent(txtUcWebMgmUrl);
		fl.addComponent(txtUc_Prop);
		fl.addComponent(chkPdp_asyncom);
		fl.addComponent(txtUc_4win_autostart);
		fl.addComponent(txtTimermethods);
		fl.addComponent(txtSrcFolder);
		fl.addComponent(txtDestFolder);
		fl.addComponent(btnrun);
		fl.setSizeFull();
		parent.addComponent(fl);
		
		parent.setMargin(true);
		addComponent(parent);
		subWindow = new Window("No App selected, please choose an app!");
		subWindow.setModal(true);
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);
        cmbListApp = new ComboBox("List of available Apps");
        Button btnSubWindowOK = new Button("OK");
        btnSubWindowOK.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String s = (String) cmbListApp.getValue();
				String[] temp = s.split(" ");
				try {
					app = AppDAO.getAppById(Integer.parseInt(temp[0]));
					fillCmbReportFile(app);
					addClassesToWhiteList(app);
					fillSrcAndDest(app);
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
	}
	 private UcConfig createUcFile(String reportFile, String blackList, String whiteList){
		 UcConfig ucConfig = new UcConfig();
		 ucConfig.setPip_host(txtPipH.getValue());
		 ucConfig.setPip_port(txtPipP.getValue());
		 
		 ucConfig.setPdp_host(txtPdpH.getValue());
		 ucConfig.setPdp_port(txtPdpP.getValue());
		 
		 ucConfig.setPmp_host(txtPmpH.getValue());
		 ucConfig.setPmp_port(txtPmpP.getValue());
		 
		 ucConfig.setMypep_host(txtMypmpH.getValue());
		 ucConfig.setMypep_port(txtMypmpP.getValue());
		 
		 ucConfig.setAnalysis_report(reportFile);
		 
		 ucConfig.setInstrumented_class_path(txtIns_class_path.getValue());
		 
		 ucConfig.setEnforcement(chkEnforcement.getValue().toString());
		 
		 ucConfig.setBlacklist(blackList);
		 ucConfig.setWhitelist(whiteList);
		 
		 ucConfig.setInstrumentation(chkInstrumentation.getValue().toString());
		 
		 ucConfig.setStatistics(txtStatistics.getValue());
		 
		 ucConfig.setTimer_t1(chkTimerT1.getValue().toString());
		 ucConfig.setTimer_t2(chkTimerT2.getValue().toString());
		 ucConfig.setTimer_t3(chkTimerT3.getValue().toString());
		 ucConfig.setTimer_t4(chkTimerT4.getValue().toString());
		 ucConfig.setTimer_t5(chkTimerT5.getValue().toString());
		 ucConfig.setIft(chkIFT.getValue().toString());
		 
		 ucConfig.setNetcom(chkNetcom.getValue().toString());
		 ucConfig.setLogChopNodes(chkLogChopNodes.getValue().toString());
		 ucConfig.setUcWebMgmUrl(txtUcWebMgmUrl.getValue().toString());
		 ucConfig.setUc_properties(txtUc_Prop.getValue());
		 ucConfig.setPdp_asyncom(chkPdp_asyncom.getValue().toString());
		 ucConfig.setUc4win_autostart(txtUc_4win_autostart.getValue());
		 ucConfig.setTimermethods(txtTimermethods.getValue());
		 
		 //set appId in uc.config, the hasCode is used
		 ucConfig.setAppId(this.app.getHashCode());
		 return ucConfig;
	 }
	 
	 private void addClassesToWhiteList(App app) {
		String pathCode = FileUtil.getPathCode(app.getHashCode());
		List<File> classFiles = FileUtil.getFiles(FileUtil.getSubDirectories(pathCode), ".class" );	
		List<String> strContainClassFiles = new ArrayList<String>();
		for (File f : classFiles) {
			strContainClassFiles.add("contains:"+f.getName().replaceAll(".class", ""));
		}
		fillTable(strContainClassFiles, gridWhiteList, "white list");
	 }
	 
//	 private void initTable(Table table, String name, String property){
//		 table = new Table("ClassPath");
//			table.addContainerProperty("Classpath", TextField.class, null);
//			table.setPageLength(5);
//			table.setWidth("100%");
//			table.addItemClickListener(new ItemClickListener() {
//				public void itemClick(ItemClickEvent event) {
//					if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
//						table.select(event.getItemId());
//					}
//				}
//
//			});
//
//			table.addActionHandler(new Action.Handler() {
//				public Action[] getActions(Object target, Object sender) {
//					return new Action[] { new Action("New"), new Action("Delete") };
//				}
//
//				@Override
//				public void handleAction(Action action, Object sender, Object target) {
//					if (action.getCaption() == "New") {
//						Object newItemId = table.addItem();
//						TextField t = new TextField();
//						t.setWidth("100%");
//						table.getItem(newItemId)
//								.getItemProperty("Classpath").setValue(t);
//					} else if (action.getCaption() == "Delete") {
//						table.removeItem(target);
//					}
//				}
//			});
//	 }
}
