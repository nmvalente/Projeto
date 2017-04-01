package message;

public class Message{
	
	private static final String CRLFCRLF = "\r\n\r\n";
	//public static final int SHA256LENGTH = 64;
	private String address = "";
	private int port = 0;
	private boolean request = false;
	protected Header header;
	protected Body body;

	public Message(String address, int port, String s){

		if (!(isValidIP(address) && isPort(port)))
			throw new IllegalArgumentException("Invalid Address and/or Port");

		this.address = address;
		this.port = port;

		String[] tokens = s.split("\\s");

		if (tokens.length < 4)
			throw new IllegalArgumentException("Invalid Message Size");

		switch (tokens[0]){
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

	/*@Override
	public String toString(){
		return "Message{\n" +
				header.toString() + '\n' +
				body.toString() + '\n' +
				'}';
	}
*/
	public boolean isValidIP(String ip) {
		try {
			if ( ip == null || ip.isEmpty() ) {
				return false;
			}
			String[] parts = ip.split( "\\." );
			if ( parts.length != 4 ) {
				return false;
			}
			for ( String s : parts ) {
				int i = Integer.parseInt( s );
				if ( (i < 0) || (i > 255) ) {
					return false;
				}
			}
			if ( ip.endsWith(".") ) {
				return false;
			}
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	private boolean isPort(int p){return (p >= 1 && p <= 49151);}

	/*public boolean isNumber(String s){
		try { 
			Integer.parseInt(s); 
		}catch(NumberFormatException e) { 
			return false; 
		}
		return true;
	}*/

	public String makeMessage(){
		String build = null;
		String ss = header.getMessageType() + " " + header.getVersion()+ " " + header.getSenderId() + " " + header.getFileId();

		switch (header.getMessageType()){
		case "PUTCHUNK": 
			build = ss + " " + header.getChunkNo() + " " + header.getReplicationDeg() + " " + CRLFCRLF + body.getMessage();
			break;
		case "STORED":
		case "GETCHUNK":
		case "REMOVED":
			build = ss + " " + header.getChunkNo() + " " + CRLFCRLF;
			break;
		case "CHUNK":
			build = ss + header.getChunkNo() + " " + CRLFCRLF + body.getMessage();
			break;
		case "DELETE":
			build = ss+ " " + CRLFCRLF;
			break;
		}
		return build;
	}

	public String makeAnswer(){
		String build = null;
		String ss = " " + header.getVersion() + " " + header.getSenderId() + " " + header.getFileId() + " " + header.getChunkNo() + " " + CRLFCRLF;
		switch (header.getMessageType()){
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

