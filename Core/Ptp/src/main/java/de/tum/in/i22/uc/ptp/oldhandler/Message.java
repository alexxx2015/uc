package de.tum.in.i22.uc.ptp.oldhandler;

/**
 * @author Cipri
 * This is a wrapper class for the message used in the communication protocol.
 */
public class Message {

	private String header ;
	private String param ;
	private String payload ;
	
	public Message(byte[] msg){
		parseBytes(msg);
	}
	
	public Message(String header, String param, String payload){
		this.header = header;
		this.param = param;
		this.payload = payload;
	}
	
	public Message(){
		this.header = "";
		this.param = "";
		this.payload = "";
	}
	
	public String header(){
		return header;
	}
	
	public String payload(){
		return payload;
	}
	
	public String param(){
		return param;
	}
	
	private void parseBytes(byte[] bytes){
		String msg = new String(bytes);
//		System.out.println();
//		System.out.println("message:" + msg);
		String[] data = msg.split(TranslationProtocol.SEPARATOR);
		if(data.length != 3){
			header = TranslationProtocol.INCORRECT_INPUT;
			param = TranslationProtocol.INCORRECT_INPUT;
			payload = TranslationProtocol.INCORRECT_INPUT;
		} 
		else {
			header = data[0];
			param = data[1];
			payload = data[2];
		}
	}
	
	public byte[] getBytes(){
		String msg = header + TranslationProtocol.SEPARATOR + param + TranslationProtocol.SEPARATOR + payload;
		byte[] data = msg.getBytes();
		return data;
	}
	
	public String toString(){
		String msg = header + TranslationProtocol.SEPARATOR + param + TranslationProtocol.SEPARATOR + payload;
		return msg;
	}
}
