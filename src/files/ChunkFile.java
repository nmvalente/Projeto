package files;

import message.Message;

public class ChunkFile
{
    private String fileId;
    private int chunkNo;
    private int replicationDeg;

    ChunkFile(String fileId, int chunkNo, int replicationDeg)
    {
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
    }

    ChunkFile(Message m)
    {
        this.fileId = m.header.getFileId();
        this.chunkNo = m.header.getChunkNo();
        this.replicationDeg = m.header.getReplicationDeg();
    }

    public String getFileId()
    {
        return fileId;
    }

    public int getChunkNo()
    {
        return chunkNo;
    }

    public int getReplicationDeg() { return replicationDeg; }

    @Override
    public String toString() {
        return "Chunk{" +
                ", fileId='" + fileId + '\'' +
                ", chunkNo=" + chunkNo + '\'' +
                ", replicationDeg=" + replicationDeg +
                '}';
    }

    public String simple() {
        return  fileId  + " , " +
                chunkNo + " , " +
                replicationDeg ;
    }
}
