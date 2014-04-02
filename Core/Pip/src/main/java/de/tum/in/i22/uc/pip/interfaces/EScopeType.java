package de.tum.in.i22.uc.pip.interfaces;


// TODO Ugly. Generic type ScopeType should not contain layer/application specific parts.
@Deprecated
public enum EScopeType {
	EMPTY, SAVE_FILE, LOAD_FILE, COPY_CLIPBOARD, PASTE_CLIPBOARD, SEND_SOCKET, READ_SOCKET, SEND_EMAIL, GET_EMAIL, GENERIC_IN, GENERIC_OUT
}
