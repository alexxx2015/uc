package de.tum.in.i22.policyeditor.server;

import java.util.Set;

import org.apache.thrift.TException;

import de.tum.in.i22.policyeditor.editor.PolicyTemplatesEditor;
import de.tum.in.i22.policyeditor.logger.EditorLogger;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TStatus;

public class EditorHandler implements IAny2Editor {

	private EditorLogger logger ;
	
	public EditorHandler(){
		this.logger = EditorLogger.instance();
	}

	@Override
	public TStatus specifyPolicyFor(Set<TContainer> representations, String dataClass) throws TException {

		PolicyTemplatesEditor.startEditor(representations, dataClass);
		
		return TStatus.OKAY;
	}
	
}