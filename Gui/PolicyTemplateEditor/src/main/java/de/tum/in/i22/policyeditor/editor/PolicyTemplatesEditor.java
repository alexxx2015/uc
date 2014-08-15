package de.tum.in.i22.policyeditor.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import de.tum.in.i22.policyeditor.logger.EditorLogger;
import de.tum.in.i22.policyeditor.model.PolicyTemplate;
import de.tum.in.i22.policyeditor.translator.DeploymentController;
import de.tum.in.i22.policyeditor.util.Config;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PdpClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
import de.tum.in.i22.uc.thrift.types.TContainer;


/**
 * @author Cipri
 *
 */
public class PolicyTemplatesEditor {

	private JFrame frmTemplatePolicyEditor;
	private final DeploymentController deploymentController;
	
	private String policyClass;
	private Set<TContainer> representations;
	private Pmp2PdpClient clientPmp;
	
	private static ThriftClientFactory thriftClientFactory = new ThriftClientFactory();
	
	private EditorLogger logger; 
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		String dataClass = "picture";
		
		PolicyTemplatesEditor.startEditor(null, dataClass);
	}

	/**
	 * Create the application.
	 * @param representations 
	 */
	public PolicyTemplatesEditor(Set<TContainer> representations, String policyClass) {
		this.deploymentController = new DeploymentController();
		this.policyClass = policyClass;
		this.representations = representations;
		logger = EditorLogger.instance();
		initialize();
	}

	public static void startEditor(final Set<TContainer> representations, final String dataClass){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					ToolTipManager.sharedInstance().setInitialDelay(500);
			        ToolTipManager.sharedInstance().setDismissDelay(1500);
			        PolicyTemplatesEditor window = new PolicyTemplatesEditor(representations, dataClass);
					window.frmTemplatePolicyEditor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTemplatePolicyEditor = new JFrame();
		frmTemplatePolicyEditor.setTitle("Template Policy Editor");
		frmTemplatePolicyEditor.setBounds(100, 100, 1029, 523);
		frmTemplatePolicyEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel editorPanel = new JPanel();
		editorPanel.setBorder(new TitledBorder(null, "Editor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JSplitPane splitPane = new JSplitPane();		
		GroupLayout groupLayout = new GroupLayout(frmTemplatePolicyEditor.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(editorPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 873, Short.MAX_VALUE)
						.addComponent(splitPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 873, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 331, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(editorPanel, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JPanel templatesPanel = new JPanel();
		Dimension minimumSize = new Dimension(420, 200);
		templatesPanel.setMinimumSize(minimumSize);
		templatesPanel.setToolTipText("Click on Policy to edit template");
		templatesPanel.setBorder(new TitledBorder(null, "Policy Templates", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setLeftComponent(templatesPanel);
		
		JScrollPane templatesScrollPanel = new JScrollPane();
		
		JPanel templatesButtons = new JPanel();
		FlowLayout flowLayout = (FlowLayout) templatesButtons.getLayout();
		GroupLayout gl_templatesPanel = new GroupLayout(templatesPanel);
		gl_templatesPanel.setHorizontalGroup(
			gl_templatesPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(templatesScrollPanel, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
				.addGroup(Alignment.TRAILING, gl_templatesPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(templatesButtons, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
					.addGap(10))
		);
		gl_templatesPanel.setVerticalGroup(
			gl_templatesPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_templatesPanel.createSequentialGroup()
					.addComponent(templatesScrollPanel, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(templatesButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		
		JLabel lblTemplates = new JLabel("");
		String iconPath = System.getProperty("user.dir") + File.separator+ "icons" + File.separator + "graphic_design.png";
		lblTemplates.setIcon(new ImageIcon(iconPath));
		lblTemplates.setToolTipText("Click on Policy to edit template");
		templatesButtons.add(lblTemplates);
		
		final JList installedList = new JList();
		final DefaultListModel installedListModel = new DefaultListModel();
		installedList.setModel(installedListModel);
		final JList templatesList = new JList();
		DefaultListModel templatesListModel = new DefaultListModel();
		templatesList.setModel(templatesListModel);
		
		TemplateListRenderer renderer = new TemplateListRenderer();
		templatesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		templatesList.setFixedCellHeight(20);		
		templatesList.setCellRenderer(renderer); 
		templatesList.addMouseListener(renderer.getHandler(templatesList)); 
		templatesList.addMouseMotionListener(renderer.getHandler(templatesList)); 
		templatesScrollPanel.setViewportView(templatesList);
		templatesPanel.setLayout(gl_templatesPanel);
		templatesList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = templatesList.locationToIndex(e.getPoint());
				CheckableItem item = (CheckableItem) templatesList.getModel().getElementAt(index);
				if(item == null)
					return;
			
				editorPanel.removeAll();
				List<Component> components = item.getPolicyEditorComponents();
	            for (Component component : components) {
	            	editorPanel.add(component);
				}
	            editorPanel.updateUI();
			}
			
		});
		
		JPanel installedPanel = new JPanel();
		installedPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Installed Policies", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(installedPanel);
		
		splitPane.setResizeWeight(0.5);
		splitPane.resetToPreferredSizes();
		
		JScrollPane installedScrollPane = new JScrollPane();
		
		JPanel installedButtonPanel = new JPanel();
		
		JButton btnDeployPolicies = new JButton("Deploy policies");
		btnDeployPolicies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListModel model = installedList.getModel();
				int policiesCounter = model.getSize();
				ArrayList<PolicyTemplate> policies = new ArrayList<PolicyTemplate>();
				for(int i=0; i<policiesCounter; i++){
					CheckableItem item = (CheckableItem) model.getElementAt(i);
					if(item.isSelected()){
						PolicyTemplate policy = item.getPolicy();
						policies.add(policy);
					}
				}
				deploymentController.deployPolicies(representations, policyClass, policies);
			}
		});
		installedButtonPanel.add(btnDeployPolicies);
		GroupLayout gl_installedPanel = new GroupLayout(installedPanel);
		gl_installedPanel.setHorizontalGroup(
			gl_installedPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_installedPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(installedButtonPanel, GroupLayout.PREFERRED_SIZE, 424, Short.MAX_VALUE)
					.addContainerGap())
				.addComponent(installedScrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
		);
		gl_installedPanel.setVerticalGroup(
			gl_installedPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_installedPanel.createSequentialGroup()
					.addComponent(installedScrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(installedButtonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		
		CheckListRenderer checkListenderer = new CheckListRenderer();
		
		installedScrollPane.setViewportView(installedList);
		installedPanel.setLayout(gl_installedPanel);
		installedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		installedList.setFixedCellHeight(20);		
		installedList.setCellRenderer(checkListenderer); 
		installedList.addMouseListener(checkListenderer.getHandler(installedList)); 
		installedList.addMouseMotionListener(checkListenderer.getHandler(installedList));
		installedList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = installedList.locationToIndex(e.getPoint());
				CheckableItem item = (CheckableItem) installedList.getModel().getElementAt(index);
				if(item == null)
					return;
				item.registerListener(installedList);		
				if (e.getClickCount() == 2) {
					List<Component> components = item.getPolicyEditorComponents();
		            for (Component component : components) {
		            	editorPanel.add(component);
					}
		            editorPanel.updateUI();
		        }
				else{
					item.setSelected(!item.isSelected());
					editorPanel.removeAll();
					editorPanel.updateUI();
				}
			}
			
		});
		frmTemplatePolicyEditor.getContentPane().setLayout(groupLayout);
		
		loadPolicyTemplates(templatesList, installedList);
		loadPolicyInstalled(installedList);
	}
	
	private void loadPolicyTemplates(JList templatesList, JList installedList){
		CheckList checkList = new CheckList(policyClass); 
		CheckableItem[] items = checkList.getItems();
		DefaultListModel model = (DefaultListModel) templatesList.getModel();
		for (int i = 0; i < items.length; i++) {
			CheckableItem checkableItem = items[i];
			checkableItem.registerListener(installedList);
			model.addElement(checkableItem);
		}
		templatesList.updateUI();
	}
	
	private void loadPolicyInstalled(JList installedList){
		CheckList checkList = new CheckList(installedList);
		CheckableItem[] items = checkList.getItems();
		DefaultListModel model = (DefaultListModel) installedList.getModel();
		for (int i = 0; i < items.length; i++) {
			CheckableItem checkableItem = items[i];
			model.addElement(checkableItem);
		}
		installedList.updateUI();
		
		//TODO: talk with the PMP. call getDeployedPolicies();
	}
	
	private List<String> getDeployedPolicies(){
		List<String> deployed = new ArrayList<String>();
		
		//Map<String, List<String>> mapList = clientPmp.listMechanisms();
				
		return deployed;
	}
	
	private void initializePMP(){
		String host = "localhost";
		int port = 0;
		try {
			Config config = new Config();
			String portString=config.getProperty("policyManagementPort");
			port = Integer.parseInt(portString);
			String hostString = config.getProperty("policyManagemtnHost");
			host = hostString;
		} catch (Exception e) {
		}
		
		try{ 
			clientPmp=thriftClientFactory.createPmp2PdpClient(new IPLocation(host, port));
			clientPmp.connect();
		} 
		catch(IOException ex){
			logger.errorLog("Pmp launch failed", ex);
		}
	}
}
