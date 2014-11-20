package de.tum.in.i22.uc.cm.datatypes.interfaces;

import de.tum.in.i22.uc.generic.Simulatable;

public interface ICondition extends Simulatable {
	boolean tick(boolean endOfTimestep);
}
