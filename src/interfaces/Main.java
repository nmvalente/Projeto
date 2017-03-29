package interfaces;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import javax.swing.SwingUtilities;

import protocols.Peer;
import utils.Utils;

public class Main
{
	public static boolean exitNow = false;
	public static GUI windows;

	public static void main(String[] args) throws UnknownHostException{

		Utils util = new Utils();
		Peer peer = new Peer( InetAddress.getLocalHost().getHostAddress() );
	
		
		// Analisa os argumentos
		if(util.analyseArgs(args) == -1)
			return;

		MulticastSocket[] multicastSockets = new MulticastSocket[3];
		InetAddress[] group = new InetAddress[3];
		
		
		// Inicia os Sockets
		try {
			util.initSockets(args, args[0], args[1], multicastSockets, group);
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
		util.initThreads(peer, multicastSockets, group);

		
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
		peer.files.getAllFilesFromStorage();


		// Ciclo principal menu
		util.mainLoop(peer);

		System.out.println("Turning off...");
		
		
		// Fecha os sockets
		try {
			util.closeSockets(multicastSockets, group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Sai do programa
		System.exit(0);
		
	}
}
