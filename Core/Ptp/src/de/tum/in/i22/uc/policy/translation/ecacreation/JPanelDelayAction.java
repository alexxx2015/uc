package de.tum.in.i22.uc.policy.translation.ecacreation;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import javax.swing.JTextField;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import net.miginfocom.swing.MigLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

public class JPanelDelayAction extends JPanel implements ECAAction{
	private JTextField jtfTimeValue;
	private JComboBox jcbTimeUnit;

	/**
	 * Create the panel.
	 */
	public JPanelDelayAction() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{18, 0, 27, 170, 18, 0};
		gridBagLayout.rowHeights = new int[]{22, 0, 29, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblTimeUnit = new JLabel("Time Unit");
		lblTimeUnit.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblTimeUnit = new GridBagConstraints();
		gbc_lblTimeUnit.gridwidth = 2;
		gbc_lblTimeUnit.anchor = GridBagConstraints.WEST;
		gbc_lblTimeUnit.insets = new Insets(0, 0, 5, 5);
		gbc_lblTimeUnit.gridx = 0;
		gbc_lblTimeUnit.gridy = 1;
		add(lblTimeUnit, gbc_lblTimeUnit);
		
		jcbTimeUnit = new JComboBox();
		jcbTimeUnit.setModel(new DefaultComboBoxModel(new String[] {"Timesteps", "Nanoseconds", "Microseconds", "Milliseconds", "Seconds", "Minutes", "Hours", "Days", "Weeks", "Months", "Years"}));
		GridBagConstraints gbc_jcbTimeUnit = new GridBagConstraints();
		gbc_jcbTimeUnit.fill = GridBagConstraints.HORIZONTAL;
		gbc_jcbTimeUnit.anchor = GridBagConstraints.NORTH;
		gbc_jcbTimeUnit.insets = new Insets(0, 0, 5, 5);
		gbc_jcbTimeUnit.gridx = 3;
		gbc_jcbTimeUnit.gridy = 1;
		add(jcbTimeUnit, gbc_jcbTimeUnit);
		
		JLabel lblTimeValue = new JLabel("Time Value");
		lblTimeValue.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblTimeValue = new GridBagConstraints();
		gbc_lblTimeValue.anchor = GridBagConstraints.WEST;
		gbc_lblTimeValue.gridwidth = 2;
		gbc_lblTimeValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblTimeValue.gridx = 0;
		gbc_lblTimeValue.gridy = 2;
		add(lblTimeValue, gbc_lblTimeValue);
		
		jtfTimeValue = new JTextField();
		GridBagConstraints gbc_jtfTimeValue = new GridBagConstraints();
		gbc_jtfTimeValue.insets = new Insets(0, 0, 0, 5);
		gbc_jtfTimeValue.fill = GridBagConstraints.HORIZONTAL;
		gbc_jtfTimeValue.gridx = 3;
		gbc_jtfTimeValue.gridy = 2;
		add(jtfTimeValue, gbc_jtfTimeValue);
		jtfTimeValue.setColumns(10);

	}

	@Override
	public String getActionName() {
		
		return "delay";
	}

	@Override
	public Object getActionResult() throws ECAInputException{
				
		ArrayList<Object> alResult=new ArrayList<Object>();				
		alResult.add(jcbTimeUnit.getSelectedItem().toString());
		alResult.add(jtfTimeValue.getText());		
		return alResult;
	}

	@Override
	public void populateUI(Object action) {
		
		if(action!=null){			
			if(!(action instanceof String)){
			@SuppressWarnings("unchecked")
			ArrayList<Object> actionDetails=(ArrayList<Object>)action;
			jcbTimeUnit.setSelectedItem(actionDetails.get(0));
			jtfTimeValue.setText(actionDetails.get(1).toString());
		  }
		}
		else jtfTimeValue.setText("");
		
	}

}
