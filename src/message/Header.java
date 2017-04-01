package message;

import utils.Utils;

public class Header{
	
	private byte[] messageType;
	private byte[] version;
	private byte[] senderId;
	private byte[] fileId; 				    
	private byte[] chunkNo;
	private byte[] replicationDeg;
	//private String headerEnd = "\r\n\r\n";
	private enum messageTypes{PUTCHUNK,STORED,GETCHUNK,CHUNK,DELETE,REMOVED};
	
	// delete
	public Header(String messageType, String version, int senderId, String fileId){
		if (!(isMessageType(messageType)))
			throw new IllegalArgumentException("Invalid Head Arguments");
		this.messageType = messageType.getBytes();
		this.version = version.getBytes();
		this.senderId = Utils.convertInttoByte(senderId);
		this.fileId = fileId.getBytes();
	}

	// getchunk, remove, stored e chunk mas sem body
	public Header(String messageType, String version, int senderId, String fileId, int chunkNo){
		if (!(isMessageType(messageType)))
			throw new IllegalArgumentException("Invalid Head Arguments");
		this.messageType = messageType.getBytes();
		this.version = version.getBytes();
		this.senderId = Utils.convertInttoByte(senderId);
		this.fileId = fileId.getBytes();
		this.chunkNo = Utils.convertInttoByte(chunkNo);
	}

	// putchunk
	public Header(String messageType, String version, int senderId, String fileId, int chunkNo, int replicationDeg){
		if (!(isMessageType(messageType) && isReplicationDeg(replicationDeg)))
			throw new IllegalArgumentException("Invalid Head Arguments");

		this.messageType = messageType.getBytes();
		this.version = version.getBytes();
		this.senderId = Utils.convertInttoByte(senderId);
		this.fileId = fileId.getBytes();
		this.chunkNo = Utils.convertInttoByte(chunkNo);
		this.replicationDeg = Utils.convertInttoByte(replicationDeg);
	}

	public byte[] getMessageType(){return messageType;}

	protected byte[] getVersion(){return version;}

	public byte[] getFileId(){return fileId;}
	
	public byte[] getSenderId(){return senderId;}

	public byte[] getChunkNo(){return chunkNo;}

	public byte[] getReplicationDeg(){return replicationDeg;}

	public String printHeader(){
			
		String build = Utils.convertBytetoString(this.getMessageType()) + " , " + Utils.convertBytetoString(this.getVersion()) + " , " + Utils.convertBytetoInt(senderId) + " , " + Utils.convertBytetoString(fileId);
		switch (Utils.convertBytetoString(this.getMessageType())){
			case "PUTCHUNK":
				return 	build + " , " + Utils.convertBytetoInt(this.chunkNo) + " , " + Utils.convertBytetoInt(this.getReplicationDeg());
			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
			case "CHUNK":
				return build + " , " + Utils.convertBytetoInt(this.chunkNo);
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
