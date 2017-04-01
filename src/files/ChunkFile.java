package files;

import message.Message;
import utils.Utils;

public class ChunkFile{
	
    private byte[] fileId;
    private byte[] chunkNo;
    private byte[] replicationDeg;

    ChunkFile(String fileId, int chunkNo, int replicationDeg){
        this.fileId = fileId.getBytes();
        this.chunkNo = Utils.convertInttoByte(chunkNo);
        this.replicationDeg =  Utils.convertInttoByte(replicationDeg);
    }

    ChunkFile(Message m){
        this.fileId = m.getHeader().getFileId();
        this.chunkNo = m.getHeader().getChunkNo();
        this.replicationDeg = m.getHeader().getReplicationDeg();
    }

    public byte[] getFileId(){return fileId;}

    public byte[] getChunkNo(){return chunkNo;}

    public byte[] getReplicationDeg() { return replicationDeg; }

    @Override
    public String toString() {
        return "Chunk{" +
                ", fileId='" + fileId + '\'' +
                ", chunkNo=" + chunkNo + '\'' +
                ", replicationDeg=" + replicationDeg +
                '}';
    }
}
