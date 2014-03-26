package de.tum.in.i22.uc.pip.core.ifm;

public interface IInformationFlowModelExtension {
	void reset();
	void push();
	void pop();
	String niceString();
}
