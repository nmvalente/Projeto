package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import interfaces.Main;
import protocols.Peer;
import utils.Utils;

import java.util.Date;

public class ReceiveDataChannel extends Thread{

	private String name;
	private MulticastSocket socket;
	private Peer peer;

	public ReceiveDataChannel(String name, MulticastSocket ms, Peer peer){
		this.name = name;
		this.socket = ms;
		this.peer = peer;
	}

	public String getCurrentTime(){
		Date date = new Date();
		return date.toString();
	}

	public void run(){
		try{
			if(name == "MC")
				Main.windows.printlnReceiverMC(getCurrentTime() + " - Started receiver thread :: "+ name);
			if(name == "MDB")
				Main.windows.printlnReceiverMDB(getCurrentTime() + " - Started receiver thread :: "+ name);
			if(name == "MDR")
				Main.windows.printlnReceiverMDR(getCurrentTime() + " - Started receiver thread :: "+ name);
		}
		catch (ArithmeticException ex){
			Main.windows.printlnReceiverMC(getCurrentTime() + " - Error in starter Receiver"); 
		}
		try{
			byte[] buf;
			DatagramPacket dg;
			String dgString, message;
			String[] parts;
			do{
				try{Thread.sleep(10);}catch(InterruptedException e){e.getMessage(); System.err.println("Error in sleep");}
				buf = new byte[Utils.BUFFER_SIZE];

				dg = new DatagramPacket( buf , buf.length );
				socket.receive(dg);
				dgString = new String( dg.getData() );
				parts = dgString.split("\\s");
				int PeerIDThatSends = Integer.parseInt(parts[2]);

				if ( !dg.getAddress().toString().substring(1).equals(peer.getLocalhost()) ){
					if(peer.getPeerId() != PeerIDThatSends){

						message = peer.getInbox().addToUnseenMessages(dg.getAddress().toString(), dg.getPort() , dgString );
						try{
							if(name == "MC")
								Main.windows.printlnReceiverMC(getCurrentTime() + " - RECEIVED - " + message);
							if(name == "MDB")
								Main.windows.printlnReceiverMDB(getCurrentTime() + " - RECEIVED - " + message);
							if(name == "MDR")
								Main.windows.printlnReceiverMDR(getCurrentTime() + " - RECEIVED - " + message);
						}
						catch(ArithmeticException e){
							if(name == "MC")
								Main.windows.printlnReceiverMC(getCurrentTime() + " - Error in  Receiver MC"); 
							if(name == "MDB")
								Main.windows.printlnReceiverMDB(getCurrentTime() + " - Error in  Receiver MDB"); 
							if(name == "MDR")
								Main.windows.printlnReceiverMDR(getCurrentTime() + " - Error in  Receiver MDR");
						}
					} 
				}
				try{Thread.sleep(10);}catch(InterruptedException e){e.getMessage();System.err.println("Error in sleep");}
			} while(true);
		}
		catch(IOException n){
			Main.windows.printlnReceiverMC(getCurrentTime() + " - Connection terminated");
		}
	}
}