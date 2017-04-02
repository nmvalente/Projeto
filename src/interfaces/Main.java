package interfaces;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import channels.ReceiveDataChannel;
import channels.SendDataChannel;
import protocols.Peer;
import utils.Utils;

public class Main{

	public static boolean exitNow = false;
	public static GUI windows;

	public static void main(String[] args) throws IOException{

		if(analyseArgs(args) == -1)
			return;
		
		MulticastSocket[] multicastSockets = null;
		InetAddress[] group = null;

		init_program(args);

		finish_program(multicastSockets, group);


	}

	private static void finish_program(MulticastSocket[] multicastSockets, InetAddress[] group) {
		System.out.println("Turning off...");

		// Fecha os sockets
		try {
			closeSockets(multicastSockets, group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Sai do programa
		System.exit(0);
	}

	private static void init_program(String[] args) throws IOException {
		Peer peer = new Peer( InetAddress.getLocalHost().getHostAddress() );
		MulticastSocket[] multicastSockets = new MulticastSocket[3];
		InetAddress[] group = new InetAddress[3];


		// Inicia os Sockets
		try {
			initSockets(args, args[0], args[1], multicastSockets, group);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Cria a janela grafica
		windows = new GUI();


		// Inicia os threads
		initThreads(peer, multicastSockets, group);


		// Trata do display dos varios threads na janela grafica
		try {
			SwingUtilities.invokeAndWait(windows);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Obtem lista de todos os ficheiros da pasta storage
		peer.getFiles().getAllFilesFromStorage();

		// Ciclo principal menu
		mainLoop(peer);
	}

	public static void displayMenu(){

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

	public static void initSockets(String[] args, String address, String port, MulticastSocket[] multicastSockets,	InetAddress[] group) throws UnknownHostException, IOException {
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

	public static void initThreads(Peer peer, MulticastSocket[] multicastSockets, InetAddress[] group) {
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

	public static void closeSockets(MulticastSocket[] multicastSockets, InetAddress[] group) throws IOException {
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

	public static int analyseArgs(String[] args) {
		if( args.length != 6 ){
			System.out.println("Invalid argument number!\n<program> <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>");
			return -1;
		}
		return 0;
	}

	public static void mainLoop(Peer peer) throws IOException {
		int option;
		Scanner in = new Scanner(System.in);

		do {

			displayMenu();
			option = in.nextInt();

			switch (option)
			{
			case 1 :
				if(peer.genericSubProtocol(1) == -1)
					Main.exitNow = true;
				break;

			case 2 :
				if(peer.genericSubProtocol(2) == -1)
					Main.exitNow = true;
				break;

			case 3 :
				if(peer.genericSubProtocol(3) == -1)
					Main.exitNow = true;
				break;

			case 4 :
				if(peer.genericSubProtocol(4) == -1)
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
