package de.tum.in.i22.uc.policy.translation.ecacreation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.Font;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.Dimension;

public class JDialogEAParameter extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton btnOk;
	private JButton btnCancel;
	private JLabel lblName;
	private JButton btnAddRow;
	private JLabel lblParameters;
	private JTextField jtfName;
	private JScrollPane jspParameter;
	private JTable jtParameter;
	private JLabel lblNewLabel;
	private JLabel lblPressEnterKey;
	//
	private SubformulaEvent entity;
	private ECARulesCreatorController controller;
	private int iSelectedAction;
	//this can be event or action
	private String sReason;
	//
	private Object UIView;	
	private JButton btnDeleteRow;
	//	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			JDialogEAParameter dialog = new JDialogEAParameter(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void launch(String reason, int index, ECARulesCreatorController controller, Container container, Object view){
		try{
			JDialogEAParameter dialog=new JDialogEAParameter(container);
			dialog.setReason(reason);
			dialog.customize();
			dialog.setController(controller);
			dialog.setSelectedAction(index);
			dialog.setEntity();
			dialog.setView(view);
			dialog.populateUI();			
			dialog.setVisible(true);			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setController(ECARulesCreatorController controller){
		this.controller=controller;
	}
	
	private void setSelectedAction(int index){
		this.iSelectedAction=index;
	}
	
	/**
	 * This is the JTextbox that shows user defined
	 * trigger event or the JList that shows actions
	 * for the 'execute' option.
	 * 
	 * @param view
	 */
	private void setView(Object view){
		UIView=view;
	}
	
	private void updateView(){
		if(UIView instanceof ECARulesCreatorView){
			ECARulesCreatorView ecv=(ECARulesCreatorView)UIView;
			ecv.showUserConfiguredEvent();
		}
		else if(UIView instanceof JPanelExecuteAction){
			JPanelExecuteAction pea=(JPanelExecuteAction) UIView;
			pea.showExecuteActions();
		}
	}
	
	/**
	 * Sets the purpose for which this UI is called: event or action.
	 * 
	 * @param reason
	 */
	private void setReason(String reason){
		sReason=reason;
	}
	
	/**
	 * Sets the entity which is been edited in 
	 * this UI. It can be an event or an action.
	 * 
	 * @param entity
	 */
	private void setEntity(){
		entity=null;
		if(controller!=null){
			if(iSelectedAction==-2){
				if(sReason.toLowerCase().equalsIgnoreCase("event"))
					entity=controller.getUserDefinedEvent();
			}
			else if(iSelectedAction>=-1){
				if(sReason.toLowerCase().equalsIgnoreCase("action"))
					{
						if(iSelectedAction!=-1)
						entity=controller.getExecuteAction(iSelectedAction);
					}
			}
		}		
	}
	
	/**
	 * This dialog can be launched from the event and
	 * action sections of ECA UI. So, for which ever case
	 * this dialog has to be customized.
	 * 
	 * @param reason
	 */
	private void customize(){
		//customize dialog title
		setTitle("Specify "+sReason+" Details");
		//customize name of label
		lblName.setText(sReason+" Name");
	}
	
	/**
	 * Fills this UI with name of event/action with
	 * its associated parameters.
	 * 
	 * @param controller
	 */
	public void populateUI(){
		if(entity!=null){
			//set name
			jtfName.setText(entity.getEventName());
			//set the parameters
			if(entity.getParameters()!=null){
				if(entity.getParameters().size()>0){
					DefaultTableModel model=(DefaultTableModel)jtParameter.getModel();
					for(int i=0; i<entity.getParameters().size(); ++i){
						SubformulaEventParameter param=entity.getParameters().get(i);					
						Object []data={param.getName(), param.getValue()};
						model.addRow(data);
					}
				}
			}
		}
	}
	
	
	/**
	 * Called to build a concrete (trigger) event or action
	 * instance.
	 * 
	 */
	private void save(){
		//Get the name
		String sEventName="";
		sEventName=jtfName.getText();
		//we need @ least a name to have an event or action
		if(sEventName.equals(""))return;
		
		//Build the parameters
		ArrayList<SubformulaEventParameter> alParameter=new ArrayList<SubformulaEventParameter>();
		DefaultTableModel model=(DefaultTableModel)jtParameter.getModel();
		if(model.getRowCount()<1) {
			ECARulesCreatorView ecv;
			if(UIView instanceof ECARulesCreatorView)
				ecv=(ECARulesCreatorView)UIView;
			else{
				JPanelExecuteAction panel=(JPanelExecuteAction)UIView;
				ecv=(ECARulesCreatorView)panel.getView();
			}
			ecv.jtfError.setText(sReason+" {"+sEventName+"} not added because parameters not provided.");
			return;
		}
		for(int i=0; i<model.getRowCount(); ++i)	
		{
			String sParamName="";
			if(model.getValueAt(i, 0)!=null)
			sParamName=model.getValueAt(i, 0).toString();
			String sParamValue="";
			if(model.getValueAt(i, 1)!=null)
			sParamValue=model.getValueAt(i, 1).toString();
			alParameter.add(new SubformulaEventParameter(sParamName,sParamValue));
		}
		
		//reconciling
		SubformulaEvent ea=new SubformulaEvent(sEventName,alParameter);
		//
		if(sReason.toLowerCase().equals("event"))
			controller.setUserDefinedEvent(ea);		
		else if(sReason.toLowerCase().equals("action")){
			//no selection made
			if(iSelectedAction==-1){
				controller.addExecuteAction(ea);
			}
			else{
				controller.modifyExecuteAction(iSelectedAction, ea);
			}
		}
		
		updateView();
	}

	/**
	 * Create the dialog.
	 */
	public JDialogEAParameter(Container container) {
		super((Frame) container);		
		setModal(true);
		setResizable(false);
		setBounds(100, 100, 392, 327);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{92, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			lblName = new JLabel("Name");
			lblName.setFont(new Font("Tahoma", Font.BOLD, 11));
			GridBagConstraints gbc_lblName = new GridBagConstraints();
			gbc_lblName.anchor = GridBagConstraints.WEST;
			gbc_lblName.insets = new Insets(0, 0, 5, 5);
			gbc_lblName.gridx = 0;
			gbc_lblName.gridy = 0;
			contentPanel.add(lblName, gbc_lblName);
		}
		{
			jtfName = new JTextField();
			GridBagConstraints gbc_jtfName = new GridBagConstraints();
			gbc_jtfName.gridwidth = 5;
			gbc_jtfName.fill = GridBagConstraints.HORIZONTAL;
			gbc_jtfName.insets = new Insets(0, 0, 5, 0);
			gbc_jtfName.gridx = 1;
			gbc_jtfName.gridy = 0;
			contentPanel.add(jtfName, gbc_jtfName);
			jtfName.setColumns(10);
		}
		{
			lblParameters = new JLabel("Parameters");
			lblParameters.setFont(new Font("Tahoma", Font.BOLD, 11));
			GridBagConstraints gbc_lblParameters = new GridBagConstraints();
			gbc_lblParameters.anchor = GridBagConstraints.WEST;
			gbc_lblParameters.insets = new Insets(0, 0, 5, 5);
			gbc_lblParameters.gridx = 0;
			gbc_lblParameters.gridy = 1;
			contentPanel.add(lblParameters, gbc_lblParameters);
		}
		{
			jspParameter = new JScrollPane();
			GridBagConstraints gbc_jspParameter = new GridBagConstraints();
			gbc_jspParameter.gridheight = 6;
			gbc_jspParameter.gridwidth = 6;
			gbc_jspParameter.insets = new Insets(0, 0, 5, 0);
			gbc_jspParameter.fill = GridBagConstraints.BOTH;
			gbc_jspParameter.gridx = 0;
			gbc_jspParameter.gridy = 2;
			jspParameter.setColumnHeaderView(new JLabel("Parameters"));
			contentPanel.add(jspParameter, gbc_jspParameter);
			{
				jtParameter = new JTable();
				jtParameter.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"Name", "Value"
					}
				) {
					Class[] columnTypes = new Class[] {
						String.class, String.class
					};
					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});
				jtParameter.getColumnModel().getColumn(0).setPreferredWidth(100);
				jtParameter.getColumnModel().getColumn(1).setPreferredWidth(142);
				jspParameter.setViewportView(jtParameter);
			}
		}
		{
			btnAddRow = new JButton("Add Row");
			btnAddRow.setPreferredSize(new Dimension(87, 23));
			GridBagConstraints gbc_btnAddRow = new GridBagConstraints();
			gbc_btnAddRow.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnAddRow.insets = new Insets(0, 0, 5, 5);
			gbc_btnAddRow.gridx = 0;
			gbc_btnAddRow.gridy = 8;
			contentPanel.add(btnAddRow, gbc_btnAddRow);
			btnAddRow.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					DefaultTableModel model=(DefaultTableModel) jtParameter.getModel();
					Object[] object={};
					model.addRow(object);
				}
				
			});
		}
		{
			btnDeleteRow = new JButton("Delete Row");
			GridBagConstraints gbc_btnDeleteRow = new GridBagConstraints();
			gbc_btnDeleteRow.insets = new Insets(0, 0, 5, 5);
			gbc_btnDeleteRow.gridx = 1;
			gbc_btnDeleteRow.gridy = 8;
			contentPanel.add(btnDeleteRow, gbc_btnDeleteRow);
			btnDeleteRow.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					DefaultTableModel model=(DefaultTableModel) jtParameter.getModel();
					int iSel=jtParameter.getSelectedRow();
					if(iSel>-1){
						model.removeRow(iSel);						
					}
				}
				
			});
		}
		{
			lblNewLabel = new JLabel("Double-click in table cells to enter/edit values.");
			lblNewLabel.setForeground(new Color(0, 128, 0));
			lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
			lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel.gridwidth = 7;
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 9;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			lblPressEnterKey = new JLabel("Use enter key to add new rows");
			lblPressEnterKey.setForeground(new Color(0, 128, 0));
			lblPressEnterKey.setFont(new Font("Tahoma", Font.BOLD, 11));
			GridBagConstraints gbc_lblPressEnterKey = new GridBagConstraints();
			gbc_lblPressEnterKey.gridwidth = 6;
			gbc_lblPressEnterKey.anchor = GridBagConstraints.WEST;
			gbc_lblPressEnterKey.insets = new Insets(0, 0, 0, 5);
			gbc_lblPressEnterKey.gridx = 0;
			gbc_lblPressEnterKey.gridy = 10;
			contentPanel.add(lblPressEnterKey, gbc_lblPressEnterKey);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnOk = new JButton("Save");
				btnOk.setPreferredSize(new Dimension(65, 23));
				btnOk.setActionCommand("OK");
				buttonPane.add(btnOk);
				getRootPane().setDefaultButton(btnOk);
				btnOk.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						
						save();
						JDialogEAParameter.this.setVisible(false);
						JDialogEAParameter.this.dispose();
					}
					
				});
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
				btnCancel.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						
						JDialogEAParameter.this.setVisible(false);
						JDialogEAParameter.this.dispose();
					}
					
				});
			}
		}
			
		jtParameter.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				
				DefaultTableModel model=(DefaultTableModel) jtParameter.getModel();
				Object[] object={};
				model.addRow(object);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
				
			}
			
			
		});		
	
	}

}

