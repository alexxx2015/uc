package de.tum.in.i22.uc.pip.eventdef.java;

/**
 * This class initializes all sinks and sources according to the joana output
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.SourceSinkName;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class JoanaInitInfoFlowEventHandler extends JavaEventHandler {

	public JoanaInitInfoFlowEventHandler() {
		super();
	}

	static IData data = null;

	@Override
	protected IStatus update() {

		// This event is used only during tests to initialize the information
		// flow schema to a specific state

//		String listOfSources="";
//		String listOfSinks="";
//		String listOfFlows="";
		JSONObject report;

		String pid;

		try {
			// type = getParameterValue(_paramType);
			pid = getParameterValue("PID");
			String r = getParameterValue("REPORT");
			report = (JSONObject) JSONValue.parse(r);
//			listOfSources = getParameterValue("listOfSources");
//			listOfSinks = getParameterValue("listOfSinks");
//			listOfFlows = getParameterValue("listOfFlows");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// If this is an information flow mapping event then fill internal
		// static mapping
		// if (type.equals("iflow")) {
		
//		String sep1 = Settings.getInstance().getJoanaDelimiter1();
//		String sep2 = Settings.getInstance().getJoanaDelimiter2();

		JSONArray listOfSources = (JSONArray)report.get("listOfSources");
		this.parseJSONList(listOfSources, pid, "source");
		
		JSONArray listOfSinks = (JSONArray)report.get("listOfSinks");
		this.parseJSONList(listOfSinks, pid, "sink");
		
		JSONArray listOfFlows = (JSONArray)report.get("listOfFlows");
		Iterator<JSONObject> listOfFlowIt = listOfFlows.iterator();
		while(listOfFlowIt.hasNext()){
			JSONObject o = listOfFlowIt.next();
			String sink = (String) o.get("sink");
			JSONArray sources = (JSONArray)o.get("sources");
			String[] toBeAdded = new String[sources.size()]; //first element is sink
			for (int i=0; i<sources.size(); i++){
				toBeAdded[i]=pid+_javaIFDelim+((String)(sources.get(i))).toLowerCase();
			}
			iFlow.put(pid+_javaIFDelim+sink.toLowerCase(), toBeAdded);
		}
		
//		parseList(listOfSources, pid, "source", sep1, sep2);
//		parseList(listOfSinks, pid, "sink", sep1, sep2);	
//		String[] flowsArr = listOfFlows.split(sep2);
//		for (String flow: flowsArr){
//			if ((flow==null)||(flow.equals(""))) break;
//			String[] map = flow.split(sep1);
//			if (map.length>2){
//				//String[] toBeAdded = Arrays.copyOfRange(map, 1, map.length-1);
//				String[] toBeAdded = new String[map.length-1]; //first element is sink
//				for (int i=1; i<map.length; i++){
//					toBeAdded[i-1]=pid+_javaIFDelim+map[i].toLowerCase();
//				}
//				iFlow.put(pid+_javaIFDelim+map[0].toLowerCase(), toBeAdded);
//			}
//		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}
	
	private void parseJSONList(JSONArray listOfTypes, String pid, String type){
		Iterator<JSONObject> listOfSourcesIt = listOfTypes.iterator();
		while(listOfSourcesIt.hasNext()){
			JSONObject o = listOfSourcesIt.next();
			String id = (String) o.get(_paramId);
			String location = (String) o.get(_paramLocation);
			String offset = String.valueOf(o.get(_paramOffset));
			String parampos = String.valueOf(o.get(_paramParamPos));
			JSONArray listOfSignatures = (JSONArray) o.get("signature");			
			Iterator<String>listOfSignaturesIt = listOfSignatures.iterator();
			while(listOfSignaturesIt.hasNext()){
				String signature = listOfSignaturesIt.next();
				addPoi(pid, type, id, location, offset, signature, parampos);
			} 
		}			
	}

//	private void parseList(String listOfPoi, String pid, String type,String sep1, String sep2) {
//		String id, location, offset, parampos, signature;
//
//		String[] listOfPoiArr = listOfPoi.split(sep2);
//		for (int i = 0; i < listOfPoiArr.length - 1; i++) {
//			String currPoi = listOfPoiArr[i];
//			if (currPoi == null)
//				break;
//			String[] poiPars = currPoi.split(sep1);
//			
//			assert(poiPars.length>=10);
//
//			Map<String, String> pars = new HashMap<String, String>();
//			for (int o = 0; o < 4; o++)
//				pars.put(poiPars[2 * o], poiPars[2 * o + 1]);
//			id = pars.get(_paramId);
//			location = pars.get(_paramLocation);
//			offset = pars.get(_paramOffset);
//			parampos = pars.get(_paramParamPos);
//
//			for (int o = 9; o < poiPars.length; o++) {
//				signature = poiPars[o];
//				addPoi(pid, type, id, location, offset, signature, parampos);
//			}
//		}
//	}

	private void addPoi(String pid, String type, String id, String location,
			String offset, String signature, String parampos) {
		String prefix = type;

		String[] infoConts = new String[] {
				signature,
				location + _javaIFDelim + offset + _javaIFDelim + signature,
				location + _javaIFDelim + offset + _javaIFDelim + signature
						+ _javaIFDelim + parampos,
				signature + _javaIFDelim + parampos };

		Set<IContainer> set=containersByPid.get(pid);
		if (set==null) set = new HashSet<IContainer>();
		for (String infoCont : infoConts) {
			// infoCont = prefix + infoCont;
			IContainer infoContId = _informationFlowModel
					.getContainer(new SourceSinkName(pid, prefix, infoCont));

			_logger.debug("contID = " + infoContId);

			if (infoContId == null) {
				infoContId = _messageFactory.createContainer();

				_informationFlowModel.addName(new SourceSinkName(pid, prefix,
						infoCont), infoContId, true);
			}
			
			set.add(infoContId);
			_logger.debug(_informationFlowModel.toString());
		}

		
		//create Poi container
		String poiName = pid+_javaIFDelim+id;
		IContainer poiId = _informationFlowModel
				.getContainer(new NameBasic(poiName));
		if (poiId == null) {
			poiId = _messageFactory.createContainer();
			_informationFlowModel.addName(new NameBasic(poiName), poiId, true);
		}

		set.add(poiId);
		containersByPid.put(pid, set);

		
		// Process alias relationship
		IContainer sig = _informationFlowModel.getContainer(new SourceSinkName(
				pid, prefix, infoConts[0]));
		IContainer locSig = _informationFlowModel
				.getContainer(new SourceSinkName(pid, prefix, infoConts[1]));
		IContainer locSigPar = _informationFlowModel
				.getContainer(new SourceSinkName(pid, prefix, infoConts[2]));
		IContainer sigPar = _informationFlowModel
				.getContainer(new SourceSinkName(pid, prefix, infoConts[3]));

		if (type.toLowerCase().equals("source")) {
			_informationFlowModel.addAlias(sig, locSig);
			_informationFlowModel.addAlias(locSig, locSigPar);
			_informationFlowModel.addAlias(sig, sigPar);
			_informationFlowModel.addAlias(sigPar, locSigPar);
			_informationFlowModel.addAlias(locSigPar, poiId);
		} else if (type.toLowerCase().equals("sink")) {
			_informationFlowModel.addAlias(poiId, locSigPar);
			_informationFlowModel.addAlias(locSigPar, locSig);
			_informationFlowModel.addAlias(locSigPar, sigPar);
			_informationFlowModel.addAlias(locSig, sig);
			_informationFlowModel.addAlias(sigPar, sig);
		}
	}
}
