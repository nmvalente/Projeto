package utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import channels.ReceiveDataChannel;
import channels.SendDataChannel;
import interfaces.Main;
import protocols.Peer;

public class Utils{

	public static final String version = "1.0";

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

	
}
