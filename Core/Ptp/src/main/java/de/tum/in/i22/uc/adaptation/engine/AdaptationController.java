package de.tum.in.i22.uc.adaptation.engine;

import de.tum.in.i22.uc.adaptation.model.DomainModel;

public class AdaptationController {

	private DomainModel baseDomainModel;
	private DomainModel newDomainModel;
	
	public AdaptationController(){
		baseDomainModel = new DomainModel();
		newDomainModel = new DomainModel();
	}
	
	public void setBaseDomainModel(DomainModel baseDM){
		this.baseDomainModel = baseDM;
	}
	
	public void setNewDomainModel(DomainModel newDM){
		this.newDomainModel = newDM;
	}
	
	/**
	 * Return the Base Domain Model.
	 * After adaptation, the BaseDomainModel will contain all the changes.
	 * @return
	 */
	public DomainModel getBaseDomainModel(){
		return this.baseDomainModel;
	}
	
	public DomainModel getNewDomainModel(){
		return this.newDomainModel;
	}
	
	/**
	 * Merges the BaseDomainModel and the NewDomainModel.
	 * The BaseDomainModel will be the updated model.
	 * @return updated elements or links between elements
	 * @throws DomainMergeException
	 */
	public int mergeDomainModels() throws DomainMergeException{
		int totalUpdates = 0;
		
		DataContainerAdaptationController dcController = new DataContainerAdaptationController(baseDomainModel, newDomainModel);
		dcController.mergeDomainModels();
		totalUpdates += DataContainerAdaptationController.getUpdatedElements();
		
		ActionTransformerAdaptationController atController = new ActionTransformerAdaptationController(baseDomainModel, newDomainModel);
		atController.mergeDomainModels();
		totalUpdates += ActionTransformerAdaptationController.getUpdatedElements();
		
		SystemAdaptationController sysController = new SystemAdaptationController(baseDomainModel, newDomainModel);
		sysController.mergeDomainModels();
		totalUpdates += SystemAdaptationController.getUpdatedElements();
		
		return totalUpdates;
	}
	
}
