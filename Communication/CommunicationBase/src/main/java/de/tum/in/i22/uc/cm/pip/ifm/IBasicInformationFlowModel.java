package de.tum.in.i22.uc.cm.pip.ifm;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;

public interface IBasicInformationFlowModel extends IInformationFlowModel {

	/**
	 * Removes data object.
	 *
	 * @param data
	 */
	void remove(IData data);

	/**
	 * Removes the given container completely by deleting associated names,
	 * aliases, and data.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param cont
	 *            the container to be removed.
	 */
	void remove(IContainer cont);

	/**
	 * Removes all data from the specified container
	 *
	 * 2014/03/14. FK.
	 *
	 * @param container
	 *            the container of which the data is to be removed.
	 */
	void emptyContainer(IContainer container);

	/**
	 * Removes all data from the container identified by the given container
	 * name.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param containerName
	 *            a name of the container that is to be emptied.
	 */
	void emptyContainer(IName containerName);

	/**
	 * Adds an alias relation from one container to another.
	 *
	 * @return
	 */
	void addAlias(IContainer fromContainer,
			IContainer toContainer);

	void addAlias(IName fromContainerName, IName toContainerName);

	/**
	 * Removes the alias from fromContainer to toContainer.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param fromContainer
	 *            the container of which the alias is outgoing
	 * @param toContainer
	 *            the container of which the alias is incoming
	 */
	void removeAlias(IContainer fromContainer,
			IContainer toContainer);

	/**
	 * Returns an immutable view onto the set of all aliases *from* the
	 * specified container.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param container
	 *            the container whose outgoing aliases will be returned.
	 * @return An immutable view onto the set of all aliases *from* the
	 *         specified container.
	 */
	Collection<IContainer> getAliasesFrom(IContainer container);

	/**
	 * Returns the reflexive, transitive closure of the alias function for
	 * container with id containerId.
	 *
	 * @param containerId
	 * @return
	 */
	Set<IContainer> getAliasTransitiveReflexiveClosure(
			IContainer container);

	/**
	 * Removes all aliases that start from the container with given id.
	 *
	 * @param fromContainerId
	 * @return
	 */
	void removeAllAliasesFrom(IContainer fromContainer);

	/**
	 * Removes all aliases that end in the container with the given id.
	 *
	 * @param toContainerId
	 * @return
	 */
	void removeAllAliasesTo(IContainer toContainer);

	/**
	 * Returns an immutable view onto the set of all aliases *to* the specified
	 * container.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param container
	 *            the container whose incoming aliases will be returned.
	 * @return An immutable view onto the set of all aliases *to* the specified
	 *         container.
	 */
	Set<IContainer> getAliasesTo(IContainer container);
	
	/**
	 * Return the non-reflexive inverse transitive alias closure of the specified container. 
	 * The resulting set will NOT contain the specified container.
	 * @param container
	 * @return
	 */
	Set<IContainer> getAliasTransitiveClosureInverse(IContainer container);

	/**
	 * Returns the non-reflexive transitive alias closure of the specified
	 * container. The resulting set will NOT contain the specified container.
	 *
	 * @param container
	 * @return
	 */
	Set<IContainer> getAliasTransitiveClosure(
			IContainer container);

	/**
	 * Adds the given data to the given container. If data or container is null,
	 * nothing will happen.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param data
	 *            the data to add
	 * @param container
	 *            to which container the data is added.
	 */
	void addData(IData data, IContainer container);

	/**
	 * Removes the given data from the given container.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param data
	 *            the data to remove
	 * @param container
	 *            the container from which the data will be removed
	 * @return true, if the data has been removed
	 */
	void removeData(IData data, IContainer container);

	/**
	 * Returns an immutable view onto the set of data within the given
	 * container. In doubt, returns an empty set; never null.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param container
	 *            the container of which we want to get the data
	 * @return an immutable view onto the set of data items stored in the given
	 *         container
	 */
	Set<IData> getData(IContainer container);

	/**
	 * Returns the data contained in the container identified by the given name,
	 * cf. {@link #getData(IContainer)}.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param containerName
	 *            a name of the container of which the containing data will be
	 *            returned.
	 * @return an immutable view onto the set of data within the container
	 */
	Set<IData> getData(IName containerName);

	/**
	 * Copies all data contained in the container identified by srcContainerName
	 * to the container identified by dstContainerName.
	 *
	 * @param srcContainerName
	 * @param dstContainerName
	 * @return true if both containers existed and data (possibly none, if
	 *         fromContainer was empty) was copied.
	 */
	boolean copyData(IName srcContainerName, IName dstContainerName);

	boolean copyData(IContainer srcContainer, IContainer dstContainer);

