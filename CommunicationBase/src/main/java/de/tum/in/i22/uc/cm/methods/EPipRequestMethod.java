package de.tum.in.i22.uc.cm.methods;



public enum EPipRequestMethod {
	HAS_ALL_DATA((byte)1),
	HAS_ANY_DATA((byte)2),
	HAS_ALL_CONTAINERS((byte)3),
	HAS_ANY_CONTAINER((byte)4),
	NOTIFY_DATA_TRANSFER((byte)5),
	NOTIFY_ACTUAL_EVENT((byte)6);

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

