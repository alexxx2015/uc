package de.tum.in.i22.uc.cm.datatypes.Linux;

/**
 * This interface indicates that a process-relative container name
 * can be cloned for another process. This is needed for clone() syscalls.
 *
 * @author Florian Kelbert
 *
 */
public interface IClonableForProcess extends IProcessRelativeName {
	/**
	 * Clones the current object instance for the specified process id.
	 * The returned object is a clone of the current object, the only difference
	 * being the process id: The returned object takes as a process id the process id
	 * specified as a parameter.
	 *
	 * @param pid
	 */
	public IClonableForProcess cloneFor(String pid);
}
