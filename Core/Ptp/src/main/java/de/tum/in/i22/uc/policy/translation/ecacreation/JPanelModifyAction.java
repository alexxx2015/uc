package de.tum.in.i22.uc.policy.translation.ecacreation;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.border.LineBorder;
import java.awt.Color;

/**
 * JPanel that houses the controls to modify the parameters
 * of an event, in the action part, during ECA rules creation.
 * 
 * @author ELIJAH
 *
 */
public class JPanelModifyAction extends JPanel implements ECAAction{
	private JTextField jtfObjectValue;
	private JTextField jtfDeviceValue;

	/**
	 * Create the panel.
	 */
	public JPanelModifyAction() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{67, 23, 153, 16, 0};
		gridBagLayout.rowHeights = new int[]{22, 0, 28, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblObjectValue = new JLabel("Object Value");
		lblObjectValue.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblObjectValue = new GridBagConstraints();
		gbc_lblObjectValue.insets = new Insets(0, 0, 5, 5);
		gbc_lblObjectValue.gridx = 0;
		gbc_lblObjectValue.gridy = 1;
		add(lblObjectValue, gbc_lblObjectValue);
		
		jtfObjectValue = new JTextField();
		GridBagConstraints gbc_jtfObjectValue = new GridBagConstraints();
		gbc_jtfObjectValue.fill = GridBagConstraints.HORIZONTAL;
		gbc_jtfObjectValue.insets = new Insets(0, 0, 5, 5);
		gbc_jtfObjectValue.gridx = 2;
		gbc_jtfObjectValue.gridy = 1;
		add(jtfObjectValue, gbc_jtfObjectValue);
		jtfObjectValue.setColumns(10);
		
		JLabel lblDeviceValue = new JLabel("Device Value");
		lblDeviceValue.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblDeviceValue = new GridBagConstraints();
		gbc_lblDeviceValue.anchor = GridBagConstraints.WEST;
		gbc_lblDeviceValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblDeviceValue.gridx = 0;
		gbc_lblDeviceValue.gridy = 2;
		add(lblDeviceValue, gbc_lblDeviceValue);
		
		jtfDeviceValue = new JTextField();
		GridBagConstraints gbc_jtfDeviceValue = new GridBagConstraints();
		gbc_jtfDeviceValue.insets = new Insets(0, 0, 0, 5);
		gbc_jtfDeviceValue.fill = GridBagConstraints.HORIZONTAL;
		gbc_jtfDeviceValue.gridx = 2;
		gbc_jtfDeviceValue.gridy = 2;
		add(jtfDeviceValue, gbc_jtfDeviceValue);
		jtfDeviceValue.setColumns(10);

	}

	@Override
	public String getActionName() {
		// TODO Auto-generated method stub
		return "modify";
	}

	@Override
	public Object getActionResult() throws ECAInputException{
		// TODO Auto-generated method stub
		ArrayList<Object> alResult=new ArrayList<Object>();		
		alResult.add(jtfObjectValue.getText());
		alResult.add(jtfDeviceValue.getText());
		return alResult;
	}

	@Override
	public void populateUI(Object action) {
		// TODO Auto-generated method stub
		if(action!=null){
			if(!(action instanceof String)){
			@SuppressWarnings("unchecked")
			ArrayList<Object> actionDetails=(ArrayList<Object>)action;
			jtfObjectValue.setText(actionDetails.get(0).toString());
			jtfDeviceValue.setText(actionDetails.get(1).toString());
			}
		}
		else{
			jtfObjectValue.setText("");
			jtfDeviceValue.setText("");
		}
	}
	
	public void popuateUI(SubformulaEvent event){
		String sObjectValue="",sDeviceValue="";
		ArrayList<SubformulaEventParameter> alParam=event.getParameters();		
		if(alParam.size()>0){			
			for(int i=0; i<alParam.size(); ++i){
				SubformulaEventParameter param=alParam.get(i);
				if(param.getName().equalsIgnoreCase("object"))
					sObjectValue=param.getValue();
				else if(param.getName().equalsIgnoreCase("device"))
					sDeviceValue=param.getValue();
				if(!sObjectValue.equals("") && !sDeviceValue.equals("")) break;
			}
			jtfObjectValue.setText(sObjectValue);
			jtfDeviceValue.setText(sDeviceValue);
		}
		
	}

}
