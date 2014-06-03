package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public abstract class ExcelEvents extends AbstractScopeEventHandler {

	protected String cs = Settings.getInstance().getExcelCoordinatesSeparator();
	
	/*
	 * Here goes code common to every excel event
	 */

	/**
	 * This method returns a boolean that states whether the provided
	 * containerName matches the row number provided as second parameter.
	 * 
	 * @param contName
	 * @param row
	 * @return
	 */
	protected boolean matchRow(IName contName, int row) {
		if (contName==null) throw new RuntimeException("impossible to matchRow of a null IName containername");
		String name= contName.getName();
		if (name==null) throw new RuntimeException("impossible to split of a null string. The container name shouldn't be null");
		String[] coordinates = name.split(cs);
		_logger.debug("matchRow: "+contName+" - row "+row+" -> coordinates are " + coordinates);
		Assert.assertEquals(4, coordinates.length);			
		return (Integer.getInteger(coordinates[2])==row);	
	}

	/**
	 * This method returns a boolean that states whether the provided
	 * containerName matches the column number provided as second parameter.
	 * 
	 * @param contName
	 * @param column
	 * @return
	 */
	protected boolean matchColumn(IName contName, int column) {
		if (contName==null) throw new RuntimeException("impossible to matchColumn of a null IName containername");
		String name= contName.getName();
		if (name==null) throw new RuntimeException("impossible to split of a null string. The container name shouldn't be null");
		String[] coordinates = name.split(cs);
		_logger.debug("matchColumn: "+contName+" - column "+column+" -> coordinates are " + coordinates);
		Assert.assertEquals(4, coordinates.length);			
		return (Integer.getInteger(coordinates[2])==column);	
	}

	
	/**
	 * This method returns a list of Names of containers on a certain sheet
	 * 
	 * @param contName
	 * @param column
	 * @return
	 */
	protected Set<IName> cellsOnThisSheet(String workBookName, String workSheetName) {
		if ((workBookName==null)||(workSheetName==null)) throw new RuntimeException("workbookname and worksheetname shouldn't be null");
		
		Collection<IName> allNames = _informationFlowModel.getAllNames();
		
		Set<IName> res = new HashSet<IName>();
		
		String[] coordinates;
		for (IName name : allNames){
			coordinates = name.getName().split(cs);
			Assert.assertEquals(4, coordinates.length);
			if (workBookName.equalsIgnoreCase(coordinates[0]) && workSheetName.equalsIgnoreCase(coordinates[1])){
				res.add(name);
			}
		}
		
		_logger.debug("cellsOnThisSheet("+workBookName+","+workSheetName+") size="+res.size());
		return res;	
	}

	
	
}
