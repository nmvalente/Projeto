package channels;

import interfaces.*;
import message.Message;
import protocols.Peer;
import utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.Random;

import files.*; 

public class SendDataChannel extends Thread{

	private InetAddress[] address;
	private MulticastSocket[] socket;
	private Peer peer;

	public SendDataChannel(InetAddress[] address, MulticastSocket[] socket, Peer peer){

		this.address   = new InetAddress[3];
		this.socket    = new MulticastSocket[3];

		this.address[Utils.MC]  = address[Utils.MC];
		this.address[Utils.MDB] = address[Utils.MDB];
		this.address[Utils.MDR] = address[Utils.MDR];
		this.socket[Utils.MC]   = socket[Utils.MC];
		this.socket[Utils.MDB]  = socket[Utils.MDB];
		this.socket[Utils.MDR]  = socket[Utils.MDR];
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
			Main.windows.printlnSendChannel("Error in starter sender thread");
		}
		try{
			DatagramPacket dg;
			Message unseenMessage;

			String reply,request;
			int group = Utils.MC;
			Random random = new Random();
			do{
				try{Thread.sleep(10);}catch(InterruptedException e){e.getMessage();System.err.println("Error in sleep of thread");}
				while(peer.getInbox().hasUnseenMessages()){
					try{Thread.sleep(10);}catch(InterruptedException e){}

					unseenMessage = peer.getInbox().getOneUnseenMessage();
					peer.getInbox().setSeen();
					request = null;
					reply = null;
					if(unseenMessage.hasRequest()){
						switch(Utils.convertBytetoString(unseenMessage.getHeader().getMessageType())){
						case "PUTCHUNK":
							group = Utils.MDB;
							break;
						case "GETCHUNK":
						case "DELETE":
						case "REMOVED":
							group = Utils.MC;
							break;
						}
						request = unseenMessage.sendMessage();
						if (request != null){
							dg = new DatagramPacket( request.getBytes() , request.length() , address[group] , socket[group].getLocalPort() );
							socket[group].send(dg);
							try{
								Main.windows.printlnSendChannel( getCurrentTime() + " - REQUEST SENT - " + unseenMessage.getHeader().getHeaderBuild());
							}
							catch (ArithmeticException ex){
								Main.windows.printlnSendChannel("Error in request sender thread");
							}
						}
					}
					else{
						switch(Utils.convertBytetoString(unseenMessage.getHeader().getMessageType())){
						case "PUTCHUNK": // responde com STORED
							if(peer.getPeerId() != Utils.convertBytetoInt(unseenMessage.getHeader().getSenderId())){
								peer.getChunks().add(unseenMessage);
								reply = unseenMessage.sendAnswer(peer);
								group = Utils.MC;
							}
							break;
						case "GETCHUNK": // responde com CHUNK
							if(peer.getPeerId() != Utils.convertBytetoInt(unseenMessage.getHeader().getSenderId())){
								ChunkFile c  = peer.getChunks().findOne( unseenMessage.getAddress() , Utils.convertBytetoString(unseenMessage.getHeader().getFileId()) , Utils.convertBytetoInt(unseenMessage.getHeader().getChunkNo()));
								if(c != null){
									String content;
									content = new String(peer.getChunks().file(unseenMessage.getAddress(), c), "US-ASCII");
									reply = unseenMessage.sendAnswer(peer) + content;
									group = Utils.MDR;
								}
							}
							break;
						case "DELETE": // apaga o ficheiro
							peer.getChunks().removeAll(unseenMessage.getAddress(), Utils.convertBytetoString(unseenMessage.getHeader().getFileId()));
							break;
						case "REMOVED": // actualiza dados do Backup e replicationDeg
							peer.getFiles().stored(unseenMessage.getAddress(), Utils.convertBytetoString(unseenMessage.getHeader().getFileId()), Utils.convertBytetoInt(unseenMessage.getHeader().getChunkNo()), "remove");
							break;
						case "CHUNK": // guarda o chunk para o Restauro
							if(peer.restoreFile != null)
								peer.restoreFile.deliveryChunk(unseenMessage);
							break;
						case "STORED": // actualiza dados do Backup
							peer.getFiles().stored(unseenMessage.getAddress(), Utils.convertBytetoString(unseenMessage.getHeader().getFileId()), Utils.convertBytetoInt(unseenMessage.getHeader().getChunkNo()), "add");
							break;
						}
						if(reply != null){
							dg = new DatagramPacket( reply.getBytes() , reply.length() , address[group] , socket[group].getLocalPort() );
							try{
								sleep(random.nextInt(Utils.BOUND_RANDOM));
							}
							catch(InterruptedException e){e.getMessage();System.err.println("Error doing random integer");}
							socket[group].send(dg);
							try{
								Main.windows.printlnSendChannel( getCurrentTime() + " -   REPLY SENT - " + unseenMessage.sendAnswer(peer) );
							}
							catch (ArithmeticException ex){
								Main.windows.printlnSendChannel("Error in reply sender thread");
							}
						}
					} 
				}
			}while(true);
		}
		catch(IOException e){Main.windows.printlnSendChannel(getCurrentTime() + " - Connection terminated");}
	}
}