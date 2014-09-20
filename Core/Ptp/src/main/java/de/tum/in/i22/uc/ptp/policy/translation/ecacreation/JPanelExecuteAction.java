package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import java.awt.Container;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.Font;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;

public class JPanelExecuteAction extends JPanel implements ECAAction{
	/**
	 *
	 */
	private static final long serialVersionUID = -5848685299730507786L;
	private JButton btnAdd;
	private JButton btnDelete;
	private Container container;
	private JList lsActions;
	private ECARulesCreatorController controller;
	private JButton btnEdit;
	private Object dlgParent;

	/**
	 * Create the panel.
	 */
	public JPanelExecuteAction(Object parent) {
		setLayout(new MigLayout("", "[150px][63px]", "[14px][23px][][][][7px][23px][5px][23px]"));
		dlgParent=parent;
		JLabel lblManageActions = new JLabel("Manage Actions");
		lblManageActions.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblManageActions, "cell 0 0,alignx center,aligny center");

		lsActions = new JList();
		lsActions.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(lsActions, "cell 0 1 1 6,grow");
		lsActions.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent fe) {


			}

			@Override
			public void focusLost(FocusEvent fe) {

				//lsActions.getSelectionModel().clearSelection();
			}

		});

		btnAdd = new JButton("Add");
		add(btnAdd, "cell 1 1,growx,aligny center");
		btnAdd.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ae) {


				JDialogEAParameter.launch("Action", -1, controller, container, JPanelExecuteAction.this);
				lsActions.getSelectionModel().clearSelection();
			}

		});

		btnEdit = new JButton("Edit");
		add(btnEdit, "cell 1 3,growx");
		btnEdit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ae) {

				int iSel=-1;
				iSel=lsActions.getSelectedIndex();
				if(iSel>-1 && controller.getExecuteActions().size()>0){
					JDialogEAParameter.launch("Action", iSel, controller, container, JPanelExecuteAction.this);
				}

				lsActions.getSelectionModel().clearSelection();
			}

		});

		btnDelete = new JButton("Delete");
		add(btnDelete, "cell 1 6,alignx center,aligny center");
		btnDelete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ae) {

				int iSel=lsActions.getSelectedIndex();
				if(iSel!=-1){
					if(controller.getExecuteActions().size()>0){
						controller.getExecuteActions().remove(iSel);
					}
				}
				showExecuteActions();
			}

		});

	}

	public void setParentContainer(Container parent){
		this.container=parent;
	}

	public void setController(ECARulesCreatorController controller){
		this.controller=controller;
	}

	@SuppressWarnings("unchecked")
	public void showExecuteActions(){

		ArrayList<SubformulaEvent> alEvents=controller.getExecuteActions();
		DefaultListModel dlModel=new DefaultListModel();
		if(alEvents.size()>0){
			for(int i=0; i<alEvents.size(); ++i){
				dlModel.add(i, alEvents.get(i).getEventName());
			}
		}
		lsActions.setModel(dlModel);
	}

	public Object getView(){
		return dlgParent;
	}

	@Override
	public String getActionName() {

		return "execute";
	}

	@Override
	public Object getActionResult() throws ECAInputException {

		return controller.getExecuteActions();
	}

	@Override
	public void populateUI(Object action) {

		if(action!=null){

		}
	}

}
