package de.tum.in.i22.uc.cm.datatypes.java.names;

public class BasicJavaName extends JavaName {

	public BasicJavaName(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public BasicJavaName(String... name){
		super(String.join(DLM, name));
	}

	@Override
	public String getPid() {
		// TODO Auto-generated method stub
		return null;
	}

}
