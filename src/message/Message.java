package message;

import utils.Utils;

public class Message{

	private String address;// = "";
	private int port = 0;
	private boolean request = false;
	protected Header header;
	protected Body body;

	public Message(String address, int port, String s){

		if (!(validateIP(address) && validatePort(port)))
			throw new IllegalArgumentException("Invalid Address and/or Port");

		this.address = address;
		this.port = port;

		String[] parts = s.split("\\s");

		if (parts.length < 4)
			throw new IllegalArgumentException("Invalid Message Size");

		switch (parts[0]){
		case "PUTCHUNK":
			header = new Header(
					parts[0],
					parts[1],
					Integer.parseInt(parts[2]),
					parts[3],
					Integer.parseInt(parts[4]),
					Integer.parseInt(parts[5])
					);
			body = new Body(parts,6,parts.length);
			break;
		case "DELETE":
			header = new Header(
					parts[0],
					parts[1],
					Integer.parseInt(parts[2]),				
					parts[3],-1,-1
					);
			body = new Body();
			break;
		case "STORED":
		case "GETCHUNK":
		case "REMOVED":
			header = new Header(
					parts[0],
					parts[1],
					Integer.parseInt(parts[2]),
					parts[3],
					Integer.parseInt(parts[4]),-1
					);
			body = new Body();
			break;
		case "CHUNK":
			header = new Header(
					parts[0],
					parts[1],
					Integer.parseInt(parts[2]),
					parts[3],
					Integer.parseInt(parts[4]),-1
					);
			body = new Body(parts,5,parts.length);
			break;
		
		}
	}

	public Message(String msgtype, String version, int senderId, String fileId, int chunkNo, int repl, String msg){
		try{
			request = true;
			header = new Header(msgtype, version, senderId, fileId, chunkNo, repl);
			body = new Body(msg);
		}
		catch (IllegalArgumentException e){e.getMessage();}
	}

	public String getAddress() {return this.address;}
	public int getPort() {return this.port;}
	public Header getHeader(){return this.header;}
	public Body getBody(){return this.body;}

	public boolean isRequest() {return request;}

	public boolean validateIP(String ip){

		if (ip == null || ip.isEmpty()){
			return false;
		}
		return true;
	}

	private boolean validatePort(int p){return (p >= 1 && p <= 49151);}

	public String sendMessage(){
		String build = null;
		String ss = Utils.convertBytetoString(header.getMessageType()) + " " + Utils.convertBytetoString(header.getVersion())+ " " + Utils.convertBytetoString(header.getSenderId()) + " " + Utils.convertBytetoString(header.getFileId());

		switch (Utils.convertBytetoString(header.getMessageType())){
		case "PUTCHUNK": 
			build = ss + " " + Utils.convertBytetoInt(header.getChunkNo()) + " " + Utils.convertBytetoInt(header.getReplicationDeg()) + " " + Utils.CRLFCRLF + Utils.convertBytetoString(body.getBody());
			break;
		case "STORED":
		case "GETCHUNK":
		case "REMOVED":
			build = ss + " " + Utils.convertBytetoInt(header.getChunkNo()) + " " + Utils.CRLFCRLF;
			break;
		case "CHUNK":
			build = ss + Utils.convertBytetoInt(header.getChunkNo()) + " " + Utils.CRLFCRLF + Utils.convertBytetoString(body.getBody());
			break; 
		case "DELETE":
			build = ss+ " " + Utils.CRLFCRLF;
			break;
		}
		return build;
	}

	public String sendAnswer(){
		String build = null;
		String ss = " " + Utils.convertBytetoString(header.getVersion()) + " " + Utils.convertBytetoString(header.getSenderId()) + " " + Utils.convertBytetoString(header.getFileId()) + " " + Utils.convertBytetoInt(header.getChunkNo()) + " " + Utils.CRLFCRLF;
		switch (Utils.convertBytetoString(header.getMessageType())){
		case "PUTCHUNK":
			build = "STORED" + ss; 
			break;
		case "GETCHUNK":
			build = "CHUNK" + ss; 
			break;
		}
		return build;
	}
}

