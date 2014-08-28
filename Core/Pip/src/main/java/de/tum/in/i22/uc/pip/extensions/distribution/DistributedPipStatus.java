package de.tum.in.i22.uc.pip.extensions.distribution;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;

/**
 * A {@link DistributedPipStatus} wraps another {@link IStatus} object and adds
 * functionality related to distribution.
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPipStatus implements IStatus {
	private final RemoteDataFlowInfo _dataflow;

	private final IStatus _status;

	/**
	 * Creates a new {@link DistributedPipStatus} instance by wrapping the
	 * provided {@link IStatus} object and by attaching the specified
	 * {@link RemoteDataFlowInfo} object to it. The rationale is that the
	 * provided {@link RemoteDataFlowInfo} occurred during the assembly
	 * of the provided {@link IStatus} object.
	 *
	 * @param status
	 * @param dataflow
	 */
	private DistributedPipStatus(IStatus status, RemoteDataFlowInfo dataflow) {
		_status = status;
		_dataflow = dataflow;
	}

	public DistributedPipStatus(RemoteDataFlowInfo dataflow) {
		this(new StatusBasic(EStatus.REMOTE_DATA_FLOW_HAPPENED), dataflow);
	}

	/**
	 * Returns the attached {@link RemoteDataFlowInfo} object.
	 * @return the attached {@link RemoteDataFlowInfo} object.
	 */
	public RemoteDataFlowInfo getDataflow() {
		return _dataflow;
	}

	/**
	 * Unwrap the internally wrapped {@link IStatus} object and return it.
	 * Functionality provided by {@link DistributedPipStatus} is (generally)
	 * no longer available on the returned {@link IStatus} object.
	 *
	 * @return the internally wrapped {@link IStatus} object.
	 */
	public IStatus unwrap() {
		return _status;
	}

	@Override
	public EStatus getEStatus() {
		return _status.getEStatus();
	}

	@Override
	public String getErrorMessage() {
		return _status.getErrorMessage();
	}

	@Override
	public boolean isStatus(EStatus status) {
		return _status.isStatus(status);
	}
}
