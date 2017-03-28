package filefunc;

import message.Message;

public class Chunk
{
    private String fileId;
    private int chunkNo;
    private int replicationDeg;

    Chunk(String fileId, int chunkNo, int replicationDeg)
    {
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
    }

    Chunk(Message m)
    {
        this.fileId = m.head.getFileId();
        this.chunkNo = m.head.getChunkNo();
        this.replicationDeg = m.head.getReplicationDeg();
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
