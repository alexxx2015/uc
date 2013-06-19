package de.tum.in.i22.pdp.cm.in;


/**
 * Used in {@link FastServiceHandler} to distinguish between methods.
 * See {@link FastServiceHandler} implementation.
 * @author Stoimenov
 *
 */
public enum EPmp2PdpMethods {
	DEPLOY_MECHANISM((byte)1),
	EXPORT_MECHANISM((byte)2),
	REVOKE_MECHANISM((byte)3);
	
    private final byte value;

    private EPmp2PdpMethods(byte value) {
        this.value = value;
    }
    public byte getValue() {
        return value;
    }
}
