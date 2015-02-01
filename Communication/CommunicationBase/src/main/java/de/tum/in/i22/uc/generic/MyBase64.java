package de.tum.in.i22.uc.generic;

import java.nio.charset.Charset;
import java.util.Base64;

public class MyBase64 {

	public static String toBase64(String s) {
		return new String(Base64.getEncoder().encode(s.getBytes(Charset.defaultCharset())), Charset.defaultCharset());
	}

	public static String fromBase64(String s) {
		return new String(Base64.getDecoder().decode(s.getBytes(Charset.defaultCharset())), Charset.defaultCharset());
	}
}
