package de.tum.in.i22.uc.cm.datatypes.interfaces;

public interface ICondition {
	/**
	 * This method evaluates this {@link ICondition} in the presence
	 * of the specified event at the current point in time.
	 * Evaluation takes into account that the specified event is
	 * currently happening. If the specified event is null, then this
	 * {@link ICondition} is evaluated as-is. This is useful for evaluating
	 * the condition at the end of a timestep.
	 *
	 * @param event
	 * @return
	 */
	public boolean evaluate(IEvent event);
}
