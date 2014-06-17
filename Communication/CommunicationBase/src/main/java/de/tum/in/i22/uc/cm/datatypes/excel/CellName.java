package de.tum.in.i22.uc.cm.datatypes.excel;

import org.junit.Assert;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.settings.Settings;

/**
 * Class representing a cell name
 * 
 * @author Enrico Lovat
 * 
 */
public class CellName extends NameBasic {
	private static final String cs = Settings.getInstance()
			.getExcelCoordinatesSeparator();
	private String workbook = "";
	private String worksheet = "";
	private int row = -1;
	private int col = -1;

	public CellName(String cellName) {
		super(cellName);
		Assert.assertNotNull(cellName);
		Assert.assertNotEquals(cellName, "");

		String[] coordinates = cellName.split(cs);
		Assert.assertEquals(4, coordinates.length);

		workbook = coordinates[0];
		worksheet = coordinates[1];
		row = Integer.valueOf(coordinates[2]);
		col = Integer.valueOf(coordinates[3]);
	}

	public CellName(String workbook, String worksheet, int row, int col) {
		super(workbook + cs + worksheet + cs + row + cs + col);

		this.workbook = workbook;
		this.worksheet = worksheet;
		this.row = row;
		this.col = col;
	}

	public String getWorkbook() {
		return workbook;
	}

	public String getWorksheet() {
		return worksheet;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}