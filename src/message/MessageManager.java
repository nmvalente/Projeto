package message;

import java.util.ArrayList;

public class MessageManager{

	private static final int MAX_LENGTH_SEEN_MESSAGES = 10;
	private ArrayList<Message> unseenMessages;
	private ArrayList<Message> seenMessages;

	public MessageManager(){

		seenMessages   = new ArrayList<Message>();
		unseenMessages = new ArrayList<Message>();
	}

	public boolean hasUnseenMessages() {return unseenMessages.size() > 0;}

	public Message getOneUnseenMessage(){

		if (unseenMessages.size() > 0)
			return unseenMessages.get(0);
		return null;
	}

	public void setSeen(){
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
			message = new Message(address.substring(1),port,s); // to remove the / before ipNumber
			unseenMessages.add(message);
			return message.getHeader().getHeaderBuild();
		}
		catch (IllegalArgumentException e){
			e.getMessage();
		}

		return "Error in newMessage";
	}

	public Message buildMessage(String msgtype, String version, int senderId, String fileId, int chunkNo, int replicationDeg, String msg){

		Message message = null;
		try{
			message = new Message(msgtype, version, senderId, fileId, chunkNo, replicationDeg, msg);
			unseenMessages.add(message);
			return message;}catch(IllegalArgumentException e){
				e.getMessage(); System.err.println("error in build message");
			}
		return message;
	}
}
