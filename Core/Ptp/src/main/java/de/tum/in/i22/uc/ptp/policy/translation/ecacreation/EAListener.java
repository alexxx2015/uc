package de.tum.in.i22.uc.ptp.policy.translation.ecacreation;

import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JTable;

public class EAListener implements KeyListener, FocusListener {
	
	private JTable jtParameter;
	
	public EAListener(JTable table){
		jtParameter=table;
	}

	@Override
	public void focusGained(FocusEvent fe) {
		
		
	}

	@Override
	public void focusLost(FocusEvent fe) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		
		JComponent comp=(JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if(comp instanceof JTable)
		{
			JTable table=(JTable) comp;
			
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		

	}

}
