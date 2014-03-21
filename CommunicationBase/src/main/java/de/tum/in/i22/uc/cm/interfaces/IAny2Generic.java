package de.tum.in.i22.uc.cm.interfaces;

//We like to show off

public interface IAny2Generic<I1 extends IAny2Generic<?, ?>, I2 extends IAny2Generic<?, ?>>{
	void init(I1 interface1, I2 interface2);
}
