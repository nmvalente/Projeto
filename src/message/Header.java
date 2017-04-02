package message;

import utils.Utils;

public class Header{

	private byte[] messageType;
	private byte[] version;
	private byte[] senderId;
	private byte[] fileId; 				    
	private byte[] chunkNo;
	private byte[] replicationDeg;

	public Header(String messageType, String version, int senderId, String fileId, int chunkNo, int replicationDeg){
		if (!(Utils.belongsToMessageTypes(messageType))) throw new IllegalArgumentException("Invalid Head Arguments");
		
		// delete
		this.messageType = messageType.getBytes();
		this.version = version.getBytes();
		this.senderId = Utils.convertInttoByte(senderId);
		this.fileId = fileId.getBytes();

		switch(messageType){	
		case "STORED":
		case "GETCHUNK":
		case "REMOVED":
		case "CHUNK":// getchunk, remove, stored e chunk mas sem body
			this.chunkNo = Utils.convertInttoByte(chunkNo);break;
		case "PUTCHUNK":// putchunk
			if(! validateReplication(replicationDeg)) throw new IllegalArgumentException("Invalid Head Arguments");
			this.chunkNo = Utils.convertInttoByte(chunkNo);
			this.replicationDeg = Utils.convertInttoByte(replicationDeg);
		}
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

	private boolean validateReplication(int replicationDeg2){return (replicationDeg2>=0 && replicationDeg2<=9);}
}
