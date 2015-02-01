package de.tum.in.i22.uc.blocks.renderable;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

class ComboTableCellRenderer extends JComboBox implements ListCellRenderer, TableCellRenderer {
	DefaultListCellRenderer listRenderer = new DefaultListCellRenderer(); 
	
	  @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
          setSelectedItem(value);
          return this;
      }

	  @Override
	  public Component getListCellRendererComponent(JList list, Object value, int index,
		      boolean isSelected, boolean cellHasFocus) {
		    listRenderer = (DefaultListCellRenderer) listRenderer.getListCellRendererComponent(list, value,
		        index, isSelected, cellHasFocus);
		    configureRenderer(listRenderer, value);
		    return listRenderer;
		  }
	  
	  
	  DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();

	  private void configureRenderer(JLabel renderer, Object value) {
	    if (value != null) {
	      renderer.setText(value.toString());

	    } else {
	      renderer.setText((String) value);
	    }
	  }


}

class FChooserTableCellEditor extends DefaultCellEditor {

	  /**
	 * 
	 */
	private static final long serialVersionUID = -370350942594738429L;
//	private JTable m_Table = null;
	  
    public FChooserTableCellEditor() { 
        super(new JTextField());
        super.setClickCountToStart(1);
//        m_Table = table;
    }
    
 
//	  @Override
      public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		  JTextField textField = new JTextField();           //(JTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
		
		  JFileChooser fileChooser;
          fileChooser = new JFileChooser(".");
          fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
          fileChooser.setVisible(true);
                    
		  int returnVal = fileChooser.showDialog(null, "Select");
		  if(returnVal== JFileChooser.APPROVE_OPTION){
			  File file = fileChooser.getSelectedFile();
			  textField.setText(file.getPath());
		  }
          return textField;
      }

 
   	  


}


