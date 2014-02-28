package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;
import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpPipDeployer;

public class PipDeployerBasic implements IPipDeployer {

	private String _id;

	private String _name;

	public PipDeployerBasic() {
		// generate unique id
		_id = UUID.randomUUID().toString();
	}

	public PipDeployerBasic(String name) {
		this();
		_name = name;
	}

	public PipDeployerBasic(GpPipDeployer gpPipDeployer) {
		if (gpPipDeployer == null) {
			return;
		}


		if (gpPipDeployer.hasId())
			_id = gpPipDeployer.getId();
		if (gpPipDeployer.hasName())
			_name = gpPipDeployer.getName();
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getId() {
		if (_id == null) {
			_id = UUID.randomUUID().toString();
		}
		return _id;
	}

	/**
	 *
	 * @param pipDeployer
	 * @return Google Protocol Buffer object corresponding to IPipDeployer
	 */
	public static GpPipDeployer createGpPipDeployer(IPipDeployer pipDeployer) {
		GpPipDeployer.Builder gp = GpPipDeployer.newBuilder();
		gp.setId(pipDeployer.getId());
		gp.setName(pipDeployer.getName());
		return gp.build();
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof PipDeployerBasic) {
			PipDeployerBasic o = (PipDeployerBasic)obj;
			isEqual = Objects.equals(_id, o._id) &&
					Objects.equals(_name, o._name);
		}

		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_id, _name);
	}

	@Override
	public String toString() {
		return "PipDeployerBasic [_id=" + _id + ", _name=" + _name + "]";
	}
}
