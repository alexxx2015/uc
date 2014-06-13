package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.excel.OfficeClipboardName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public abstract class ExcelEvents extends AbstractScopeEventHandler {

	protected String cs = Settings.getInstance().getExcelCoordinatesSeparator();
	protected String ls = Settings.getInstance().getExcelListSeparator();

	protected static final String ocbName = Settings.getInstance().getExcelOcbName();
	protected static final String scbName = Settings.getInstance().getExcelScbName();

	protected Set<CellName> getSetOfCells(String target) {
		if (target == null)
			throw new RuntimeException(
					"impossible to getSetOfCells from null set");
		String[] cells = target.split(ls);
		Set<CellName> res = new HashSet<CellName>();
		for (String s : cells) {
			res.add(new CellName(s));
		}
		return res;
	}

	/**
	 * This method pushes the content of the Excel_SystemClipboard container to
	 * the officeclipboard. This means that the name of every container is
	 * shifted of one position, and the head will take the new content.
	 */
	protected void pushOCB() {
		Collection<OfficeClipboardName> ocbs = _informationFlowModel
				.getAllNames(OfficeClipboardName.class);

		// copy the content of the system clipboard into a new container
		IContainer cont = new ContainerBasic();
		IContainer scb = _informationFlowModel.getContainer(new NameBasic(
				scbName));
		_informationFlowModel.copyData(scb, cont);

		if (ocbs == null) {
			// if ocb is empty, create new posizion 0
			_informationFlowModel.addName(
					OfficeClipboardName.create(ocbName, 0), cont, false);
			return;
		}

		// otherwise shift all the positions (but order them first)
		OfficeClipboardName[] arr = new OfficeClipboardName[ocbs.size()];
		for (OfficeClipboardName ocb : ocbs) {
			arr[ocb.getPos()] = ocb;
		}

		for (int i = 0; i < arr.length; i++) {
			IContainer tmpCont = _informationFlowModel.getContainer(arr[i]);
			_informationFlowModel.addName(arr[i], cont, false);
			cont = tmpCont;
		}
		OfficeClipboardName newPos = OfficeClipboardName.create(ocbName,
				arr.length);
		_informationFlowModel.addName(newPos, cont, false);

	}

	/**
	 * This method gets a pointer to the head of the office clipboard. It
	 * doesn't remove the element
	 */
	protected IContainer headOCB() {
		return getOCB(0);
	}

	/**
	 * This method deletes the n-th element from the office clipboard This means
	 * that every container after it needs to be shifted of one position. If the
	 * clipboard currently contains less than n elements, this method does
	 * nothing. Notice that index starts from 0.
	 */
	protected void deleteOCB(int pos) {
		Collection<OfficeClipboardName> ocbs = _informationFlowModel
				.getAllNames(OfficeClipboardName.class);

		if (pos >= ocbs.size())
			return;

		// shift all the positions > pos (but order them first)
		OfficeClipboardName[] arr = new OfficeClipboardName[ocbs.size()];
		for (OfficeClipboardName ocb : ocbs) {
			arr[ocb.getPos()] = ocb;
		}

		for (int i = pos; i < arr.length - 1; i++) {
			// Pointing ocb[n] to
			// ocb[n+1] empties container formerly pointed by ocb[n] and makes 2
			// references to ocb[n+1]. next iteration pointing ocb[n+1] to
			// ocb[n+2] deletes nothing because ocb[n+1] had 2 names, so now it
			// still has one.
			_informationFlowModel.addName(arr[i],
					_informationFlowModel.getContainer(arr[i + 1]),true);
		}

		// remove the last element of the list
		_informationFlowModel.removeName(arr[arr.length - 1]);
	}

	/**
	 * This method gets the n-th element from the office clipboard
	 */
	protected IContainer getOCB(int pos) {
		if (pos < 0)
			return null;
		OfficeClipboardName posName = OfficeClipboardName.create(ocbName, pos);
		return _informationFlowModel.getContainer(posName);
	}
	
	protected class SortByCol implements Comparator<CellName>{
	    public int compare(CellName c1, CellName c2) {
	    	return (c1.getCol()==c2.getCol()?0:c1.getCol()>c2.getCol()?-1:1);
	    }
	}
	
	protected class SortByColReverse implements Comparator<CellName>{
	    public int compare(CellName c1, CellName c2) {
	    	return (c1.getCol()==c2.getCol()?0:c1.getCol()>c2.getCol()?1:-1);
	    }
	}

	protected class SortByRow implements Comparator<CellName>{
	    public int compare(CellName c1, CellName c2) {
	    	return (c1.getRow()==c2.getRow()?0:c1.getRow()>c2.getRow()?-1:1);
	    }
	}

	protected class SortByRowReverse implements Comparator<CellName>{
	    public int compare(CellName c1, CellName c2) {
	    	return (c1.getRow()==c2.getRow()?0:c1.getRow()>c2.getRow()?1:-1);
	    }
	}

}
