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
import java.util.ArrayList;
import java.util.List;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.policyeditor.deployment.DeploymentController;
import de.tum.in.i22.policyeditor.model.PolicyTemplate;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import javax.swing.border.CompoundBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;


/**
 * @author Cipri
 * Policy Templates Editor GUI
 */
public class PtEditor {

	private JFrame frmTemplatePolicyEditor;
	private final DeploymentController deploymentController;
	
	private String policyClass;
	private Set<IContainer> representations;
	
	private static final Logger _logger = LoggerFactory.getLogger(PtEditor.class); 

	/**
	 * Create the application.
	 * @param representations2 
	 */
	public PtEditor(DeploymentController deploymentController, Set<IContainer> representations2, String policyClass) {
		this.deploymentController = deploymentController;
		this.policyClass = policyClass;
		this.representations = representations2;
		initialize();
	}

	public static void startEditor(final DeploymentController deploymentController, final Set<IContainer> representations2, final String dataClass){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					ToolTipManager.sharedInstance().setInitialDelay(500);
			        ToolTipManager.sharedInstance().setDismissDelay(1500);
			        final PtEditor window = new PtEditor(deploymentController, representations2, dataClass);
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
		frmTemplatePolicyEditor.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        deploymentController.closeConnection();
		    }
		});
		
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
		
		final JList<CheckableItem> installedList = new JList<CheckableItem>();
		final DefaultListModel<CheckableItem> installedListModel = new DefaultListModel<CheckableItem>();
		installedList.setModel(installedListModel);
		final JList<CheckableItem> templatesList = new JList<CheckableItem>();
		DefaultListModel<CheckableItem> templatesListModel = new DefaultListModel<CheckableItem>();
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
				/* TEMPLATE SELECTED HANDLER */
				int index = templatesList.locationToIndex(e.getPoint());
				
				CheckableItem item = null;
				try {
					item = templatesList.getModel().getElementAt(index);
				} catch (Exception ex){}
				_logger.info("Templated selected item: "+ item);
				if(item == null){
					editorPanel.removeAll();
					editorPanel.updateUI();
					return;
				}
				
				editorPanel.removeAll();
				List<Component> components = item.getPolicyEditorComponents();
	            for (Component component : components) {
	            	editorPanel.add(component);
				}
	            editorPanel.updateUI();
			}
			
		});
		
		final JPanel installedPanel = new JPanel();
		installedPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Installed Policies", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(installedPanel);
		
		splitPane.setResizeWeight(0.5);
		splitPane.resetToPreferredSizes();
		
		JScrollPane installedScrollPane = new JScrollPane();
		
		JPanel installedButtonPanel = new JPanel();
		installedButtonPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		
		JButton btnDeployPolicies = new JButton("Deploy policies");
		btnDeployPolicies.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnDeployPolicies.setForeground(new Color(0, 153, 0));
		btnDeployPolicies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* DEPOLY POLICIES HANDLER */
				
				//group policies to deploy
				ListModel<CheckableItem> model = installedList.getModel();
				int policiesCounter = model.getSize();
				List<PolicyTemplate> policies = new ArrayList<PolicyTemplate>();
				for(int i=0; i<policiesCounter; i++){
					CheckableItem item = model.getElementAt(i);
					if(item.isSelected()){
						PolicyTemplate policy = item.getPolicy();
						policies.add(policy);
					}
				}
				deploymentController.deployPolicies(representations, policyClass, policies);
				
				//refresh the view with the deployed policies
				loadPolicyInstalled(installedList);
				installedList.removeAll();
				installedList.updateUI();
			}
		});
		installedButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		installedButtonPanel.add(btnDeployPolicies);
		
		JButton btnRefreshPolicies = new JButton("Refresh policies");
		btnRefreshPolicies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* REFRESH POLICIES HANDLER */
				installedList.removeAll();
				installedList.updateUI();
				loadPolicyInstalled(installedList);
				editorPanel.updateUI();
			}
		});
		installedButtonPanel.add(btnRefreshPolicies);
		
		JButton btnRevokePolicies = new JButton("Revoke policies");
		btnRevokePolicies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* REVOKE POLICIES HANDLER */
				
				//group policies to deploy
				ListModel<CheckableItem> model = installedList.getModel();
				int policiesCounter = model.getSize();
				List<PolicyTemplate> policies = new ArrayList<PolicyTemplate>();
				for(int i=0; i<policiesCounter; i++){
					CheckableItem item = model.getElementAt(i);
					if(item.isSelected()){
						PolicyTemplate policy = item.getPolicy();
						policies.add(policy);
					}
				}
				deploymentController.revokePolicies(representations, policyClass, policies);
				
				//refresh the view with the deployed policies
				loadPolicyInstalled(installedList);
				installedList.removeAll();
				installedList.updateUI();
			}
		});
		btnRevokePolicies.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnRevokePolicies.setForeground(new Color(204, 0, 0));
		installedButtonPanel.add(btnRevokePolicies);
		
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
				CheckableItem item = null;
				try {
					item = installedList.getModel().getElementAt(index);
				} catch (Exception ex){}
				_logger.info("Installed selected item: "+ item);
				if(item == null){
					editorPanel.removeAll();
					editorPanel.updateUI();
					return;
				}
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
	
	private void loadPolicyTemplates(JList<CheckableItem> templatesList, JList<CheckableItem> installedList){
		CheckList checkList = new CheckList(policyClass); 
		CheckableItem[] items = checkList.getItems();
		DefaultListModel<CheckableItem> model = (DefaultListModel<CheckableItem>) templatesList.getModel();
		for (int i = 0; i < items.length; i++) {
			CheckableItem checkableItem = items[i];
			checkableItem.registerListener(installedList);
			model.addElement(checkableItem);
		}
		//add dummy element for the white space
		CheckableItem checkableItem = new CheckableItem("");
		checkableItem.registerListener(installedList);
		model.addElement(checkableItem);
		
		templatesList.updateUI();
	}
	
	private void loadPolicyInstalled(JList<CheckableItem> installedList){
		List<PolicyTemplate> policies = this.deploymentController.getDeployedPolicies(policyClass);
		CheckList checkList = new CheckList(installedList);
		for(PolicyTemplate p : policies){
			checkList.addPolicy(p);
		}
		
		DefaultListModel<CheckableItem> model = (DefaultListModel<CheckableItem>) installedList.getModel();
		model.clear();
		
		CheckableItem[] items = checkList.getItems();
		for (int i = 0; i < items.length; i++) {
			CheckableItem checkableItem = items[i];
			checkableItem.instantiatePolicy(null);
		}
	}
}



