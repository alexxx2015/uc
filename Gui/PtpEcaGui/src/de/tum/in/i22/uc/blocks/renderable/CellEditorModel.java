package de.tum.in.i22.uc.blocks.renderable;

import javax.swing.table.TableCellEditor;
import javax.swing.DefaultCellEditor;
import java.util.ArrayList;

/**
 * 17 March 2013:
 * Working with JTables, it is difficult to set cell editor per cell. It is only
 * possible per column. An attempt is made for per-row implementation: RowEditorModel.java
 * but it is not sufficient for me.
 * @author ELIJAH
 *
 */
public class CellEditorModel {
	
	class CellInformation{		
		private int iRow;
		private int iColumn;
		private TableCellEditor tableCellEditor;
		public CellInformation(int r, int c, TableCellEditor tce){
			iRow=r;
			iColumn=c;
			tableCellEditor=tce;
		}
		
		public TableCellEditor getCellEditor(){
			return tableCellEditor;
		}
		
		public int getRow(){
			return iRow;
		}
		
		public int getColumn(){
			return iColumn;
		}
	
	}
	
	private ArrayList<CellInformation> alCellInfo;
	/*
	 * 11 April 2013. This feature helps to prevent parameter row
	 * uneditable
	 */
	private boolean bForConcreteInstance;	
	
	public CellEditorModel(){
		alCellInfo=new ArrayList<CellInformation>();
		bForConcreteInstance=false;		
	}
	
	public void addCellEditor(int r, int c, TableCellEditor tce){
		alCellInfo.add(new CellInformation(r,c,tce));
	}
	
	public TableCellEditor getCellEditor(int row, int col){
		TableCellEditor tce=null;
		for(int i=0; i<alCellInfo.size(); ++i){
			CellInformation ci=alCellInfo.get(i);
			if(row==ci.getRow() && col==ci.getColumn())
				{
					if(bForConcreteInstance && row==0){												
							DefaultCellEditor dce=(DefaultCellEditor)ci.getCellEditor();
							dce.getComponent().setEnabled(false);
							tce=dce;						
					}
					else tce=ci.getCellEditor();
					break;
				}
		}
		return tce;
	}
	
	public void setIsForConcreteInstance(boolean ci){
		bForConcreteInstance=ci;
	}
	
	public boolean getIsForConcreteInstance(){
		return bForConcreteInstance;
	}

}
