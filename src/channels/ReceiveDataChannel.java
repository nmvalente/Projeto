package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import interfaces.Main;
import protocols.Peer;
import java.util.Date;

public class ReceiveDataChannel extends Thread{

	protected static final int BUFFER_SIZE = 1024;
	private String name;
	private MulticastSocket socket;
	private Peer peer;

	public ReceiveDataChannel(String name, MulticastSocket ms, Peer peer){
		this.name = name;
		this.socket = ms;
		this.peer   = peer;
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
			if(name == "MC")
				Main.windows.printlnReceiverMC(getCurrentTime() + " - Error in starter Receiver MC"); 
			if(name == "MDB")
				Main.windows.printlnReceiverMC(getCurrentTime() + " - Error in starter Receiver MDB"); 
			if(name == "MDR")
				Main.windows.printlnReceiverMC(getCurrentTime() + " - Error in starter Receiver MDR"); 
		}
		try{
			byte[] buf;
			DatagramPacket dg;
			String dgString;
			String message;
			do{
				try{Thread.sleep(10);}catch(InterruptedException e){e.getMessage(); System.err.println("Error in sleep");}
				buf = new byte[BUFFER_SIZE];

				dg = new DatagramPacket( buf , buf.length );
				socket.receive(dg);
				dgString = new String( dg.getData() );

				if ( !dg.getAddress().toString().substring(1).equals(peer.getLocalhost()) ){
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
							Main.windows.printlnReceiverMC(getCurrentTime() + " - Error in  Receiver MDB"); 
						if(name == "MDR")
							Main.windows.printlnReceiverMC(getCurrentTime() + " - Error in  Receiver MDR");
					}
				} 
				try{Thread.sleep(10);}catch(InterruptedException e){e.getMessage();System.err.println("Error in sleep");}
			} while(true);
		}
		catch(IOException n){
			if(name == "MC")
				Main.windows.printlnReceiverMC(getCurrentTime() + " - Connection terminated");
			if(name == "MDB")
				Main.windows.printlnReceiverMDB(getCurrentTime() + " - Connection terminated");
			if(name == "MDR")
				Main.windows.printlnReceiverMDR(getCurrentTime() + " - Connection terminated");
		}
	}
}