package channels;

import interfaces.*;
import message.Message;
import protocols.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.Random;

import files.*; 

public class SendDataChannel extends Thread{

	public static final int MC  = 0;
	public static final int MDB = 1;
	public static final int MDR = 2;

	InetAddress[] address;
	MulticastSocket[] socket;
	Peer peer;

	public SendDataChannel(InetAddress[] address, MulticastSocket[] socket, Peer peer){

		this.address   = new InetAddress[3];
		this.socket    = new MulticastSocket[3];

		this.address[MC]  = address[MC];
		this.address[MDB] = address[MDB];
		this.address[MDR] = address[MDR];
		this.socket[MC]   = socket[MC];
		this.socket[MDB]  = socket[MDB];
		this.socket[MDR]  = socket[MDR];
		this.peer         = peer;
	}

	public String getCurrentTime(){

		Date date = new Date();
		return date.toString();
	}

	public void run(){

		try{
			Main.windows.printlnSendChannel( getCurrentTime() + " - Started sender thread" );
		}
		catch (ArithmeticException ex){
			Main.windows.printStackTraceSendChannel(ex);
		}

		try{
			DatagramPacket dg;
			Message unseenMessage;
			//InfoFile u;
			String reply,request;
			int group = MC, r = 0;
			Random random = new Random();

			do{

				try{Thread.sleep(10);}catch(InterruptedException e){}

				while(peer.inbox.hasUnseenMessages()){
					try{Thread.sleep(10);}catch(InterruptedException e){}

					unseenMessage = peer.inbox.getOneUnseenMessage();
					peer.inbox.setSeen();

					request = null;
					reply = null;

					if ( unseenMessage.isRequest() ){
						switch( unseenMessage.header.getMessageType() ){
						case "PUTCHUNK":
							request = unseenMessage.makeMessage();
							group = MDB;
							break;

						case "GETCHUNK":
							request = unseenMessage.makeMessage();
							group = MC;
							break;

						case "DELETE":
							request = unseenMessage.makeMessage();
							group = MC;
							break;

						case "REMOVED":
							request = unseenMessage.makeMessage();
							group = MC;
							break;
						}

						if (request != null){
							dg = new DatagramPacket( request.getBytes() , request.length() , address[group] , socket[group].getLocalPort() );
							socket[group].send(dg);

							try{
								Main.windows.printlnSendChannel( getCurrentTime() + " - REQUEST SENT - " + unseenMessage.getHeader().printHeader());
							}
							catch (ArithmeticException ex){
								Main.windows.printStackTraceSendChannel(ex);
							}
						}
					}
					else{
						switch( unseenMessage.header.getMessageType() ){
						case "PUTCHUNK": // responde com STORED
							peer.chunks.add( unseenMessage );
							reply = unseenMessage.makeAnswer();
							group = MC;
							break;

						case "GETCHUNK": // responde com CHUNK
							ChunkFile c  = peer.chunks.find( unseenMessage.getAddress() , unseenMessage.header.getFileId() , unseenMessage.header.getChunkNo() );
							if (c!=null){
								try {
									String content;
									content = new String(peer.chunks.file(unseenMessage.getAddress(), c), "UTF-8");
									reply = unseenMessage.makeAnswer() + content;
								} catch (IOException e){
									// remove referencia do chunk
									peer.chunks.remove(unseenMessage.getAddress(), unseenMessage.header.getFileId(), unseenMessage.header.getChunkNo());
								}
								group = MDR;
							}
							break;

						case "DELETE": // apaga o ficheiro
							peer.chunks.remove(unseenMessage.getAddress(), unseenMessage.header.getFileId());
							break;

						case "REMOVED": // actualiza dados do Backup e replicationDeg
							peer.files.removeSTORED(unseenMessage.getAddress(), unseenMessage.header.getFileId(), unseenMessage.header.getChunkNo());
							break;

						case "CHUNK": // guarda o chunk para o Restauro
							peer.restoreFile.add(unseenMessage);
							break;

						case "STORED": // actualiza dados do Backup
							peer.files.addSTORED(unseenMessage.getAddress(), unseenMessage.header.getFileId(), unseenMessage.header.getChunkNo());
							break;
						}

						if(reply != null){
							dg = new DatagramPacket( reply.getBytes() , reply.length() , address[group] , socket[group].getLocalPort() );
							try{
								r = random.nextInt(401);
								sleep(r);
							}
							catch(InterruptedException e){
								e.getMessage();
							}

							socket[group].send(dg);

							try{
								Main.windows.printlnSendChannel( getCurrentTime() + " -   REPLY SENT - " + unseenMessage.makeAnswer() );
							}
							catch (ArithmeticException ex){
								Main.windows.printStackTraceSendChannel(ex);
							}
						}
					} 
				}

			} while(true);
		}
		catch(IOException e) {
			Main.windows.printlnSendChannel(getCurrentTime() + " - Connection terminated");
		}
	}
}
