package de.tum.in.i22.uc.cm;

/**
 * This class should be use for IMessageFactory creation.
 * This gives possibility to completely replace the Basic
 * Messages with some other implementation.
 * @author Stoimenov
 *
 */
public class MessageFactoryCreator {
	public static IMessageFactory createMessageFactory() {
		return new MessageFactory();
	}
}