	void addDataTransitively(Collection<IData> data, IName dstContainerName);

	void addDataTransitively(Collection<IData> data, IContainer dstContainer);

	/**
	 * Returns all containers in which the specified data is in
	 *
	 * 2014/04/10. FK.
	 *
	 * @param data
	 *            the data whose containers are returned.
	 * @return The set of containers containing the specified data.
	 */
	Set<IContainer> getContainers(IData data);

	/**
	 * Returns all containers of the specified type in which the specified data
	 * is in.
	 *
	 * 2014/04/11. FK.
	 *
	 * @param data
	 *            the data whose containers are returned.
	 * @param type
	 *            the type of the container to be returned
	 * @return all containers of type <T> containing the specified data.
	 */
	<T extends IContainer> Set<T> getContainers(IData data,
			Class<T> type);

	void addData(Collection<IData> data, IContainer container);

	/**
	 * Makes the given name point to the given container.
	 *
	 * If the given name was already assigned to another container, this old
	 * name/container mapping is overwritten. If this was the last name for that
	 * container, the corresponding container is deleted.
	 *
	 * Calling this method is equivalent to calling
	 * {@link IBasicInformationFlowModel#addName(name, container, true)}.
	 *
	 * 2014/09/05. FK.
	 *
	 * @param name
	 *            the new name for the given container.
	 * @param container
	 *            the container for which the new name applies.
	 */
	void addName(IName name, IContainer container);

	/**
	 * Makes the given name point to the given container.
	 *
	 * If the given name was already assigned to another container, this old
	 * name/container mapping is overwritten. If this was the last name for that
	 * container and deleteUnreferencedContainer is equal to true the
	 * corresponding container is deleted.
	 *
	 * 10/06/14. EL.
	 *
	 * @param name
	 *            the new name for the given container.
	 * @param container
	 *            the container for which the new name applies.
	 */
	void addName(IName name, IContainer container,
			boolean deleteUnreferencedContainer);

	/**
	 * Adds an additional name, newName, for the container that is already
	 * identified by another name, oldName.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param oldName
	 *            a name identifying an already existing container
	 * @param newName
	 *            the additional new name for the container identified by
	 *            oldName.
	 */
	void addName(IName oldName, IName newName);

	/**
	 * Removes the name. If the name is the last one for the container, the
	 * container is also removed from the model.
	 *
	 * @param name
	 * @return
	 */
	void removeName(IName name);

	/**
	 * Removes the name. If the name is the last and deleteUnreferencedContainer
	 * is true, then the container is also removed.
	 *
	 * @param name
	 * @param deleteUnreferencedContainer
	 * @return
	 */
	void removeName(IName name,
			boolean deleteUnreferencedContainer);

	/**
	 * Returns the container that is referenced by the naming name.
	 *
	 * @param name
	 * @return
	 */
	IContainer getContainer(IName name);

	/**
	 * Returns an unmodifiable view onto all containers.
	 *
	 * 2014/03/30. FK.
	 *
	 * @return an unmodifiable view onto all containers.
	 */
	Set<IContainer> getAllContainers();

	/**
	 * Returns an unmodifiable view onto all names.
	 *
	 * 2014/03/14. FK.
	 *
	 * @return an unmodifiable view onto all names.
	 */
	Collection<IName> getAllNames();

	/**
	 * Returns an unmodifiable view onto all names of the specified type.
	 *
	 * 2014/04/1. FK.
	 *
	 * @param the
	 *            type of the names to be returned.
	 * @return an unmodifiable view onto all names of the specified type.
	 */
	<T extends IName> Collection<T> getAllNames(Class<T> type);

	/**
	 * Returns an unmodifiable view onto all names for the given container.
	 *
	 * 2014/03/14. FK.
	 *
	 * @param container
	 *            the container whose names are returned.
	 * @return an unmodifiable view onto all names for the given container
	 */
	Collection<IName> getAllNames(IContainer container);

	/**
	 * Get all names of the container identified by the given containerName. It
	 * is ensured that all names within the result are of the specified type.
	 *
	 * @param containerName
	 * @param type
	 * @return
	 */
	<T extends IName> List<T> getAllNames(IName containerName,
			Class<T> type);

	/**
	 * Get all names of the specified container. It is ensured that all names
	 * within the result are of the specified type.
	 *
	 * 2014/04/11. FK.
	 *
	 * @param cont
	 *            the {@link IContainer} whose {@link IName}s will be returned
	 * @param type
	 *            the type of the {@link IName}s to be returned
	 * @return all names of type <T> of the specified container
	 */
	<T extends IName> List<T> getAllNames(IContainer cont,
			Class<T> type);


	IData getDataFromId(String id);
}