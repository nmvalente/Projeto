package utils;

import java.nio.charset.StandardCharsets;


public class Utils{

	public static final String version = "1.0";
	public static enum messageTypes{PUTCHUNK,STORED,GETCHUNK,CHUNK,DELETE,REMOVED};	
	public static final int WAITING_TIMES = 5;
	public static final String CHARSET_NAME = "utf-8";
	public static final String CRLFCRLF = "\r\n\r\n";
	public static final String HASH_TYPE = "SHA-256";
	public static final int CHUNK_MAX_SIZE = 64000;
	public static final int MC  = 0;
	public static final int MDB = 1;
	public static final int MDR = 2;
	public static final int BUFFER_SIZE = 1024;

	public static String convertBytetoString(byte[] array){

		String string = new String(array, StandardCharsets.UTF_8);
		return string;	
	}

	public static int convertBytetoInt(byte[] array){

		String string = new String(array, StandardCharsets.UTF_8);
		int integer = Integer.parseInt(string);

		return integer;
	}

	public static byte[] convertInttoByte(int number){

		String string = String.valueOf(number);

		return string.getBytes();
	}

	public static boolean belongsToMessageTypes(String type){
		for (messageTypes enumTypes : messageTypes.values())
			if (enumTypes.name().equals(type))
				return true;
		return false;
	}

}
