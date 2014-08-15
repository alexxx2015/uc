package de.tum.in.i22.uc.blocks.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;

import org.apache.commons.io.FilenameUtils;
//import de.tum.in.i22.uc.policy.deployment.DeploymentController;
//import de.tum.in.i22.uc.policy.deployment.DeploymentException;
import org.w3c.dom.Document;





//import de.tum.in.i22.uc.blocks.workspace.blockevents.FileMenuAction;
import de.tum.in.i22.uc.policy.translation.Filter.FilterStatus;
import de.tum.in.i22.uc.policy.translation.TranslationController;
import de.tum.in.i22.uc.utilities.PublicMethods;

public class JDTranslationView extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField jtfSource;
	private JTextField jtfDestination;
	private JButton btnSource;
	private JButton btnDestination;
	private JTextArea jtaStatus;
	private JCheckBox jckDeploy;
	private JButton btnTranslate;
	private JButton btnCancel;
	private JFileChooser fcDialogBox;
	private TranslationController transController;
	//private DeploymentController deplController;
	private JTextField jtfPIPAddress;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			JDTranslationView dialog = new JDTranslationView(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Open this tranlsation view.
	 * 
	 * @param parent
	 */
	public static void launch(Container parent){
		JDTranslationView view=new JDTranslationView(parent);	
		view.setLocationRelativeTo(parent);
		view.setVisible(true);
	}
	
	
	/**
	 * 
	 * @param s
	 * @return returns a proper file name.
	 */
	private String getCorrectFileName(String s){
		String sTrueFileName="";
		int iFirstOccur=s.indexOf(".xml", 0);		
		if(iFirstOccur==-1){
			sTrueFileName=s+".xml";												
		}					
		else{			
			if(iFirstOccur>0){
				String sFile=s.substring(0, iFirstOccur);	
				sTrueFileName=sFile+".xml";								
			}
		}
		return sTrueFileName;
	}
	
	/**
	 * Enable or disable fields.
	 * 
	 * @param disable
	 */
	private void controlFields(boolean enable){
		jtfSource.setEnabled(enable);
		jtfDestination.setEnabled(enable);
		jckDeploy.setEnabled(enable);
		btnSource.setEnabled(enable);
		btnDestination.setEnabled(enable);
	}
	
	public void setStatus(String text){
		if(jtaStatus.getText().equals(""))
			jtaStatus.setText(text);
		else {
			String sOld=jtaStatus.getText();
			jtaStatus.setText(sOld+"\n"+text);
		}
	}

	/**
	 * Create the dialog.
	 */
	public JDTranslationView(Container container) {
		super((Frame)container);
		setTitle("Policy Translation");
		setResizable(false);
//		setBounds(100, 100, 461, 305);
		setBounds(100, 100, 577, 543);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 103, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 102, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			btnSource = new JButton("Source File");
			btnSource.setToolTipText("Specification-level policy to translate");
			btnSource.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try{
						fcDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
						fcDialogBox.setDialogTitle("Select a policy file to open");
						fcDialogBox.setCurrentDirectory(new File(System.getProperty("user.dir")));
						int iReturn=fcDialogBox.showOpenDialog(JDTranslationView.this);
						//code to open policy file
						if(iReturn==JFileChooser.APPROVE_OPTION)
							jtfSource.setText(getCorrectFileName(fcDialogBox.getSelectedFile().getAbsolutePath()));
						
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			GridBagConstraints gbc_btnSource = new GridBagConstraints();
			gbc_btnSource.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnSource.insets = new Insets(0, 0, 5, 5);
			gbc_btnSource.gridx = 0;
			gbc_btnSource.gridy = 0;
			contentPanel.add(btnSource, gbc_btnSource);
		}
		{
			jtfSource = new JTextField();
			GridBagConstraints gbc_jtfSource = new GridBagConstraints();
			gbc_jtfSource.gridwidth = 5;
			gbc_jtfSource.insets = new Insets(0, 0, 5, 0);
			gbc_jtfSource.fill = GridBagConstraints.HORIZONTAL;
			gbc_jtfSource.gridx = 1;
			gbc_jtfSource.gridy = 0;
			contentPanel.add(jtfSource, gbc_jtfSource);
			jtfSource.setColumns(10);
			String sSourceFile="";
//			if(FileMenuAction.fCurrentFile!=null)
//				sSourceFile=FileMenuAction.fCurrentFile.getAbsolutePath();
			//TODO: add source file
			jtfSource.setText(sSourceFile);
		}
		{
			btnDestination = new JButton("Destination File");
			btnDestination.setToolTipText("Specify custom destination. If unspecified, ECA rules will be saved to the default location");
			btnDestination.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try{
						fcDialogBox.setDialogType(JFileChooser.SAVE_DIALOG);
						fcDialogBox.setDialogTitle("Select destionation file name");
						fcDialogBox.setCurrentDirectory(new File(System.getProperty("user.dir")));
						int iReturn=fcDialogBox.showOpenDialog(JDTranslationView.this);
						//code to open policy file
						if(iReturn==JFileChooser.APPROVE_OPTION){
							String sFileName=getCorrectFileName(fcDialogBox.getSelectedFile().getAbsolutePath());												
							jtfDestination.setText(sFileName);
								
						}						
						
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
				}
			});
			GridBagConstraints gbc_btnDestination = new GridBagConstraints();
			gbc_btnDestination.insets = new Insets(0, 0, 5, 5);
			gbc_btnDestination.gridx = 0;
			gbc_btnDestination.gridy = 1;
			contentPanel.add(btnDestination, gbc_btnDestination);
		}
		{
			jtfDestination = new JTextField();
			GridBagConstraints gbc_jtfDestination = new GridBagConstraints();
			gbc_jtfDestination.gridwidth = 5;
			gbc_jtfDestination.insets = new Insets(0, 0, 5, 0);
			gbc_jtfDestination.fill = GridBagConstraints.HORIZONTAL;
			gbc_jtfDestination.gridx = 1;
			gbc_jtfDestination.gridy = 1;
			contentPanel.add(jtfDestination, gbc_jtfDestination);
			jtfDestination.setColumns(10);
		}
		{
			lblNewLabel_1 = new JLabel(" ");
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 0;
			gbc_lblNewLabel_1.gridy = 2;
			contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		}
		{
			JLabel lblStatus = new JLabel("Status:");
			GridBagConstraints gbc_lblStatus = new GridBagConstraints();
			gbc_lblStatus.anchor = GridBagConstraints.WEST;
			gbc_lblStatus.insets = new Insets(0, 0, 5, 5);
			gbc_lblStatus.gridx = 0;
			gbc_lblStatus.gridy = 3;
			contentPanel.add(lblStatus, gbc_lblStatus);
		}
		{
			jtaStatus = new JTextArea();
			jtaStatus.setLineWrap(true);
			jtaStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
			jtaStatus.setEditable(false);
			jtaStatus.setBorder(new LineBorder(new Color(0, 0, 0)));
			GridBagConstraints gbc_jtaStatus = new GridBagConstraints();
			gbc_jtaStatus.gridheight = 2;
			gbc_jtaStatus.gridwidth = 6;
			gbc_jtaStatus.insets = new Insets(0, 0, 5, 0);
			gbc_jtaStatus.fill = GridBagConstraints.BOTH;
			gbc_jtaStatus.gridx = 0;
			gbc_jtaStatus.gridy = 4;
			contentPanel.add(jtaStatus, gbc_jtaStatus);
		}
		{
			jckDeploy = new JCheckBox("Instantiate & deploy policy now.");
			GridBagConstraints gbc_jckDeploy = new GridBagConstraints();
			gbc_jckDeploy.anchor = GridBagConstraints.WEST;
			gbc_jckDeploy.gridwidth = 6;
			gbc_jckDeploy.gridx = 0;
			gbc_jckDeploy.gridy = 6;
			contentPanel.add(jckDeploy, gbc_jckDeploy);
			jckDeploy.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent ce) {
					
					//jtfPIPAddress.setEnabled(jckDeploy.isSelected());
				}
				
			});
		}
		{
			/*lblNewLabel = new JLabel("PIP Server Address");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel.gridwidth = 2;
			gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 5;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);*/
		}
		{
			/*jtfPIPAddress = new JTextField();
			jtfPIPAddress.setEnabled(false);
			jtfPIPAddress.setText("localhost");
			GridBagConstraints gbc_jtfPIPAddress = new GridBagConstraints();
			gbc_jtfPIPAddress.gridwidth = 3;
			gbc_jtfPIPAddress.insets = new Insets(0, 0, 0, 5);
			gbc_jtfPIPAddress.fill = GridBagConstraints.HORIZONTAL;
			gbc_jtfPIPAddress.gridx = 2;
			gbc_jtfPIPAddress.gridy = 5;
			contentPanel.add(jtfPIPAddress, gbc_jtfPIPAddress);
			jtfPIPAddress.setColumns(10);*/
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnTranslate = new JButton("Translate");
				btnTranslate.addActionListener(new ActionListener() {
					private JDTranslationView PolicyCreator;

					public void actionPerformed(ActionEvent arg0) {
						controlFields(false);
						jtaStatus.setText("");
						String sSrc=jtfSource.getText();
						String sDest=jtfDestination.getText();
						if(sSrc.equals("")){
							setStatus("- Specification-level policy file missing.");							
							controlFields(true);
							return;							
						}
						if(sDest.equals("")){
							sDest=FilenameUtils.removeExtension(sSrc)+"_ilp.xml";
							setStatus("ILP will be written in: "+sDest);
						}
						
						//1st: Create a policy file from a diagram file
						de.tum.in.i22.uc.policy.translation.PolicyCreator policyCreator = new de.tum.in.i22.uc.policy.translation.PolicyCreator(sSrc);
						policyCreator.filter();	
						FilterStatus fStatus=policyCreator.getFilterStatus();
						String sMessage=policyCreator.getMessage();
						if(fStatus==FilterStatus.FAILURE) return;
						setStatus(sMessage);
						DOMResult policy = policyCreator.getOutput();
				        try {
							System.out.println("policy stripped is:"+ PublicMethods.TransformDomresultToString(policy));
						} catch (TransformerException e) {
							e.printStackTrace();
						}
						transController=new TranslationController((Document) policy.getNode());
						
						setStatus("- Translation started.");
						transController.filter();
						
						//in case of error
						if(transController.getFilterStatus()==FilterStatus.FAILURE){
							setStatus(transController.getMessage());
							controlFields(true);
							return;
						}
						else
							setStatus("- Translation successful.");
						
						String translatedPolicy = transController.getFinalOutput();
						FileWriter fw;
						try {
							fw = new FileWriter(sDest);
							fw.write(translatedPolicy);
							fw.close();
						} catch (IOException e) {
							setStatus("- Error when writing to file.");
						}
						
						
						if(jckDeploy.isSelected()){											
							
							/*deplController=new DeploymentController(sDest,JDTranslationView.this);
							try {
								deplController.instantiate();
								deplController.deploy();
							} catch (DeploymentException e) {
								
								setStatus(e.getMessage());								
							}*/
														
						}
						//enable our fields
						controlFields(true);
					}
				});
				btnTranslate.setActionCommand("OK");
				buttonPane.add(btnTranslate);
				getRootPane().setDefaultButton(btnTranslate);
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.setPreferredSize(new Dimension(77, 23));
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
				btnCancel.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						JDTranslationView.this.setVisible(false);
						JDTranslationView.this.dispose();
					}
					
					
				});
			}
		}
	
		//preparing dialog box for open and save commands
		fcDialogBox=new JFileChooser();		
		FileFilter fnefFilter=new FileNameExtensionFilter("Policy Files (.xml)","xml");		
		fcDialogBox.setFileFilter(fnefFilter);
	
	}

}
