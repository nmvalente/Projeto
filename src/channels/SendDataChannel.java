package channels;

import filefunc.*;
import interfaces.*;
import message.Message;
import protocols.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.Random; 

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

		try
		{
			Main.windows.printlnSendChannel( getCurrentTime() + " - Started sender thread" );
		}
		catch (ArithmeticException ex)
		{
			Main.windows.printStackTraceSendChannel(ex);
		}

		try {

			DatagramPacket dg;
			Message m;
			Ufile u;
			String reply,request;
			int group = MC, r=0;
			Random random = new Random();

			do {

				try{Thread.sleep(10);}catch(InterruptedException e){}

				// Message handler
				while( peer.inbox.hasNewMessage() )
				{
					try{Thread.sleep(10);}catch(InterruptedException e){}

					m = peer.inbox.getNewMessage();
					peer.inbox.setAsRead();

					request = null;
					reply = null;

					if ( m.isRequest() )
					{
						switch( m.header.getMessageType() )
						{
						case "PUTCHUNK":
							request = m.build();
							group = MDB;
							break;

						case "GETCHUNK":
							request = m.build();
							group = MC;
							break;

						case "DELETE":
							request = m.build();
							group = MC;
							break;

						case "REMOVED":
							request = m.build();
							group = MC;
							break;
						}

						if ( request != null ) // envia um pedido
						{
							dg = new DatagramPacket( request.getBytes() , request.length() , address[group] , socket[group].getLocalPort() );
							socket[group].send(dg);

							try
							{
								Main.windows.printlnSendChannel( getCurrentTime() + " - REQUEST SENT - " + m.getHeader().printHeader());
							}
							catch (ArithmeticException ex)
							{
								Main.windows.printStackTraceSendChannel(ex);
							}
						}
					}
					else
					{
						switch( m.header.getMessageType() )
						{
						case "PUTCHUNK": // responde com STORED
							peer.chunks.add( m );
							reply = m.reply();
							group = MC;
							break;

						case "GETCHUNK": // responde com CHUNK
							Chunk c  = peer.chunks.find( m.getAddress() , m.header.getFileId() , m.header.getChunkNo() );
							if (c!=null)
							{
								try {
									String content;
									content = new String(peer.chunks.file(m.getAddress(), c), "UTF-8");
									reply = m.reply() + content;
								} catch (IOException e) // nao existe o ficheiro chunk
								{
									// remove referencia do chunk
									peer.chunks.remove(m.getAddress(), m.header.getFileId(), m.header.getChunkNo());
								}
								group = MDR;
							}
							break;

						case "DELETE": // apaga o ficheiro
							peer.chunks.remove(m.getAddress(), m.header.getFileId());
							break;

						case "REMOVED": // actualiza dados do Backup e replicationDeg
							peer.files.removeSTORED(m.getAddress(), m.header.getFileId(), m.header.getChunkNo());
							break;

						case "CHUNK": // guarda o chunk para o Restauro
							peer.restoreFile.add(m);
							break;

						case "STORED": // actualiza dados do Backup
							peer.files.addSTORED(m.getAddress(), m.header.getFileId(), m.header.getChunkNo());
							break;
						}

						if ( reply != null ) // envia resposta
						{
							dg = new DatagramPacket( reply.getBytes() , reply.length() , address[group] , socket[group].getLocalPort() );

							try
							{
								r = random.nextInt(401);
								sleep(r);
							}
							catch(InterruptedException e)
							{
								e.getMessage();
							}

							socket[group].send(dg);

							try
							{
								Main.windows.printlnSendChannel( getCurrentTime() + " -   REPLY SENT - " + m.reply() );
							}
							catch (ArithmeticException ex)
							{
								Main.windows.printStackTraceSendChannel(ex);
							}
						}
					} 
				}

			} while (true);
		}
		catch(IOException e) 
		{
			Main.windows.printlnSendChannel(getCurrentTime() + " - Connection terminated");
		}
	}
}
