package de.tum.in.i22.uc.cm.pip.ifm;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;

public interface IStructuredInformationFlowModel extends IInformationFlowModel {

	/**
	 * This method takes as parameter a list of pairs (label - set of data) that
	 * represents the structure to be associated to a new structured data item,
	 * which should be returned. The behavior is to add another entry in our
	 * _structureMap table where a new IData is associated to the structure
	 * given as parameter.
	 *
	 * The new data item associated to the structured is returned.
	 *
	 */
	IData newStructuredData(Map<String, Set<IData>> structure);

	/**
	 * This method takes as parameter a data item and returns the structure
	 * associated to it. If no structure for it exists, then the
	 * <code>null</code> value is returned.
	 */
	Map<String, Set<IData>> getStructureOf(IData data);

	/**
	 * This method receives a (structured) data item in input and returns the
	 * list of all the structured and non-structured data-items it corresponds
	 * to. If the initial item is not structured, this method returns only it.
	 *
	 * Because every structured data-item is freshly created, it is not possible
	 * to have circular dependency that would lead to a loop.
	 *
	 */
	Set<IData> flattenStructure(IData data);
	
	
	/**
	 * This method takes as parameter a structured data, a valid checksum for it
	 * and a flag that states whether the new checksum should overwrite an
	 * existing value, if present. If data is <code>null</code>, not structured,
	 * if new checksum is <code>null</code>, or if an existing checksum already
	 * exists and the boolean flag is false, the method returns false. Otherwise
	 * it returns true.
	 *
	 */
	public boolean newChecksum(IData data, IChecksum checksum, boolean overwrite);

	/**
	 * This method takes as parameter a data item and returns the checksum
	 * associated to it. If no checksum for it exists, then the
	 * <code>null</code> value is returned.
	 */
	public IChecksum getChecksumOf(IData data);


	/**
	 * This method delete the checksum for a given data. If data is
	 * <code>null</code> or has no checksum associated to it, the method returns
	 * false. Otherwise it returns true.
	 *
	 */
	public boolean deleteChecksum(IData d);
	
	
	/**
	 * This method delete the structure for a given data. If data is
	 * <code>null</code> or has no structure associated to it, the method
	 * returns false. Otherwise it returns true. Note that deleting the
	 * structure also delete the respective checksum.
	 *
	 */
	
	public boolean deleteStructure(IData d) ;
	
}