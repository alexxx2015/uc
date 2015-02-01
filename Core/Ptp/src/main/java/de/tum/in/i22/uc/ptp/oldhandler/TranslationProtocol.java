package de.tum.in.i22.uc.ptp.oldhandler;

/**
 * @author Cipri
 * The communication protocol is based on string matching for commands.
 */
public class TranslationProtocol {

	public final static String RESULT_OK = "TRANSLATION_OK";
	public final static String RESULT_ERROR = "TRANSLATION_FAILED";
	
	public final static String INCORRECT_INPUT = "INCORRECT_INPUT";
	public final static String POLICY_IN = "POLICY_IN";

	public final static String SEPARATOR = "####";

	public final static String TRANSLATION_END = "TRANSLATION_END";
	
}
