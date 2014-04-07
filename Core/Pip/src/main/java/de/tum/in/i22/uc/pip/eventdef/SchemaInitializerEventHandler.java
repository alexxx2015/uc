package de.tum.in.i22.uc.pip.eventdef;

import java.util.Map;
import java.util.Map.Entry;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.settings.Settings;

public class SchemaInitializerEventHandler extends BaseEventHandler {

	public SchemaInitializerEventHandler() {
		super();
	}

	@Override
	public IStatus update() {

		Map<IName,IData> initialRepresentations = Settings.getInstance().getPipInitialRepresentations();

		if (initialRepresentations == null) {
			return new StatusBasic(EStatus.OKAY);
		}

		for (Entry<IName, IData> entry : initialRepresentations.entrySet()) {
			IName name = entry.getKey();
			IData data = entry.getValue();

			if (name != null && data != null) {
				IContainer cont = basicIfModel.getContainer(name);
				if (cont == null) {
					cont = new ContainerBasic();
					basicIfModel.addName(name, cont);
				}
				basicIfModel.addDataToContainer(data, cont);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
