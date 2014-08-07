package de.tum.in.i22.uc.blocks.renderable;

import javax.swing.*;
import javax.swing.table.*;



import java.util.Vector;

 public class JTableX extends JTable
 {
     protected RowEditorModel rm;
     protected CellEditorModel cem;

     public JTableX()
     {
         super();
         rm = null;
     }

     public JTableX(TableModel tm)
     {
         super(tm);
         rm = null;
     }

     public JTableX(TableModel tm, TableColumnModel cm)
     {
         super(tm,cm);
         rm = null;
     }

     public JTableX(TableModel tm, TableColumnModel cm,
      ListSelectionModel sm)
     {
         super(tm,cm,sm);
         rm = null;
     }

     public JTableX(int rows, int cols)
     {
         super(rows,cols);
         rm = null;
     }

     public JTableX(final Vector rowData, final Vector columnNames)
     {
         super(rowData, columnNames);
         rm = null;
     }

     public JTableX(final Object[][] rowData, final Object[] colNames)
     {
         super(rowData, colNames);
         rm = null;
     }

     // new constructor
     public JTableX(TableModel tm, RowEditorModel rm)
     {
         super(tm,null,null);
         this.rm = rm;
     }

     public void setRowEditorModel(RowEditorModel rm)
     {
         this.rm = rm;
     }

     public RowEditorModel getRowEditorModel()
     {
         return rm;
     }
     
     public void setCellEditorModel(CellEditorModel m){
    	 this.cem=m;
     }
     
     public CellEditorModel getCellEditorModel(){
    	 return cem;
     }   
     
     public TableCellEditor getCellEditor(int row, int col)
     {
         TableCellEditor tmpEditor = null;
         /*if (rm!=null && this.getColumnClass(col)!=Boolean.class)
             tmpEditor = rm.getEditor(row);*/
         if (cem!=null && this.getColumnClass(col)!=Boolean.class)
             tmpEditor = cem.getCellEditor(row,col);
         if (tmpEditor!=null) return tmpEditor;
         return super.getCellEditor(row,col);
     }

    //Prevent the state of instance row checkbox from being changed 
	@Override
	public boolean isCellEditable(int row, int column) {
		
		if(cem!=null){
			if(cem.getIsForConcreteInstance()){
				String sName=this.getValueAt(row, 1).toString();
				if(column==0 && sName.equalsIgnoreCase("object")){
					System.out.println("Uneditable");
					return false;
				}
			}
		}
		return super.isCellEditable(row, column);
	}
 }
