package de.tum.in.i22.uc.cm.datatypes.basic;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;

/***
 * A checksum for structured data
 * 
 * @author lovat
 *
 */

public class ChecksumBasic implements IChecksum {
	private long val=-1;

	public long getVal() {
		return val;
	}

	public void setVal(long val) {
		this.val = val;
	}

	public ChecksumBasic(long val) {
		this.val=val;
	}
	
	public ChecksumBasic() {
		this.val=-1;
	}
			
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (val ^ (val >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChecksumBasic other = (ChecksumBasic) obj;
		if (val != other.val)
			return false;
		return true;
	}
	
	
	
}
