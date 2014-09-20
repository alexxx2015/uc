package de.tum.in.i22.uc.ptp.adaptation.model;

public class DomainModel {

	public static enum LayerType {
		PIM,
		PSM,
		ISM
	}
	
	private LayerModel pimLayer;
	private LayerModel psmLayer;
	private LayerModel ismLayer;

	private String name ;
	
	public DomainModel(){
		this("<EmptyDomainModel>");
	}
	
	public DomainModel(String name){
		this.name = name;
		pimLayer = new LayerModel("PIM", LayerType.PIM);
		psmLayer = new LayerModel("PSM", LayerType.PSM);
		ismLayer = new LayerModel("ISM", LayerType.ISM);
		pimLayer.setRefinedAs(psmLayer);
		psmLayer.setRefinedAs(ismLayer);
		ismLayer.setRefinedAs(ismLayer);
		pimLayer.setParentDomainModel(this);
		psmLayer.setParentDomainModel(this);
		ismLayer.setParentDomainModel(this);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the total number of elements contained by this model.
	 * @return
	 */
	public int getElementsSize(){
		int ismE = this.ismLayer.getElementsSize();
		int psmE = this.psmLayer.getElementsSize();
		int pimE = this.pimLayer.getElementsSize();
		int totalE = ismE + psmE + pimE;
		return totalE;
	}
	
	public LayerModel getLayer(LayerType type){
		switch(type){
		case PIM:
			return pimLayer;
		case PSM:
			return psmLayer;
		case ISM:
			return ismLayer;
		default:
			return null;
		}
	}
	
	public LayerModel getPimLayer() {
		return pimLayer;
	}
	public void setPimLayer(LayerModel pimLayer) {
		this.pimLayer = pimLayer;
		pimLayer.setParentDomainModel(this);
	}
	public LayerModel getPsmLayer() {
		return psmLayer;
	}
	public void setPsmLayer(LayerModel psmLayer) {
		this.psmLayer = psmLayer;
		psmLayer.setParentDomainModel(this);
	}
	public LayerModel getIsmLayer() {
		return ismLayer;		
	}
	public void setIsmLayer(LayerModel ismLayer) {
		this.ismLayer = ismLayer;
		ismLayer.setParentDomainModel(this);
	}
	
	public String toString(){
		String result ="DomainModel: " + this.name;
		result +="\n";
		String pimString = "\n"+this.pimLayer.toString();
		String psmString = "\n"+this.psmLayer.toString();
		String ismString = "\n"+this.ismLayer.toString();
		result += pimString;
		result += psmString;
		result += ismString;
		return result;
	}
	
}
