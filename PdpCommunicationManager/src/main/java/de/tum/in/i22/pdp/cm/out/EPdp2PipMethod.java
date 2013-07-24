package de.tum.in.i22.pdp.cm.out;


public enum EPdp2PipMethod {
	EVALUATE_PREDICATE((byte)1),
	GET_CONTAINER_FOR_DATA((byte)2),
	GET_DATA_IN_CONTAINER((byte)3);
	
    private final byte value;

    private EPdp2PipMethod(byte value) {
        this.value = value;
    }
    
    public byte getValue() {
        return value;
    }
    
    public static EPdp2PipMethod fromByte(byte b) {

        for(EPdp2PipMethod t : values())
        {
            if(t.getValue() == b)
                return t;
        } 
        
        throw new RuntimeException("Byte value " + b + " not valid for EPmp2PdpMethod.");
    }
}
