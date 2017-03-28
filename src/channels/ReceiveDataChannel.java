package channels;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import interfaces.Main;
import protocols.Peer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiveDataChannel extends Thread{

	public static final int MAXBUFFER = 1024;

	String name;
	MulticastSocket socket;
	Peer peer;

	public ReceiveDataChannel(String name, MulticastSocket s, Peer peer){

		this.name = name;
		this.socket = s;
		this.peer   = peer;
	}

	public String getCurrentTime(){

		Date date = new Date();
		return date.toString();
	}

	public void run(){

		try
		{
			if(name == "MC")
				Main.windows.printlnReceiverMC(getCurrentTime() + " - Started receiver thread :: "+ name);
			if(name == "MDB")
				Main.windows.printlnReceiverMDB(getCurrentTime() + " - Started receiver thread :: "+ name);
			if(name == "MDR")
				Main.windows.printlnReceiverMDR(getCurrentTime() + " - Started receiver thread :: "+ name);

		}
		catch (ArithmeticException ex)
		{
			if(name == "MC")
				Main.windows.printStackTraceReceiverMC(ex); 
			if(name == "MDB")
				Main.windows.printStackTraceReceiverMDB(ex);
			if(name == "MDR")
				Main.windows.printStackTraceReceiverMDR(ex);
		}

		try
		{
			byte[] buf;
			DatagramPacket dg;
			String dgString;
			String msg;// char

			do
			{
				try{Thread.sleep(10);}catch(InterruptedException e){}

				buf = new byte[ MAXBUFFER ];

				dg = new DatagramPacket( buf , buf.length );
				socket.receive(dg);
				dgString = new String( dg.getData() );

				if ( !dg.getAddress().toString().substring(1).equals(peer.getLocalhost()) )
				{
					msg = peer.inbox.newMessage(dg.getAddress().toString(), dg.getPort() , dgString );

					try
					{

						if(name == "MC")
							Main.windows.printlnReceiverMC(getCurrentTime() + " - RECEIVED - " + msg);
						if(name == "MDB")
							Main.windows.printlnReceiverMDB(getCurrentTime() + " - RECEIVED - " + msg);
						if(name == "MDR")
							Main.windows.printlnReceiverMDR(getCurrentTime() + " - RECEIVED - " + msg);
					}
					catch (ArithmeticException ex)
					{
						if(name == "MC")
							Main.windows.printStackTraceReceiverMC(ex); 
						if(name == "MDB")
							Main.windows.printStackTraceReceiverMDB(ex);
						if(name == "MDR")
							Main.windows.printStackTraceReceiverMDR(ex);
					}
				} 

				try
				{
					sleep(10);
				}
				catch(InterruptedException e)
				{
					e.getMessage();
				}
			} while(true);

		}
		catch(IOException n) 
		{
			if(name == "MC")
				Main.windows.printlnReceiverMC(getCurrentTime() + " - Connection terminated");
			if(name == "MDB")
				Main.windows.printlnReceiverMDB(getCurrentTime() + " - Connection terminated");
			if(name == "MDR")
				Main.windows.printlnReceiverMDR(getCurrentTime() + " - Connection terminated");
		}
	}
}
