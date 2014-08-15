package de.tum.in.i22.uc.adaptation.model;

public class DomainModel {

	public static enum DomainLayer {
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
		pimLayer = new LayerModel("PIM", DomainLayer.PIM);
		psmLayer = new LayerModel("PSM", DomainLayer.PSM);
		ismLayer = new LayerModel("ISM", DomainLayer.ISM);
		pimLayer.setRefinedAs(psmLayer);
		psmLayer.setRefinedAs(ismLayer);
		ismLayer.setRefinedAs(ismLayer);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public LayerModel getLayer(DomainLayer type){
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
	}
	public LayerModel getPsmLayer() {
		return psmLayer;
	}
	public void setPsmLayer(LayerModel psmLayer) {
		this.psmLayer = psmLayer;
	}
	public LayerModel getIsmLayer() {
		return ismLayer;
	}
	public void setIsmLayer(LayerModel ismLayer) {
		this.ismLayer = ismLayer;
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
