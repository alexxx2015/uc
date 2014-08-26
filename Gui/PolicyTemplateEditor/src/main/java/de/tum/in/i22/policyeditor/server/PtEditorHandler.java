package de.tum.in.i22.policyeditor.server;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.policyeditor.editor.PolicyTemplatesEditor;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2PtEditor;

public class PtEditorHandler implements IAny2PtEditor {

	private static final Logger _logger = LoggerFactory.getLogger(PtEditorHandler.class);
	
	public PtEditorHandler(){
		
	}

	@Override
	public IStatus specifyPolicyFor(Set<IContainer> representations,
			String dataClass) {
		PolicyTemplatesEditor.startEditor(representations, dataClass);
		return new StatusBasic(EStatus.OKAY);
	}

	
}