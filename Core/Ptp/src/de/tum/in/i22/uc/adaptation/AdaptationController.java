package de.tum.in.i22.uc.adaptation;

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
	
	public void mergeDomainModels() throws DomainMergeException{
		
		DataContainerAdaptationController dcController = new DataContainerAdaptationController(baseDomainModel, newDomainModel);
		dcController.mergeDomainModels();
		
		ActionTransformerAdaptationController atController = new ActionTransformerAdaptationController(baseDomainModel, newDomainModel);
		atController.mergeDomainModels();
		
		SystemAdaptationController sysController = new SystemAdaptationController(baseDomainModel, newDomainModel);
		sysController.mergeDomainModels();
	}
	
}
