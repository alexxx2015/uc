package de.tum.in.i22.uc.adaptation.engine;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.adaptation.model.DomainModel;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.adaptation.model.SystemModel;

public class SystemAdaptationController {

	/**
	 * The Base Domain Model.
	 * The results of the adaptation will be stored here.
	 */
	private DomainModel baseDm;
	
	private static final Logger logger = LoggerFactory.getLogger(SystemAdaptationController.class);
	
	/**
	 * The New Domain Model which possibly contains new definitions. 
	 */
	private DomainModel newDm;
	
	public SystemAdaptationController(DomainModel baseDomainModel, DomainModel newDomainModel) {
		this.baseDm = baseDomainModel;
		this.newDm = newDomainModel;
	}
	
	/**
	 * BOTTOM-UP approach	
	 * STEP 0: merge domain models: Systems
	 * @throws DomainMergeException
	 */
	public void mergeDomainModels() throws DomainMergeException{
		
		// see STAGE 1
		mergeInnerLinks(baseDm, newDm);
		// see STAGE 2
		mergeCrossLinks(baseDm, newDm);
		
	}

	private void mergeInnerLinks(DomainModel baseDm, DomainModel newDm) {
		mergeInnerLinksLayer(baseDm.getIsmLayer(), newDm.getIsmLayer());
		mergeCrossLinksLayer(baseDm.getIsmLayer(), newDm.getIsmLayer());
		
		mergeInnerLinksLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
		mergeCrossLinksLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
	}
	
	

	private void mergeInnerLinksLayer(LayerModel baseLayer, LayerModel newLayer) {
		ArrayList<SystemModel> systems = newLayer.getSystems();
		for(SystemModel newS : systems){
			addNewSystemToBase(newS, baseLayer);
		}
		
	}
	
	
	private void addNewSystemToBase(SystemModel newS, LayerModel baseLayer) {
		
	}

	private void mergeCrossLinksLayer(LayerModel psmLayer, LayerModel psmLayer2) {
		// TODO Auto-generated method stub
		
	}
	
	private void mergeCrossLinks(DomainModel baseDm, DomainModel newDm) {
		// TODO Auto-generated method stub
		
	}
	
	
}
