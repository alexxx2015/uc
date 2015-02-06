package de.tum.in.i22.uc.pip.eventdef.linux;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

/**
 * 
 * @author Florian Kelbert
 * 
 */
public class ExecveEventHandler extends AbstractScopeEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		String filename = null;
		String cmdline = null;

		_logger.trace("Executing Execve eventhandler");

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			filename = getParameterValue("filename");
			cmdline = getParameterValue("cmdline");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		_logger.trace(" host:" + host + " pid:" + pid + " filename:" + filename + " cmdline:" + cmdline);

		IName fileName = FilenameName.create(host, LinuxEvents.toRealPath(filename));
		IName procName = ProcessName.create(host, pid);

		IContainer fileCont = _informationFlowModel.getContainer(fileName);
		IContainer procCont = _informationFlowModel.getContainer(procName);

		boolean performUsualSemantics = true;

		//TODO move the following code in a separated method
		
		if (cmdline != null) {
			String[] cmds = cmdline.split(" ");

			// 1: check for specific commands
			if ((cmds[0].equals("tar")) || (cmds[0].equals("/bin/tar"))) {

				IContainer srcCont, dstCont;
				IName src, dst;

				boolean structActive = Settings.getInstance().getEnabledInformationFlowModels()
						.contains(EInformationFlowModel.STRUCTURE);
				Map<String,Set<IData>> map=null;
				
				
				// 3: decide according to its parameters

				if (cmds[1].contains("c")) { // MERGER
					dst = FilenameName.create(host,LinuxEvents.toRealPath(filename, cmds[2]));
					dstCont = _informationFlowModel.getContainer(dst);

					if (dstCont == null) {
						dstCont = new FileContainer();
						_informationFlowModel.addName(dst, dstCont);
					}

					if (cmds.length > 3) {
						
						if (structActive){
							map=new HashMap<String,Set<IData>>();
						}
						
						for (int i = 3; i < cmds.length; i++) {
							performUsualSemantics = false;
							src = FilenameName.create(host,LinuxEvents.toRealPath(filename, cmds[i]));
							srcCont = _informationFlowModel.getContainer(src);

							if (srcCont != null) {
								// 3: check if structure modeling is enabled
								if (structActive) {
									Set<IData> sd=_informationFlowModel.getData(src);
									map.put(cmds[i], sd);
								} else {
									_informationFlowModel.copyData(src, dst);
								}
							}
						}
						
						if (structActive){
							IData newD=_informationFlowModel.newStructuredData(map);
							_informationFlowModel.addData(newD, dstCont);
						}
						
					}
				} else if (cmds[1].contains("x")) { // SPLITTER
					src = FilenameName.create(host,LinuxEvents.toRealPath(filename, cmds[2]));
					srcCont = _informationFlowModel.getContainer(src);

					if ((srcCont != null) && (cmds.length > 3)) {
						for (int i = 3; i < cmds.length; i++) {
							performUsualSemantics = false;
							dst = FilenameName.create(host,LinuxEvents.toRealPath(filename, cmds[i]));
							dstCont = _informationFlowModel.getContainer(dst);

							if (dstCont == null) {
								dstCont = new FileContainer();
								_informationFlowModel.addName(dst, dstCont);
							}

							if (srcCont != null) {
								// 3: check if structure modeling is enabled
								if (structActive) {
									for (IData d: _informationFlowModel.getData(src)){
										Map<String,Set<IData>> res=_informationFlowModel.getStructureOf(d);
										if (res==null){ //no structured data --> transfer it as is
											_informationFlowModel.addData(d, dstCont);
										} else { //data has a structure --> check if possible to split, otherwise add everything
											Set<IData> splitSet = res.get(cmds[i]);
											if (splitSet == null){
												_informationFlowModel.addData(d, dstCont);
											} else{
												_informationFlowModel.addData(splitSet, dstCont);
											}
										}
									}	 
								} else {
									_informationFlowModel.copyData(src, dst);
								}
							}
						}
					}
				}
			}
		}

		if (performUsualSemantics) {

			if (fileCont == null) {
				fileCont = new FileContainer();
				_informationFlowModel.addName(fileName, fileCont);
			}

			if (procCont == null) {
				procCont = new ProcessContainer(host, pid);
				_informationFlowModel.addName(procName, procCont);
			}

			_informationFlowModel.copyData(fileName, procName);
		}

		return STATUS_OKAY;
	}
}
