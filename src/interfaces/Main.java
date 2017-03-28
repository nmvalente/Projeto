package interfaces;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import channels.*;
import filefunc.*;
import protocols.Peer;

public class Main
{
	public static boolean exitNow = false;
	public static OutputWindow windows;

	public static void main(String[] args) throws IOException, InterruptedException, InvocationTargetException
	{
		// "program mcAddress mcPort mdbAddress mdbPort mdrAddress mdrPort
		// 224.0.0.1 1110 224.0.0.2 1111 224.0.0.3 1112

		if( args.length != 6 )  // minimo de 7 argumentos
		{
			System.out.println("Invalid argument number!\n<program> <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>");
			return ;
		}

		FileManager menu = new FileManager();
		Peer peer = new Peer( InetAddress.getLocalHost().getHostAddress() );

		String 	address = null,
				port 	= null;

		MulticastSocket[] multicastSockets = new MulticastSocket[3];
		InetAddress[] group = new InetAddress[3];
		

		// Configuracao do socket para MC --------------------
		address = args[0];
		port    = args[1];

		group[0] = InetAddress.getByName( address );
		multicastSockets[0] = new MulticastSocket(Integer.parseInt( port ));
		multicastSockets[0].joinGroup(group[0]);

		// Configuracao do socket para MDB -------------------
		address = args[2];
		port    = args[3];

		group[1] = InetAddress.getByName( address );
		multicastSockets[1] = new MulticastSocket(Integer.parseInt( port ));
		multicastSockets[1].joinGroup(group[1]);

		// Configuracao do socket para MDR -------------------
		address = args[4];
		port    = args[5];

		group[2] = InetAddress.getByName( address );
		multicastSockets[2] = new MulticastSocket(Integer.parseInt( port ));
		multicastSockets[2].joinGroup(group[2]);

		windows = new OutputWindow();

		
		// configuracao de Threads
		
		ReceiveDataChannel MCchannel   = new ReceiveDataChannel("MC" ,multicastSockets[0],peer);
		ReceiveDataChannel MDBchannel  = new ReceiveDataChannel("MDB",multicastSockets[1],peer);
		ReceiveDataChannel MDRchannel  = new ReceiveDataChannel("MDR",multicastSockets[2],peer);
		SendDataChannel   SENDchannel = new SendDataChannel  (group,multicastSockets,peer);

		// Inicio de threads
		
		MCchannel.start();
		MDBchannel.start();
		MDRchannel.start();
		SENDchannel.start();

		SwingUtilities.invokeAndWait(windows);


		Scanner in = new Scanner(System.in);
		int option;

		peer.files.getAllFilesFromStorage();


		// Ciclo de menu
		do {

			menu.menu();
			option = in.nextInt();

			switch (option)
			{
			case 1 :
				if(peer.backup() == -1)
					exitNow = true;
				break;

			case 2 :
				if(peer.restore() == -1)
					exitNow = true;
				break;

			case 3 :
				if(peer.delete() == -1)
					exitNow = true;
				break;

			case 0 :
				exitNow = true;
				break;

			default:
				System.out.println("Invalid input!");
				break;
			}

		} while( !exitNow );


		System.out.println("Turning off...");
		
		// desliga o socket de MC
		multicastSockets[0].leaveGroup(group[0]);
		multicastSockets[0].close();

		// desliga o socket de MDB
		multicastSockets[1].leaveGroup(group[1]);
		multicastSockets[1].close();

		// desliga o socket de MDR
		multicastSockets[2].leaveGroup(group[2]);
		multicastSockets[2].close();
		
		System.exit(0);
	}


}
