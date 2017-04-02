package files;

import java.io.File;

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
    
	public void deleteFile(String pathName){
		File f = new File(pathName);
		if (f.exists()){
			f.delete();
		}
	}

    @Override
    public String toString() {
        return "Chunk{" +
        		" fileId='" + Utils.convertBytetoString(fileId) + '\'' +
                ", senderId='" + Utils.convertBytetoInt(senderID) + '\'' +
                ", chunkNo=" + Utils.convertBytetoInt(chunkNo) + '\'' +
                ", replicationDeg=" + Utils.convertBytetoInt(replicationDeg) +
                '}';
    }

	public byte[] getSenderId() {
		return senderID;
	}
}
