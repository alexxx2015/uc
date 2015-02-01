package de.tum.in.i22.uc.model;

	import java.awt.BorderLayout ;
import java.awt.Color;
import java.awt.Component;
	import java.awt.Container ;
	import java.awt.Dimension ;

	import java.awt.event.ActionEvent ;
	import java.awt.event.ActionListener ;


import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
	import javax.swing.JButton ;
import javax.swing.JComboBox;
import javax.swing.JDialog;
	import javax.swing.JFrame ;
	import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu ;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

    /**
     * @version 1.0
     * @author ladmin
     * */
	public class ParamTableModel extends DefaultTableModel {

		
	     @Override
	     public Class<?> getColumnClass(int columnIndex)
	     {
	       if (columnIndex == 0)
	       {
	         return Boolean.class;
	       }
	       else
	       {
	         // returns Object.class
	         return super.getColumnClass(columnIndex);
	       }
	     }

	  public boolean isCellEditable(int row, int column) {
		  if(column==1)
		    return false;
		  else return true;
//		  return (column != 0);
	  }
	  
	  /**
	   * Helpful to avoid cyclic behaviour during undo and redo.
	   *
	   */
	  private boolean bFireEvent;
	  
	  @Override
	  public void setValueAt(Object object, int row, int col){
		bFireEvent=true; 
		super.setValueAt(object, row, col);
	  }
	  
	  /**
	   * Overloaded method of previous one to help prevent
	   * cyclic behaviour in undo and redo actions
	   * 
	   * @param object
	   * @param row
	   * @param col
	   * @param fireEvent
	   */
	  public void setValueAt(Object object, int row, int col, boolean fireEvent){
		  bFireEvent=fireEvent;
		  super.setValueAt(object, row, col);
	  }
	  
	  public boolean getFireParamChangeEvent(){
		  return bFireEvent;
	  }
	
	}
