package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import de.tum.in.i22.uc.ptp.utilities.PublicMethods;

public class ECARulesCreatorView extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField jtfEventText;
	private JTextField jtfSubformula;
	private JTextField jtfConditionText;
	private ButtonGroup bgEvent;
	private ButtonGroup bgCondition;
	private ArrayList<Subformula> alSubformula;
	private int iCurrentSubformula;
	private JLabel lblEcaRule;
	private JRadioButton rbAny;
	private JRadioButton rbSelectEvent;
	private JComboBox jcbEvents;
	private JRadioButton rbEventText;
	private JRadioButton rbSubformula;
	private JRadioButton rbConditionText;
	private JRadioButton rbTrue;
	private JPanel jpAction;
	private JComboBox jcbAction;
	private JButton btnPrevious;
	private JButton btnNext;
	private JButton btnCancel;
	private JPanelModifyAction jpModify;
	private JPanelExecuteAction jpExecute;
	private JPanelDelayAction jpDelay;
	private ECARulesCreatorController controller;
	public JTextField jtfError;
	private JButton btnParameters;	
	private JSeparator jsHorTop;
	private JLabel lblEnforcementType;
	private JButton btnEditSubformula;
	private JButton btnEditXpath;
	private static boolean bUserCancelled;
	public static int initSFSize;

	/**
	 * Launch the application.
	 * commented because this test 
	 * is no longer needed
	 */
//	public static void main(String[] args) {
//		try {
//			ECARulesCreatorView.launch(new ArrayList<Subformula>(), new JDialog());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}		
	
