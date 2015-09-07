package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PXP can invoke on a PIP.
 * @author Stepa
 *
 */
@AThriftService(name="TPxp2Pip")
public interface IPxp2Pip {
	@AThriftMethod(signature="map<string, set<map<string, string>>> filterModel(1: map<string, string> params)")
	public Map<String, Set<Map<String, String>>> filterModel(Map<String, String> params);
}

