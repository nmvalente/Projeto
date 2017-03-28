package message;
public class Head
{
	private String messageType;  		   // = {"PUTCHUNK","STORED","GETCHUNK","CHUNK","DELETE","REMOVED"};
	private String version; 			   // versao do protocolo
	private String fileId; 				   // nome em SHA256
	private int chunkNo; 				   // nº de chunk
	private int replicationDeg;			   // nº de cópias


	public Head(String messageType, String version, String fileId)
	{
		if (!(isMessageType(messageType) && isVersion(version)))
			throw new IllegalArgumentException("Invalid Head Arguments");

		this.messageType = messageType;
		this.version = version;
		this.fileId = fileId;
	}

	public Head(String messageType, String version, String fileId, int chunkNo)
	{
		if (!(isMessageType(messageType) && isVersion(version)))
			throw new IllegalArgumentException("Invalid Head Arguments");

		this.messageType = messageType;
		this.version = version;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}

	public Head(String messageType, String version, String fileId, int chunkNo, int replicationDeg)
	{
		if (!(isMessageType(messageType) && isVersion(version) && isReplicationDeg(replicationDeg)))
			throw new IllegalArgumentException("Invalid Head Arguments");

		this.messageType = messageType;
		this.version = version;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.replicationDeg = replicationDeg;
	}

	public String getMessageType()
	{
		return messageType;
	}

	public String getVersion()
	{
		return version;
	}

	public String getFileId()
	{
		return fileId;
	}

	public int getChunkNo()
	{
		return chunkNo;
	}

	public int getReplicationDeg()
	{
		return replicationDeg;
	}

	@Override
	public String toString()
	{
		return "Head{" +
				"messageType='" + messageType + '\'' +
				", replicationDeg=" + replicationDeg + '\'' +
				", version='" + version + '\'' +
				", fileId='" + fileId + '\'' +
				", chunkNo=" + chunkNo +
				'}';
	}

	public String simple()
	{
		switch ( messageType )
		{
			case "PUTCHUNK":
				return 	messageType + " , " +
						version + " , " +
						fileId + " , " +
						chunkNo + " , " +
						replicationDeg ;

			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
			case "CHUNK":
				return 	messageType + " , " +
						version + " , " +
						fileId + " , " +
						chunkNo ;

			case "DELETE":
				return 	messageType + " , " +
						version + " , " +
						fileId ;

			default: return "Invalid message type.";
		}

	}

	private boolean isMessageType(String msgtype)
	{
		return (
				msgtype.equals("PUTCHUNK") ||
				msgtype.equals("STORED") ||
				msgtype.equals("GETCHUNK") ||
				msgtype.equals("CHUNK") ||
				msgtype.equals("DELETE") ||
				msgtype.equals("REMOVED")
		);
	}

	private boolean isVersion(String vers)
	{
		return ( vers.equals("1.0") );
	}

	private boolean isChunkNo(int num)
	{
		return ( num >= 0 );
	}

	private boolean isReplicationDeg(int repl)
	{
		return (repl>=0 && repl<=9);
	}
}
