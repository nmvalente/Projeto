package protocols;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import files.*;
import message.MessageManager;

public class Peer{

	private String localhost;
	private int PeerID;
	private float protocolVersion;
	protected MessageManager  inbox;
	public FileManager  files;
	protected int indexToChose;
	protected int indexChosed;
	protected Chunks chunks;
	protected Scanner scanner = new Scanner(System.in);

	public RestoreFile restoreFile = null;

	public Peer(String PeerID, String protocolVersion, String localhost) throws UnknownHostException{
		this.localhost = localhost;
		inbox  = new MessageManager();
		files  = new FileManager();
		chunks = new Chunks();
		this.PeerID = Integer.parseInt(PeerID);
		this.setProtocolVersion(Float.parseFloat(protocolVersion));
	}

	public String getLocalhost() {return localhost;}
	public MessageManager getInbox(){return inbox;}
	public FileManager getFiles(){return files;}
	public Chunks getChunks(){return chunks;}
	public int getPeerId(){return this.PeerID;}

	public int genericSubProtocol(int subProtocol) throws IOException{

		calc_bound_index(subProtocol);

		System.out.printf("\nOption [0-" + (this.indexToChose-1) +"] > ");  
		indexChosed = scanner.nextInt();

		if ( indexChosed >= 0 && indexChosed < indexToChose ){

			switch(subProtocol){
			case 1:	
				System.out.printf("\nReplication Degree [1-9] > ");
				int desiredReplicationDeg = scanner.nextInt();
				new Backup(this.indexChosed, desiredReplicationDeg, this);
				break;			
			case 2: 
				new Restore(this.indexChosed, this, this.restoreFile);
				break;
			case 3:	
				new Reclaim(this.indexChosed, this);
				break;
			case 4:
				new Delete(this.indexChosed, this);			
				break;
			default:
				break;
			}
		}
		else{System.out.println("> Invalid index!");}
		return 0;
	}

	private int calc_bound_index(int subProtocol) {
		if(subProtocol != 3){
			if(files.printAllFilesStored() == -1)
				return -1;
			this.indexToChose = this.files.getNumberOfFiles();
		}
		else{// RECLAIM
			chunks.list();
			this.indexToChose = this.chunks.getNChunk();
		}
		return this.indexToChose;
	}

	public float getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(float protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
}