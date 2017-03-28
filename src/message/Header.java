package message;
public class Header
{
	private String messageType;
	private String version; 			    
	private int senderId;			    
	private String fileId; 				    
	private int chunkNo; 				    
	private int replicationDeg;		
	public String headerEnd = "\r\n\r\n";
	public enum messageTypes{PUTCHUNK,STORED,GETCHUNK,CHUNK,DELETE,REMOVED};
	
	// delete
	public Header(String messageType, String version, int senderId, String fileId){
		if (!(isMessageType(messageType) && isVersion(version)))
			throw new IllegalArgumentException("Invalid Head Arguments");
		this.messageType = messageType;
		this.version = "1.0";
		this.senderId = senderId;
		this.fileId = fileId;
	}

	// getchunk, remove, stored 			e chunk mas sem body
	public Header(String messageType, String version, int senderId, String fileId, int chunkNo){
		if (!(isMessageType(messageType) && isVersion(version)))
			throw new IllegalArgumentException("Invalid Head Arguments");
		this.messageType = messageType;
		this.version = "1.0";
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}

	// putchunk
	public Header(String messageType, String version, int senderId, String fileId, int chunkNo, int replicationDeg){
		if (!(isMessageType(messageType) && isVersion(version) && isReplicationDeg(replicationDeg)))
			throw new IllegalArgumentException("Invalid Head Arguments");

		this.messageType = messageType;
		this.version = "1.0";
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.replicationDeg = replicationDeg;
	}

	public String getMessageType(){
		return messageType;
	}

	public String getVersion(){
		return version;
	}

	public String getFileId(){
		return fileId;
	}
	
	public int getSenderId(){
		return senderId;
	}

	public int getChunkNo(){
		return chunkNo;
	}

	public int getReplicationDeg(){
		return replicationDeg;
	}

	@Override
	public String toString(){
		return "Head{" +
				"messageType='" + messageType + '\'' +
				", replicationDeg=" + replicationDeg + '\'' +
				", version='" + version + '\'' +
				", senderId='" + senderId + '\'' +
				", fileId='" + fileId + '\'' +
				", chunkNo=" + chunkNo +
				'}';
	}

	public String printHeader(){
		switch (messageType){
			case "PUTCHUNK":
				return 	messageType + " , " +
						version + " , " +
						senderId + " , " +
						fileId + " , " +
						chunkNo + " , " +
						replicationDeg ;

			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
			case "CHUNK":
				return 	messageType + " , " +
						version + " , " +
						senderId + " , " +
						fileId + " , " +
						chunkNo ;

			case "DELETE":
				return 	messageType + " , " +
						version + " , " +
						senderId + " , " +
						fileId ;

			default: return "Invalid message type.";
		}
	}

	private boolean isMessageType(String msgtype){

	    for (messageTypes enumTypes : messageTypes.values()) {
	        if (enumTypes.name().equals(msgtype)) {
	            return true;
	        }
	    }
	    return false;
	}

	private boolean isVersion(String vers){
		return ( vers.equals("1.0") );
	}

	private boolean isReplicationDeg(int repl){
		return (repl>=0 && repl<=9);
	}
}
