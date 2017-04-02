package files;

import message.Message;
import utils.Utils;

public class ChunkFile{

	private byte[] fileId;
	private byte[] chunkNo;
	private byte[] replicationDeg;
	private byte[] senderID;

	ChunkFile(Message m){
		this.fileId = m.getHeader().getFileId();
		this.chunkNo = m.getHeader().getChunkNo();
		this.replicationDeg = m.getHeader().getReplicationDeg();
		this.senderID = m.getHeader().getSenderId();
	}

	public byte[] getFileId(){return fileId;}
	public byte[] getChunkNo(){return chunkNo;}
	public byte[] getReplicationDeg() { return replicationDeg; }
	public byte[] getSenderId(){return senderID;}

	@Override
	public boolean equals(Object object) {
		if(! (object instanceof ChunkFile)) return false;
		ChunkFile o = (ChunkFile) object;
		
		if(Utils.convertBytetoString(this.fileId).equals(Utils.convertBytetoString(o.fileId)))
			return true;
		else
			return false;
	}
	
	/*
	public void deleteFile(String pathName){
		File f = new File(pathName);
		if (f.exists()){
			f.delete();
		}
	}
	 */
	/*@Override
    public String toString() {
        return "Chunk{" +
        		" fileId='" + Utils.convertBytetoString(fileId) + '\'' +
                ", senderId='" + Utils.convertBytetoInt(senderID) + '\'' +
                ", chunkNo=" + Utils.convertBytetoInt(chunkNo) + '\'' +
                ", replicationDeg=" + Utils.convertBytetoInt(replicationDeg) +
                '}';
    }
	 */

}