public static void launch(ArrayList<Subformula> subformulas, Container container){
	       initSFSize = subformulas.size();
		try{
			//
			ECARulesCreatorView dialog = new ECARulesCreatorView(container);			
			dialog.assignButtonGroups();
			dialog.alSubformula=subformulas;
			dialog.iCurrentSubformula=1;
			//disable previous button
			dialog.btnPrevious.setEnabled(false);
			//
			dialog.indicateCurrentSubformula();
			//
			dialog.bUserCancelled=true;
			//			
			dialog.populateUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
		catch(Exception e){
			e.printStackTrace();
		}								
	}
	
	/**
	 * 
	 * @return Returns if this dialog was cancelled by the 
	 * user or not.
	 */
	public static boolean isUserCancelled(){
		return bUserCancelled;
	}
	
	/**
	 * Put radio buttons of trigger events and condition 
	 * into separate groups
	 */
	private void assignButtonGroups(){
		//Trigger event
		bgEvent=new ButtonGroup();
		bgEvent.add(this.rbAny);
		bgEvent.add(this.rbEventText);
		bgEvent.add(this.rbSelectEvent);
		//Condition
		bgCondition=new ButtonGroup();
		bgCondition.add(this.rbConditionText);
		bgCondition.add(this.rbSubformula);
		bgCondition.add(this.rbTrue);
	}
	
	/**
	 * Called upon user clicking next button. 
	 */
	private void configureNextECARule(){
		if(iCurrentSubformula<=alSubformula.size()){
			++iCurrentSubformula;
			btnPrevious.setEnabled(true);
			if(iCurrentSubformula==alSubformula.size())
				btnNext.setText("Finish");
		}
		indicateCurrentSubformula();
		populateUI();
	}
	
	/**
	 * Called upon user clicking previous button
	 */
	private void configurePreviousECARule(){
		if(iCurrentSubformula>1){
			--iCurrentSubformula;
			if(iCurrentSubformula==1)
				btnPrevious.setEnabled(false);
			if(iCurrentSubformula!=alSubformula.size())
				btnNext.setText("Next");
		}
		indicateCurrentSubformula();
		populateUI();
	}
	
	/**
	 * Shows the currently-operated subformula
	 */
	private void indicateCurrentSubformula(){
		String sCurrent=String.valueOf(iCurrentSubformula);
		lblEcaRule.setText("ECA Rule #"+sCurrent+" of "+alSubformula.size());
	}
	
	public ArrayList<SubformulaEvent> alEvents;
	
	/**
	 * Load values of the current subformula into
	 * the user interface
	 */
	@SuppressWarnings("rawtypes")
	private void populateUI(){
		if(alSubformula.size() == 0)
			return;
		Subformula curSubformula=alSubformula.get(iCurrentSubformula-1);
		controller.setCurrentSubformula(curSubformula);
		ECARule ecaRule=controller.getECARule();
		System.out.println("\nCurrent subformula:\n"+ curSubformula + "\n");
		//Populate the trigger event part. The condition part
		//is handled automatically for some trigger event
		alEvents=curSubformula.getEvents();	
		javax.swing.DefaultComboBoxModel dcm=new javax.swing.DefaultComboBoxModel();
		if(alEvents.size()==1){
			dcm.addElement(alEvents.get(0).getEventName());
			jcbEvents.setModel(dcm);			
		}
		else{
			// 1. remove duplicate event names
			alEvents = new PublicMethods().removeDuplicates(alEvents);
			
			// 2. remove policy activation event from the list of trigger event names
			for(int i=0; i<alEvents.size(); ++i){
				if(alEvents.get(i).getEventName().equalsIgnoreCase("activateMechanism")) {
					alEvents.remove(i);
					break;
				}
			}

			// 3. add all the left event names to the dropdown data model
			for(int i=0; i<alEvents.size(); ++i){
				dcm.addElement(alEvents.get(i).getEventName());
				
			}
			jcbEvents.setModel(dcm);			
		}
		
		if(ecaRule!=null){
			//1. helps to populate the UI if with previously
			//set trigger event.
			String eventName="";
			if(ecaRule.getEvent()!=null) 
			eventName=ecaRule.getEvent().getEventName();
			//select radio button
			JRadioButton button=(JRadioButton)controller.getButtonForTriggerEvent();
			button.setSelected(true);
			//
			
			//if(!button.getText().equalsIgnoreCase(rbAny.getText())){
			if(button.getLocation().y!=rbAny.getLocation().y){
				if(!eventName.equals("")){
					if(button.getText().equalsIgnoreCase(rbSelectEvent.getText()))									
						jcbEvents.setSelectedItem(eventName);
					else
						jtfEventText.setText(eventName);
				}
			}
			
			//2. condition
			JRadioButton rButton=(JRadioButton)controller.getButtonForCondition();
			if(ecaRule.getCondition()!=null){
				String sCondition=(String)ecaRule.getCondition();					
				if(rButton.getText().equalsIgnoreCase(rbSubformula.getText())){
					rbSubformula.setSelected(true);
					jtfSubformula.setText(sCondition);
				}					
				else if(rButton.getText().equalsIgnoreCase(rbConditionText.getText())){
					rbConditionText.setSelected(true);
					jtfConditionText.setText(sCondition);
				}
				else rbTrue.setSelected(true);
			}
			
			//3. action
			jcbAction.setSelectedIndex(controller.getIndexSelectedAction());			
			
		}
		else{
			//fresh start and no eca rule
			//1 & 2. Set the trigger event which
			//automatically selects the condition
			//part
			if(alEvents.size()==1){
				rbSelectEvent.setSelected(true);
				jcbEvents.setSelectedIndex(-1);
				jcbEvents.setSelectedIndex(0);
				//since a specific event has been selected
				//condition evaluates to true
				rbTrue.setSelected(true);
			}
			else if(alEvents.size()>1) {
				rbSelectEvent.setSelected(true);
				jcbEvents.setSelectedIndex(-1);
				//since no specific condition has been
				//selected, we display subformula
				rbSubformula.setSelected(true);						
			}
			else {
				rbAny.setSelected(true);
				//since no specific condition has been
				//selected, we display subformula
				rbSubformula.setSelected(true);				
			}
			
		}
		
	}
	
	private void init(){		
		//
		controller=new ECARulesCreatorController();		
		
		jpModify=new JPanelModifyAction();		
		jpModify.setPreferredSize(new Dimension(280,100));	
		
		jpExecute=new JPanelExecuteAction(this);
		jpExecute.setParentContainer(getParent());
		jpExecute.setController(controller);
		jpExecute.setPreferredSize(new Dimension(240,146));
		
		jpDelay=new JPanelDelayAction();
		jpDelay.setPreferredSize(new Dimension(285,100));				
	
	}
	
	/**
	 * 
	 * @return Returns the selected radio button for
	 * trigger event.
	 */
	private JRadioButton getSelectedEventButton(){		
		JRadioButton button;
		if(rbAny.isSelected())					
		 button=rbAny;
		else if(rbSelectEvent.isSelected())
		 button=rbSelectEvent;
		else button=rbEventText;
		return button;
	}
	
	/**
	 * 
	 * @return Returns the selected radio button for
	 * condition.
	 */
	private JRadioButton getSelectedConditionButton(){		
		JRadioButton button;
		if(rbSubformula.isSelected())					
		 button=rbSubformula;
		else if(rbConditionText.isSelected())
		 button=rbConditionText;
		else button=rbTrue;
		return button;
	}
	
	/**
	 * First determines the event configured by user and then returns
	 * it.
	 * 
	 * @param isNext flag to know if user clicked next button
	 * @return Returns configured event part of ECA Rule
	 * @throws ECAInputException
	 */
	private Object getConfiguredTriggerEvent(boolean isNext) throws ECAInputException{
		if(rbAny.isSelected())					
			 return "any";
		else if(rbSelectEvent.isSelected())
		{
			int i=jcbEvents.getSelectedIndex();
			if(jcbEvents.getSelectedItem()==null){			
				if(isNext)
				throw new ECAInputException("Please, select an event.");
				else return null;
			}			
			return alSubformula.get(iCurrentSubformula-1).getEvents().get(i);
			
		}
		else {
			String sText=jtfEventText.getText();
			if(sText.equals("")) {
				if(isNext)
				throw new ECAInputException("Please, specify event text");
			}
			return sText;
		}
	}
	
	/**
	 * First determines the condition configured by user and then returns
	 * it.
	 * 
	 * @param isNext flag to know if user clicked next button
	 * @return Returns configured condition part of ECA Rule
	 * @throws ECAInputException
	 */
	private Object getConfiguredCondition(boolean isNext) throws ECAInputException{
		
		if(rbSubformula.isSelected()){
			return this.jtfSubformula.getText();
		}			 
		else if(rbConditionText.isSelected()){
			String sText="";
			sText=jtfConditionText.getText();
			if(sText.equals("")) {
				if(isNext)
				throw new ECAInputException("Please, specify condition text.");				
			}
			return sText;
		}			 
		else {
			return "true";
		}
		
	}
	
	/**
	 * 
	 * @param isNext flag to know if user clicked next button
	 * @return Returns the configured action part of an ECA Rule
	 * @throws ECAInputException 
	 */
	private Object getConfiguredAction(boolean isNext) throws ECAInputException{
		if(jcbAction.getSelectedIndex()==0){
			if(isNext)
			throw new ECAInputException("Please, specify an action.");
			else return null;
		}
		if(jcbAction.getSelectedIndex()==1)
			return "inhibit";
		else{
			if(jpAction.getComponents().length==0) return null;
			ECAAction actionPanel=(ECAAction)jpAction.getComponent(0);
			return actionPanel.getActionResult();
		}		
	}
	
	/**
	 * For debugging purposes to see how each ECA rule is
	 * configured during forward and backward traversals.
	 */
	private void print(){		
		ECARule ecaRule=controller.getECARule();
		if(ecaRule==null) return;
		SubformulaEvent se=ecaRule.getEvent();
		if(se==null)return;
		System.out.println("Event name: "+se.getEventName());
		System.out.println("Condition: "+ecaRule.getCondition().toString());
		if(ecaRule.getAction() instanceof String)
			System.out.println("Action: "+ecaRule.getAction().toString());
		else{
			@SuppressWarnings("unchecked")
			ArrayList<Object> action=(ArrayList<Object>)ecaRule.getAction();
			if(action!=null)
			System.out.println("Action: "+action.size());
		}
	}
	
	/**
	 * Clear values of UI elements and prepare for
	 * next round of ECA rule configuration.
	 */
	private void clearUI(){
		jtfEventText.setText("");
		jtfSubformula.setText("");
		jtfConditionText.setText("");
		jtfError.setText("");
		jcbAction.setSelectedIndex(0);
	}
	
	/**
	 * Displays error at the bottom of this UI in
	 * red color. 
	 * 
	 * @param text
	 */
	public void setErrorText(String text){
		jtfError.setText(text);
	}
	
	/**
	 * Responsible for displaying the various UI views
	 * for the different actions: inhibit, modify, execute and
	 * delay.
	 * 
	 * @param selAction
	 * @param populateUI
	 * @param action
	 */
	private void prepareActionUI(int selAction, boolean populateUI, Object action){
		
			//Remove previously added UI
			jpAction.removeAll();
			
				switch (selAction){
					case 0: case 1:
						//do nothing
						break;
					case 2:		
						if(populateUI) jpModify.populateUI(action);
						else{
							//populate UI with values of selected 
							//event if any
							if(jcbEvents.getSelectedItem()!=null){
								int i=jcbEvents.getSelectedIndex();
//								Subformula curSubformula=alSubformula.get(iCurrentSubformula-1);
								jpModify.popuateUI(alEvents.get(i));
							}
							
						}
						jpAction.add(jpModify);							
						break;
					case 3:				
						if(populateUI) jpExecute.populateUI(action);						
						jpAction.add(jpExecute);
						break;
					case 4:			
						jpDelay.populateUI(action);						
						jpAction.add(jpDelay);									
						break;
				}
				
			//update the action panel
			jpAction.revalidate();
			jpAction.repaint();	
	}
	
	public void showUserConfiguredEvent(){
		SubformulaEvent event=controller.getUserDefinedEvent();
		if(event!=null){
			jtfEventText.setText(event.getEventName());
		}
	}
	
	public void setSubformula(String text){
		jtfSubformula.setText(text);
	}
	
	public String getSubformula(){
		return jtfSubformula.getText();
	}
	
	public void setSubformulaXpath(String text){
		jtfConditionText.setText(text);
	}
	
	public String getSubformulaXpath(){
		return jtfConditionText.getText();
	}

	/**
	 * Create the dialog.
	 */
	public ECARulesCreatorView(Container container) {
		super((Frame) container.getParent());
		//init();
		setModal(true);
		setTitle("Configure ECA FTPRules");
		setBounds(100, 100, 577, 543);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[14.00][4.00][18.00][105.00,grow][][24.00][][16.00][10.00][0][0][25.00][133.00][][][-2.00]", "[][][][][][][][][-18.00][][][grow][]"));
		{			
			{
				lblEcaRule = new JLabel("ECA Rule #");
				lblEcaRule.setForeground(new Color(0, 128, 0));
				lblEcaRule.setFont(new Font("Tahoma", Font.BOLD, 12));
				contentPanel.add(lblEcaRule, "cell 0 0 4 1");
			}
		}
		{
			jsHorTop = new JSeparator();
			jsHorTop.setForeground(Color.BLACK);
			Dimension dim=getPreferredSize();
			jsHorTop.setPreferredSize(new Dimension(dim.width,5));
			contentPanel.add(jsHorTop, "cell 0 1 15 1,growx");
		}
		{
			JLabel lblTriggerEvent = new JLabel("Configure Trigger Event");
			lblTriggerEvent.setFont(new Font("Dialog", Font.BOLD, 12));
			contentPanel.add(lblTriggerEvent, "flowx,cell 0 2 4 1,growx");
		}
		{
			JSeparator jsVertical = new JSeparator();
			jsVertical.setOrientation(SwingConstants.VERTICAL);
			jsVertical.setBackground(Color.BLACK);
			Dimension dim=getPreferredSize();
			jsVertical.setPreferredSize(new Dimension(5,dim.height+60));
			contentPanel.add(jsVertical, "cell 5 2 2 6,alignx center");
		}
		{
			JLabel lblCondition = new JLabel("Configure Condition");
			lblCondition.setFont(new Font("Dialog", Font.BOLD, 12));
			contentPanel.add(lblCondition, "cell 7 2 6 1");
		}
		{
			rbAny = new JRadioButton("Any Event");
			rbAny.setFont(new Font("Dialog", Font.BOLD, 11));
			rbAny.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent e) {
					/*if(ECARulesCreatorView.this.rbAny.isSelected()){						
						rbSubformula.setSelected(true);
					}*/
				}
				
			});
			contentPanel.add(rbAny, "cell 0 3 4 1");
		}
		{
			
		}
		{
			rbSubformula = new JRadioButton("Subformula");
			rbSubformula.setFont(new Font("Dialog", Font.BOLD, 11));
			rbSubformula.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent ce) {
					jtfSubformula.setEnabled(rbSubformula.isSelected());
					if(rbSubformula.isSelected()){
						//int iEvents=alSubformula.get(iCurrentSubformula-1).getEvents().size();
						//if(iEvents!=1)
					 jtfSubformula.setText(alSubformula.get(iCurrentSubformula-1).getNodeString());
					}					
				}
				
			});
			contentPanel.add(rbSubformula, "cell 7 3 6 1");
		}
		{
			rbSelectEvent = new JRadioButton("Select an event");
			rbSelectEvent.setFont(new Font("Dialog", Font.BOLD, 11));
			rbSelectEvent.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent e) {
				jcbEvents.setEnabled(rbSelectEvent.isSelected());
				if(!rbSelectEvent.isSelected())
					jcbEvents.setSelectedIndex(-1);			
				}
		});
			contentPanel.add(rbSelectEvent, "cell 0 4 4 1");
		}
		{
			jtfSubformula = new JTextField();
			jtfSubformula.setEditable(false);
			contentPanel.add(jtfSubformula, "cell 8 4 5 1,growx");
			jtfSubformula.setColumns(10);
		}
		{
			rbConditionText = new JRadioButton("Enter XPath Expression");
			rbConditionText.setFont(new Font("Dialog", Font.BOLD, 11));
			rbConditionText.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent e) {
					jtfConditionText.setEnabled(rbConditionText.isSelected());
				}
				
			});
			{
				btnEditSubformula = new JButton("...");
				contentPanel.add(btnEditSubformula, "cell 13 4");
				btnEditSubformula.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						XmlXpathEditor.launch("subformula", ECARulesCreatorView.this);
					}
					
				});
			}
			{
				jcbEvents = new JComboBox();
				contentPanel.add(jcbEvents, "cell 1 5 4 1,growx,aligny center");
				jcbEvents.addItemListener(new ItemListener(){

					@Override
					public void itemStateChanged(ItemEvent ie) {
						if(controller.getECARule()==null){
						if(jcbEvents.getSelectedIndex()>-1 || jcbEvents.getSelectedItem()!=null){
							//since specific event has been selected
							rbTrue.setSelected(true);
							//update action part if modify is
							//selected
							if(jcbAction.getSelectedIndex()==2){
								int i=jcbEvents.getSelectedIndex();
//								Subformula curSubformula=alSubformula.get(iCurrentSubformula-1);
								for(int k=0; k<alEvents.size();k++)
									System.out.println("events: "+alEvents.get(k).getEventName());
								jpModify.popuateUI(alEvents.get(i));
							}
						}
					}
						
					}
					
				});
			}
			contentPanel.add(rbConditionText, "cell 7 5 6 1");
		}
		{
			rbEventText = new JRadioButton("Define trigger event");
			rbEventText.setFont(new Font("Dialog", Font.BOLD, 11));
			rbEventText.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent ce) {
					jtfEventText.setEnabled(rbEventText.isSelected());
					btnParameters.setEnabled(rbEventText.isSelected());
					if(rbEventText.isSelected()){
						//bgCondition.clearSelection();
						rbConditionText.setSelected(true);
					}
				}
				
			});
			contentPanel.add(rbEventText, "cell 0 6 4 1");
		}
		{
			jtfConditionText = new JTextField();
			contentPanel.add(jtfConditionText, "cell 8 6 5 1,growx");
			jtfConditionText.setColumns(10);
		}
		{
			btnEditXpath = new JButton("...");
			contentPanel.add(btnEditXpath, "cell 13 6");
			btnEditXpath.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					XmlXpathEditor.launch("subformula-xpath",ECARulesCreatorView.this);
				}
				
			});
		}
		{
			jtfEventText = new JTextField();
			jtfEventText.setEditable(false);
			contentPanel.add(jtfEventText, "cell 1 7 3 1,growx,aligny top");
			jtfEventText.setColumns(10);
		}
		{
			btnParameters = new JButton("Parameters");
			btnParameters.setEnabled(false);
			contentPanel.add(btnParameters, "cell 4 7,growx,aligny top");
			btnParameters.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent ae) {
					JDialogEAParameter.launch("Event", -2, controller, ECARulesCreatorView.this.getParent(),ECARulesCreatorView.this);
				}
				
			});
		}
		{
			rbTrue = new JRadioButton("True");
			rbTrue.setFont(new Font("Dialog", Font.BOLD, 11));
			contentPanel.add(rbTrue, "cell 7 7 6 1,aligny top");
		}
		{
			JSeparator jsHorizontal = new JSeparator();
			jsHorizontal.setBackground(Color.BLACK);
			contentPanel.add(jsHorizontal, "cell 0 8 15 1,growx");			
		}
		{
			JLabel lblAction = new JLabel("Configure Action");
			lblAction.setFont(new Font("Dialog", Font.BOLD, 12));
			contentPanel.add(lblAction, "cell 0 9 4 1");
		}
		{
			lblEnforcementType = new JLabel("Enforcement Type");
			lblEnforcementType.setFont(new Font("Dialog", Font.BOLD, 11));
			contentPanel.add(lblEnforcementType, "cell 0 10 4 1");
		}
		{
			jcbAction = new JComboBox();
			jcbAction.setFont(new Font("Dialog", Font.BOLD, 11));
			jcbAction.setModel(new DefaultComboBoxModel(new String[] {"Select an action", "Inhibit", "Modify", "Execute", "Delay"}));
			contentPanel.add(jcbAction, "cell 0 11 4 1,growx,aligny top");
			
		}
		jcbAction.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent ie) {
				if(ItemEvent.SELECTED==ie.getStateChange()){
					ECARule ecaRule=controller.getECARule();
					if(ecaRule!=null){
						prepareActionUI(jcbAction.getSelectedIndex(),true,ecaRule.getAction());						
					}
					else{						
						prepareActionUI(jcbAction.getSelectedIndex(),false,null);							
						}
				}
			}
			
		});
		{
			jpAction = new JPanel();
			jpAction.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			contentPanel.add(jpAction, "cell 4 10 11 2,grow");				
			{
				jtfError = new JTextField();
				jtfError.setForeground(Color.RED);
				jtfError.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
				jtfError.setEditable(false);
				contentPanel.add(jtfError, "cell 0 12 15 1,growx");
				jtfError.setColumns(10);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnPrevious = new JButton("Previous");
				buttonPane.add(btnPrevious);
				btnPrevious.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent ae) {
						
						Subformula curSubformula=alSubformula.get(iCurrentSubformula-1);
						//store the radio button selections for
						//trigger event and condition
						controller.setCurrentSubformula(curSubformula);						
						controller.setButtonForTriggerEvent(getSelectedEventButton());
						controller.setButtonForCondition(getSelectedConditionButton());
						controller.setIndexSelectedAction(jcbAction.getSelectedIndex());

						try {
							controller.updateECARule(getConfiguredTriggerEvent(false),getConfiguredCondition(false),getConfiguredAction(false));
							print();
							clearUI();
							configurePreviousECARule();
						} catch (ECAInputException e) {
							jtfError.setText(e.getMessage());
						}
						
						
					}
					
				});
			}
			{		
				btnNext = new JButton();
				if(initSFSize==1)
					btnNext.setText("Finish");
				else
				    btnNext.setText("Next");
				btnNext.setActionCommand("OK");
				buttonPane.add(btnNext);
				getRootPane().setDefaultButton(btnNext);
				btnNext.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent ae) {
						//store the radio button selections for
						//trigger event and condition
						if(alSubformula.size()==0)
							return;
						controller.setCurrentSubformula(alSubformula.get(iCurrentSubformula-1));						
						controller.setButtonForTriggerEvent(getSelectedEventButton());
						controller.setButtonForCondition(getSelectedConditionButton());	
						controller.setIndexSelectedAction(jcbAction.getSelectedIndex());
						
						try {
							controller.updateECARule(getConfiguredTriggerEvent(true),getConfiguredCondition(true),getConfiguredAction(true));
							print();
							if(btnNext.getText().equalsIgnoreCase("finish"))
							{
								//write final xml result to file
								bUserCancelled=false;
								ECARulesCreatorView.this.setVisible(false);
								ECARulesCreatorView.this.dispose();
							}
							else{
							clearUI();
							configureNextECARule();
							}
						} catch (ECAInputException e) {
							jtfError.setText(e.getMessage());
						}						
						
						
					}
				});
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
				btnCancel.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent ae) {
						
						for(int i=0; i<alSubformula.size(); ++i){
							alSubformula.get(i).setECARule(null);
						}
						bUserCancelled=true;
						ECARulesCreatorView.this.setVisible(false);
						ECARulesCreatorView.this.dispose();
					}
					
				});
			}
		}
		
	
		init();	
	}

}
