package de.tum.in.i22.uc.cm.datatypes.java.data;

public class SourceData extends JavaData {

    private String sourceId;
    private long timeStamp;
    
    public SourceData(String id) {
	super(id);
	
	String[] comps = id.split("\\" + DLM);
	sourceId = comps[0];
	timeStamp = Long.valueOf(comps[1]);
    }

    public SourceData(String sourceId, long timeStamp) {
	this(sourceId + DLM + timeStamp);
    }

    public String getSourceId() {
        return sourceId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
    
    

}
