package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;

public abstract class JavaEventHandler extends BaseEventHandler{
	
	protected static Map<String,String> iFlow = new HashMap<String,String>();


	protected final String _paramId = "id";
	protected final String _paramSignature = "signature";
	protected final String _paramLocation = "location";
	protected final String _paramParamPos = "parampos";
	protected final String _paramType = "type";
	protected final String _paramOffset = "offset";

	protected final String _javaIFDelim = ":";
	protected final String _srcPrefix = "src_";
	protected final String _snkPrefix = "snk_";

}
