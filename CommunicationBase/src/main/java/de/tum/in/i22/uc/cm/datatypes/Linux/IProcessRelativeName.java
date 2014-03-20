package de.tum.in.i22.uc.cm.datatypes.Linux;

import de.tum.in.i22.uc.cm.datatypes.IName;

/**
 * This interface indicates that a container name is relative to a process.
 * This means that a process id is part of the respective container name.
 * For example, this is the case for mmap mappings and file descriptors.
 * Hence, this interface provides a method to retrieve the respective process id.
 *
 * @author Florian Kelbert
 *
 */
public interface IProcessRelativeName extends IName {
	public int getPid();
}
