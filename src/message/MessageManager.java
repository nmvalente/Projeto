package message;

import java.util.ArrayList;
import java.util.Iterator;

public class MessageManager{

	private static final int MAX_LENGTH_SEEN_MESSAGES = 10;
	private ArrayList<Message> unseenMessages;
	private ArrayList<Message> seenMessages;

	public MessageManager(){

		seenMessages   = new ArrayList<Message>();
		unseenMessages = new ArrayList<Message>();
	}

	public boolean hasUnseenMessages() { return unseenMessages.size() > 0; }

	public Message getOneUnseenMessage(){

		if (unseenMessages.size() > 0)
			return unseenMessages.get(0);

		return null;
	}

	public void setAsRead(){

		if (unseenMessages.size() > 0){

			seenMessages.add( unseenMessages.get(0));
			unseenMessages.remove(0);

			if ( seenMessages.size() > MAX_LENGTH_SEEN_MESSAGES )
				seenMessages.remove(0);
		}
	}

	public String addToUnseenMessages(String address, int port, String s){

		Message message = null;

		try	{

			message = new Message(address.substring(1),port,s);

			unseenMessages.add(message);

			return message.getHeader().printHeader();
		}
		catch (IllegalArgumentException e){
			e.getMessage();
		}

		return "Error in newMessage";
	}

	public String query(String msgtype, String version, int senderId, String fileId, int chunkNo, int replicationDeg, String msg){
		
		Message message = null;

		try{
			message = new Message(msgtype, version, senderId, fileId, chunkNo, replicationDeg, msg);

			unseenMessages.add(message);

			return message.getHeader().printHeader();
		}catch (IllegalArgumentException e){
			e.getMessage();
		}

		return "Error in newRequest";
	}

	public void list(int c){
		
		Message aux;
		int i = 0;

		System.out.println("\n List of Messages " + ((c==0)?"Unseen":"Seen"));
		System.out.println("\n**************************************************");

		for (Iterator<Message> it = ((c==0)?unseenMessages:seenMessages).iterator(); it.hasNext();){
			aux = it.next();
			i++;
			System.out.printf("%2d ~ %s\n", i, aux.getHeader().printHeader());
		}

		System.out.println("\n**************************************************");
		System.out.printf( " Listed %d message%s.\n", i, ((i==1)?"":"s"));
	}
}
