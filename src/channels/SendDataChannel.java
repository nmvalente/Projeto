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
			//InfoFile u;
			String reply,request;
			int group = Utils.MC, r = 0;
			Random random = new Random();
			do{
				try{Thread.sleep(10);}catch(InterruptedException e){e.getMessage();System.err.println("Error in sleep of thread");}
				while(peer.getInbox().hasUnseenMessages()){
					try{Thread.sleep(10);}catch(InterruptedException e){}
					unseenMessage = peer.getInbox().getOneUnseenMessage();
					peer.getInbox().setSeen();
					request = null;
					reply = null;
					if(unseenMessage.isRequest()){
						switch(Utils.convertBytetoString(unseenMessage.getHeader().getMessageType())){
						case "PUTCHUNK":
							request = unseenMessage.sendMessage();
							group = Utils.MDB;
							break;
						case "GETCHUNK":
							request = unseenMessage.sendMessage();
							group = Utils.MC;
							break;
						case "DELETE":
							request = unseenMessage.sendMessage();
							group = Utils.MC;
							break;
						case "REMOVED":
							request = unseenMessage.sendMessage();
							group = Utils.MC;
							break;
						}
						if (request != null){
							//System.out.println(request);
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
							peer.getChunks().add(unseenMessage);
							reply = unseenMessage.sendAnswer();
							group = Utils.MC;
							break;
						case "GETCHUNK": // responde com CHUNK
							ChunkFile c  = peer.getChunks().find( unseenMessage.getAddress() , Utils.convertBytetoString(unseenMessage.getHeader().getFileId()) , Utils.convertBytetoInt(unseenMessage.getHeader().getChunkNo()));
							if(c != null){
								try{
									String content;
									content = new String(peer.getChunks().file(unseenMessage.getAddress(), c), "UTF-8");
									reply = unseenMessage.sendAnswer() + content;
								}catch(IOException e){
									// remove referencia do chunk
									peer.getChunks().remove(unseenMessage.getAddress(), Utils.convertBytetoString(unseenMessage.getHeader().getFileId()), Utils.convertBytetoInt(unseenMessage.getHeader().getChunkNo()));
								}
								group = Utils.MDR;
							}
							break;
						case "DELETE": // apaga o ficheiro
							peer.getChunks().remove(unseenMessage.getAddress(), Utils.convertBytetoString(unseenMessage.getHeader().getFileId()));
							break;
						case "REMOVED": // actualiza dados do Backup e replicationDeg
							peer.getFiles().removeSTORED(unseenMessage.getAddress(), Utils.convertBytetoString(unseenMessage.getHeader().getFileId()), Utils.convertBytetoInt(unseenMessage.getHeader().getChunkNo()));
							break;
						case "CHUNK": // guarda o chunk para o Restauro
							peer.restoreFile.add(unseenMessage);
							break;
						case "STORED": // actualiza dados do Backup
							peer.getFiles().addSTORED(unseenMessage.getAddress(), Utils.convertBytetoString(unseenMessage.getHeader().getFileId()), Utils.convertBytetoInt(unseenMessage.getHeader().getChunkNo()));
							break;
						}
						if(reply != null){
							dg = new DatagramPacket( reply.getBytes() , reply.length() , address[group] , socket[group].getLocalPort() );
							try{
								r = random.nextInt(401);
								sleep(r);
							}
							catch(InterruptedException e){e.getMessage();System.err.println("Error doing random integer");}
							socket[group].send(dg);
							try{
								Main.windows.printlnSendChannel( getCurrentTime() + " -   REPLY SENT - " + unseenMessage.sendAnswer() );
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