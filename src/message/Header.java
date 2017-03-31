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
		if (!(isMessageType(messageType)))
			throw new IllegalArgumentException("Invalid Head Arguments");
		this.messageType = messageType;
		this.version = version;
		this.senderId = senderId;
		this.fileId = fileId;
	}

	// getchunk, remove, stored e chunk mas sem body
	public Header(String messageType, String version, int senderId, String fileId, int chunkNo){
		if (!(isMessageType(messageType)))
			throw new IllegalArgumentException("Invalid Head Arguments");
		this.messageType = messageType;
		this.version = version;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}

	// putchunk
	public Header(String messageType, String version, int senderId, String fileId, int chunkNo, int replicationDeg){
		if (!(isMessageType(messageType) && isReplicationDeg(replicationDeg)))
			throw new IllegalArgumentException("Invalid Head Arguments");

		this.messageType = messageType;
		this.version = version;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.replicationDeg = replicationDeg;
	}

	public String getMessageType(){return messageType;}

	public String getVersion(){return version;}

	public String getFileId(){return fileId;}
	
	public int getSenderId(){return senderId;}

	public int getChunkNo(){return chunkNo;}

	public int getReplicationDeg(){return replicationDeg;}

	public String printHeader(){
			
		String build = this.messageType + " , " + version + " , " + senderId + " , " + fileId;
		switch (this.messageType){
			case "PUTCHUNK":
				return 	build + " , " + this.chunkNo + " , " + this.replicationDeg;
			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
			case "CHUNK":
				return build + " , " + this.chunkNo;
			case "DELETE":
				return 	build;
			default: return "Invalid message type.";
		}
	}

	private boolean isMessageType(String messageType2){
	    for (messageTypes enumTypes : messageTypes.values())
	        if (enumTypes.name().equals(messageType2))
	            return true;
	    return false;
	}

	private boolean isReplicationDeg(int replicationDeg2){return (replicationDeg2>=0 && replicationDeg2<=9);}
}
