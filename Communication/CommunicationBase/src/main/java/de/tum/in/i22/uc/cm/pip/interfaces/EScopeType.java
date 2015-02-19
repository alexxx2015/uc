package de.tum.in.i22.uc.cm.pip.interfaces;

public enum EScopeType {
	UNKNOWN, 
	SPECIAL, //for the time being this is used to return multiple scopes as attributes of one scope
	SAVE_FILE, 
	LOAD_FILE, 
	COPY_CLIPBOARD, 
	PASTE_CLIPBOARD, 
	SEND_SOCKET,
	READ_SOCKET, 
	SEND_EMAIL, 
	GET_EMAIL, 
	JBC_GENERIC_LOAD, 
	JBC_GENERIC_SAVE, 
	BIN_GENERIC_LOAD, 
	BIN_GENERIC_SAVE,
	TAR_MERGE,
	TAR_SPLIT
}
