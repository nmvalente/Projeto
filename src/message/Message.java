package message;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Message
{
	public static final String CRLFCRLF = "\r\n\r\n";
	public static final int SHA256LENGTH = 64;

	private String address = "";
	private int port = 0;
	private boolean request = false;

	public Header header;
	public Body body;

	public Message(String address, int port, String s){
		
		if (!(isAddress(address) && isPort(port)))
			throw new IllegalArgumentException("Invalid Address and/or Port");

		this.address = address;
		this.port = port;

		String[] tokens = s.split("\\s");

		if (tokens.length < 4)
			throw new IllegalArgumentException("Invalid Message Size");

		switch (tokens[0])
		{
			case "PUTCHUNK":
				header = new Header(
						tokens[0],
						tokens[1],
						Integer.parseInt(tokens[2]),
						tokens[3],
						Integer.parseInt(tokens[4]),
						Integer.parseInt(tokens[5])
				);

				body = new Body(tokens,6,tokens.length);
				break;

			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
				header = new Header(
						tokens[0],
						tokens[1],
						Integer.parseInt(tokens[2]),
						tokens[3],
						Integer.parseInt(tokens[4])
				);
				body = new Body();
				break;

			case "CHUNK":
				header = new Header(
						tokens[0],
						tokens[1],
						Integer.parseInt(tokens[2]),
						tokens[3],
						Integer.parseInt(tokens[4])
				);
				body = new Body(tokens,5,tokens.length);
				break;

			case "DELETE":
				header = new Header(
						tokens[0],
						tokens[1],
						Integer.parseInt(tokens[2]),				
						tokens[3]

				);
				body = new Body();
				break;
		}

	}
	
	public Message(String msgtype, String version, int senderId, String fileId, int chunkNo, int repl, String msg)
	{
		try
		{
			request = true;
			header = new Header(msgtype, version, senderId, fileId, chunkNo, repl);
			body = new Body(msg);
		}
		catch (IllegalArgumentException e)
		{
			e.getMessage();
		}
	}

	public String getAddress() {return this.address;}

	public int getPort() {return this.port;}
	
	public Header getHeader(){ return this.header;}

	public boolean isRequest() {return request;}

	@Override
	public String toString(){
		return "Message{\n" +
				header.toString() + '\n' +
				body.toString() + '\n' +
				'}';
	}

	private boolean isAddress(String ip){
		Pattern regex = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		Matcher matcher = regex.matcher(ip);
		return matcher.matches();
	}

	private boolean isPort(int p){
		return (p>=1 && p<=49151);
	}

	public boolean isNumber(String s){
		try{
			Integer.parseInt(s);
		}
		catch(NumberFormatException e){
			return false;
		}
		return true;
	}

	public String build(){
		String ss = null;

		switch (header.getMessageType())
		{
			case "PUTCHUNK":
				ss = header.getMessageType() + " " + header.getVersion()+ " " + header.getSenderId() + " " + header.getFileId() + " " + header.getChunkNo() + " " + header.getReplicationDeg() + " " + CRLFCRLF + body.getMessage();
				break;

			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
				ss = header.getMessageType() + " " + header.getVersion() + " " + header.getSenderId() + " " + header.getFileId() + " " + header.getChunkNo() + " " + CRLFCRLF;
				break;

			case "CHUNK":
				ss = header.getMessageType() + " " + header.getVersion() + " " + header.getSenderId() + " " + header.getFileId() + " " + header.getChunkNo() + " " + CRLFCRLF + body.getMessage();
				break;

			case "DELETE":
				ss = header.getMessageType() + " " + header.getVersion() + " " + header.getSenderId() + " " + header.getFileId() + " " + CRLFCRLF;
				break;
		}

		return ss;
	}

	public String reply(){
		String ss = null;

		switch (header.getMessageType())
		{
			case "PUTCHUNK":
				ss = "STORED" + " " + header.getVersion() + " " + header.getSenderId() + " " + header.getFileId() + " " + header.getChunkNo() + " " + CRLFCRLF;
				break;

			case "GETCHUNK":
				ss = "CHUNK" + " " + header.getVersion() + " " + header.getSenderId() + " " + header.getFileId() + " " + header.getChunkNo() + " " + CRLFCRLF;
				break;
		}

		return ss;
	}

}

