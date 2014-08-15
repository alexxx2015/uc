package de.tum.in.i22.policyeditor.tests;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.tum.in.i22.policyeditor.editor.PolicyTemplatesEditor;
import de.tum.in.i22.uc.thrift.types.TAttribute;
import de.tum.in.i22.uc.thrift.types.TAttributeName;
import de.tum.in.i22.uc.thrift.types.TContainer;

public class EditorClientTest {

	@Test
	public void launchEditorSimple() {
		String dataClass = "picture";
		Set<TContainer> representations = getTestContainers();
		
		PolicyTemplatesEditor.startEditor(representations, dataClass);
		
		assertTrue(true);
	}
	
	
	private Set<TContainer> getTestContainers(){
		Set<TContainer> representations = new HashSet(); 
		for(int i =0; i<3; i++){
			TContainer c = new TContainer();
			c.setId("container_"+i);
			TAttribute attr = new TAttribute();
			TAttributeName name = TAttributeName.OWNER;
			attr.setName(name);
			attr.setValue("attr"+i);
			c.addToAttributes(attr);
			representations.add(c);
		}
		return representations;
	}
	
}
