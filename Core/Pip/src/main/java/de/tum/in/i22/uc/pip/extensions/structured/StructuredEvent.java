package de.tum.in.i22.uc.pip.extensions.structured;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModel;
import de.tum.in.i22.uc.pip.eventdef.linux.LinuxEvents;

public abstract class StructuredEvent {
	protected String _name;
	protected IAnyInformationFlowModel _informationFlowModel;

	protected static final Logger _logger = LoggerFactory.getLogger(StructuredEvent.class);

	public static StructuredEvent createFromExecve(String host, String filename, String[] cmds,
			IAnyInformationFlowModel _informationFlowModel) {
		IContainer srcCont, dstCont;
		IName src, dst;
		Map<String, IName> labelContMap = new HashMap<String, IName>();

		if (cmds == null) {
			_logger.trace("null cmds -> returning null");
			return null;
		}

		if ((cmds[0].equals("tar")) || (cmds[0].equals("/bin/tar"))) {
			if (cmds[1].contains("c")) { // MERGER
				dst = FilenameName.create(host, LinuxEvents.toRealPath(filename, cmds[2]));
				dstCont = _informationFlowModel.getContainer(dst);
				if (dstCont == null) {
					dstCont = new FileContainer();
					_informationFlowModel.addName(dst, dstCont);
				}
				if (cmds.length > 3) {
					for (int i = 3; i < cmds.length; i++) {
						src = FilenameName.create(host, LinuxEvents.toRealPath(filename, cmds[i]));
						srcCont = _informationFlowModel.getContainer(src);
						labelContMap.put(src.getName(), src);
					}
				}

				return new Merger("tar Merger", labelContMap, dst, _informationFlowModel);

			} else if (cmds[1].contains("x")) { // SPLITTER
				src = FilenameName.create(host, LinuxEvents.toRealPath(filename, cmds[2]));
				srcCont = _informationFlowModel.getContainer(src);

				if ((srcCont != null) && (cmds.length > 3)) {
					for (int i = 3; i < cmds.length; i++) {
						dst = FilenameName.create(host, LinuxEvents.toRealPath(filename, cmds[i]));
						dstCont = _informationFlowModel.getContainer(dst);

						if (dstCont == null) {
							dstCont = new FileContainer();
							_informationFlowModel.addName(dst, dstCont);
						}
						
						labelContMap.put(dst.getName(), dst);
					}
					return new Splitter("tar Splitter", src, labelContMap, _informationFlowModel);
				}
			}
		}
		return null;
	}


}