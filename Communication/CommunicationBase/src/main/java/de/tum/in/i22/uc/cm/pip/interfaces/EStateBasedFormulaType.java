package de.tum.in.i22.uc.cm.pip.interfaces;

public enum EStateBasedFormulaType {
	IS_NOT_IN,
	IS_ONLY_IN,
	IS_COMBINED_WITH;

	public static EStateBasedFormulaType from(String s) {
		switch(s.trim().toLowerCase()) {
			case "isnotin":
				return IS_NOT_IN;
			case "isonlyin":
				return IS_ONLY_IN;
			case "iscombinedwith":
				return IS_COMBINED_WITH;
		}
		return null;
	}
}
