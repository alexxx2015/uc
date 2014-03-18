package de.tum.in.i22.uc.cm.methods;


public enum EPipResponse {
	VOID((byte)1),
	ICONTAINER((byte)2),
	ISTATUS((byte)3);

    private final byte value;

    private EPipResponse(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static EPipResponse fromByte(byte b) {

        for(EPipResponse t : values())
        {
            if(t.getValue() == b)
                return t;
        }

        throw new RuntimeException("Byte value " + b + " not valid for EPipRequestMethod.");
    }
}
