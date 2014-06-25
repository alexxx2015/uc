package de.tum.in.i22.uc.cm.datatypes.excel;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;

/**
 * Class representing name for OfficeClipboard's containers
 * 
 * @author Enrico Lovat
 * 
 */
public class OfficeClipboardName extends NameBasic {
	private int pos = -1;

	private OfficeClipboardName(String name, int pos) {
		super(name);
		this.pos=pos;
	}

	public static OfficeClipboardName create(String clipboardName, int pos) {
		String name= clipboardName + "[" + pos + "]";
		return new OfficeClipboardName(name, pos);
	}
	public int getPos() {
		return pos;
	}

}
