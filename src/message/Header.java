package message;

import utils.Utils;

public class Header{

	private byte[] messageType;
	private byte[] version;
	private byte[] senderId;
	private byte[] fileId; 				    
	private byte[] chunkNo;
	private byte[] replicationDeg;
	private String headerBuild;

	public Header(String messageType, String version, int senderId, String fileId, int chunkNo, int replicationDeg){
		if (!(Utils.belongsToMessageTypes(messageType))) throw new IllegalArgumentException("Invalid Head Arguments");
		
		// delete
		this.messageType = messageType.getBytes();
		this.version = version.getBytes();
		this.senderId = Utils.convertInttoByte(senderId);
		this.fileId = fileId.getBytes();
		this.headerBuild = messageType + " , " + version + " , " + senderId + " , " + fileId;
		
		switch(messageType){	
		case "STORED":
		case "GETCHUNK":
		case "REMOVED":
		case "CHUNK":// getchunk, remove, stored e chunk mas sem body
			this.chunkNo = Utils.convertInttoByte(chunkNo);
			this.headerBuild += " , " + Utils.convertBytetoInt(this.chunkNo);
			break;
		case "PUTCHUNK":// putchunk
			if(! validateReplication(replicationDeg)) throw new IllegalArgumentException("Invalid Head Arguments");
			this.chunkNo = Utils.convertInttoByte(chunkNo);
			this.replicationDeg = Utils.convertInttoByte(replicationDeg);
			this.headerBuild += " , " + chunkNo + " , " + replicationDeg;
			break;
		}		
	}

	public byte[] getMessageType(){return messageType;}
	public byte[] getVersion(){return version;}
	public byte[] getFileId(){return fileId;}
	public byte[] getSenderId(){return senderId;}
	public byte[] getChunkNo(){return chunkNo;}
	public byte[] getReplicationDeg(){return replicationDeg;}
	public String getHeaderBuild(){return headerBuild;}
	private boolean validateReplication(int replicationDeg2){return (replicationDeg2>=0 && replicationDeg2<=9);}
}
