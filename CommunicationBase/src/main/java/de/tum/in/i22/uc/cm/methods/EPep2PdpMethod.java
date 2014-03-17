package de.tum.in.i22.uc.cm.methods;


public enum EPep2PdpMethod {
	NOTIFY_EVENT((byte)1),
	UPDATE_INFORMATION_FLOW_SEMANTICS((byte)2);
	
    private final byte value;

    private EPep2PdpMethod(byte value) {
        this.value = value;
    }
    
    public byte getValue() {
        return value;
    }
    
    public static EPep2PdpMethod fromByte(byte b) {

        for(EPep2PdpMethod t : values())
        {
            if(t.getValue() == b)
                return t;
        } 
        
        throw new RuntimeException("Byte value " + b + " not valid for EPep2PdpMethod.");
    }
}
