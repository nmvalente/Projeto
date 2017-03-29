package filefunc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

import channels.ReceiveDataChannel;
import channels.SendDataChannel;
import interfaces.Main;
import protocols.Peer;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
	}

	public void displayMenu(){

		System.out.println("\n**************************************************");
		System.out.println(" \t   Distributed Backup Service");
		System.out.println("**************************************************");
		System.out.println("");
		System.out.println("1 - Backup File");
		System.out.println("2 - Restore File");
		System.out.println("3 - Reclaim Space");
		System.out.println("4 - Delete File\n");
		System.out.println("0 - Quit\n");
		System.out.printf("Option [0-4] > ");
	}

	public void initSockets(String[] args, String address, String port, MulticastSocket[] multicastSockets,
			InetAddress[] group) throws UnknownHostException, IOException {
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
	}

	public void initThreads(Peer peer, MulticastSocket[] multicastSockets, InetAddress[] group) {
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
	}

	public void closeSockets(MulticastSocket[] multicastSockets, InetAddress[] group) throws IOException {
		// desliga o socket de MC
		multicastSockets[0].leaveGroup(group[0]);
		multicastSockets[0].close();

		// desliga o socket de MDB
		multicastSockets[1].leaveGroup(group[1]);
		multicastSockets[1].close();

		// desliga o socket de MDR
		multicastSockets[2].leaveGroup(group[2]);
		multicastSockets[2].close();
	}

	public int analyseArgs(String[] args) {
		if( args.length != 6 ){
			System.out.println("Invalid argument number!\n<program> <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>");
			return -1;
		}
		return 0;
	}

	public void mainLoop(Peer peer) {
		int option;
		Scanner in = new Scanner(System.in);

		do {

			displayMenu();
			option = in.nextInt();

			switch (option)
			{
			case 1 :
				if(peer.manageBackup() == -1)
					Main.exitNow = true;
				break;

			case 2 :
				if(peer.restore() == -1)
					Main.exitNow = true;
				break;

			case 3 :
				if(peer.reclaim() == -1)
					Main.exitNow = true;
				break;

			case 4 :
				if(peer.delete() == -1)
					Main.exitNow = true;
				break;

			case 0 :
				Main.exitNow = true;
				break;

			default:
				System.out.println("Invalid input!");
				break;
			}

		} while( !Main.exitNow );

		in.close();
	}
}
