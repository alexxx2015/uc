package de.tum.in.i22.uc.cm.methods;



public enum EPipRequestMethod {
	HAS_DATA((byte)1),
	HAS_CONTAINER((byte)2),
	NOTIFY_EVENT((byte)3);

    private final byte value;

    private EPipRequestMethod(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static EPipRequestMethod fromByte(byte b) {

        for(EPipRequestMethod t : values())
        {
            if(t.getValue() == b)
                return t;
        }

        throw new RuntimeException("Byte value " + b + " not valid for EPipRequestMethod.");
    }
}
