package de.tum.in.i22.uc.gui.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class OffsetParameter {
	/*
	 * parameter = -1 --> return value parameter = 0 --> method invocation
	 * 
	 * (not sure we need the distinction. for sources only -1 makes sense and
	 * for sinks only 0 does)
	 * 
	 * parameter = n>0 --> n-th parameter
	 */

	private Map<Integer, OffsetNode> type;
	private String signature;

	public Map<Integer, OffsetNode> getType() {
		return type;
	}

	public void setType(Map<Integer, OffsetNode> type) {
		this.type = type;
	}


	public OffsetParameter(Map<Integer, OffsetNode> map, String sign) {
		this.type = map;
		this.signature = sign;
	}

	public OffsetParameter(int parameter, OffsetNode on, String sign) {
		Map<Integer,OffsetNode> m= new HashMap<Integer,OffsetNode>();
		m.put(parameter, on);
		this.type = m;
		this.signature = sign;
	}
	
	
	public OffsetParameter(Map<Integer, OffsetNode> map) {
		this.type = (map != null ? map : new HashMap<Integer, OffsetNode>());
		this.signature = "<no signature provided>";
	}

	
	
	public String getIdOfPar(int parameter){
		if (type==null) return null;
		OffsetNode on =this.type.get(parameter);
		if (on==null) return null;
		return on.getId();
	}
	
	
	/*
	 * does a bit of math with the nodeType. note that no parameter should be
	 * given type error, nor none.
	 */
	public void append(Map<Integer, OffsetNode> map) {
		if ((map==null)||!(map instanceof Map<?,?>)) return;
		
		for (Entry<Integer, OffsetNode> e : map.entrySet()) {
			OffsetNode on = this.type.get(e.getKey());
			if (on == null) {
				this.type.put(e.getKey(), e.getValue());
			} else {
				StaticAnalysis.nodeType oldT = on.getType();
				String oldI = on.getId();

				OffsetNode onNew = e.getValue();
				if (onNew != null) {
					String newI = onNew.getId();
					StaticAnalysis.nodeType newT = onNew.getType();
				
					if (oldT==newT){
						if (oldI.equals(newI)){
							System.err.println("No need to merge, old and new are the same");
							break;
						} else{
							System.err.println("Not possible to merge, parameter "+e.getKey()+" alrady exists but with different id");
							break;
						}
					}
					
					StaticAnalysis.nodeType resT = StaticAnalysis.append(oldT, newT);
				
					on.setType(resT);

					if (resT==StaticAnalysis.nodeType.ERROR) return;

					if (oldT != resT){
						on.setId(on.getId()+"-"+onNew.getId());
					}
						
				} else {
					System.err.println("Error! wrong or no type provided!");
				}
			}
		}
	}

	
	public void append(int parameter, OffsetNode on) {
		Map<Integer,OffsetNode> m= new HashMap<Integer,OffsetNode>();
		m.put(parameter, on);
		append(m);
	}
	
	public void append(int parameter, StaticAnalysis.nodeType t) {
		OffsetNode on=new OffsetNode("test",t);
		append(parameter,on);
	}
	
	public void append(int parameter, String id, StaticAnalysis.nodeType t) {
		OffsetNode on=new OffsetNode(id,t);
		append(parameter,on);
	}
	
	
	public Set<Integer> getParsOfType(StaticAnalysis.nodeType t) {
		if (!(t instanceof StaticAnalysis.nodeType))
			return null;
		Set<Integer> res = new HashSet<Integer>();
		for (Entry<Integer, OffsetNode> e : this.type.entrySet()) {
			if (e.getValue().getType().equals(t))
				res.add(e.getKey());
		}
		return res;
	}

	public StaticAnalysis.nodeType getTypeOfPar(int parameter) {
		try {
			OffsetNode on=type.get(parameter);
			if (on == null)
				return StaticAnalysis.nodeType.NONE;
			StaticAnalysis.nodeType n = on.getType();
			if (n == null)
				return StaticAnalysis.nodeType.NONE;
			return n;
		} catch (Exception e) {
			System.err
					.println("Something went horribly wrong in function getTypeOfPar!!!");
			return StaticAnalysis.nodeType.ERROR;
		}
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
