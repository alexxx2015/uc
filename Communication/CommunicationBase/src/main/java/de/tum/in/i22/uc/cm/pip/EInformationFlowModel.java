package de.tum.in.i22.uc.cm.pip;

import java.util.HashSet;
import java.util.Set;

public enum EInformationFlowModel {
	SCOPE,
	QUANTITIES;

	public static Set<EInformationFlowModel> from(String str) {
		Set<EInformationFlowModel> result = new HashSet<>();

		for (String s : str.split(",")) {
			switch (s.trim().toLowerCase()) {
				case "scope":
					result.add(SCOPE);
				case "quantities":
					result.add(QUANTITIES);
			}
		}

		return result;
	}
}
