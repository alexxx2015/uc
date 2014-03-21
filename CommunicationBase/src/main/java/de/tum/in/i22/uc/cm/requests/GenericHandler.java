package de.tum.in.i22.uc.cm.requests;

public abstract class GenericHandler<Req extends Request> {
	public abstract Object process(Req request);
}
