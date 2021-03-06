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
	protected MessageManager messageHandler;
	public FileManager  files;
	protected int indexToChose;
	protected int indexChosed;
	protected Chunks chunks;
	protected Scanner scanner = new Scanner(System.in);

	public RestoreFile restoreFile;

	public Peer(String PeerID, String protocolVersion, String localhost) throws UnknownHostException{
		this.localhost = localhost;
		messageHandler  = new MessageManager();
		files  = new FileManager();
		chunks = new Chunks();
		this.PeerID = Integer.parseInt(PeerID);
		this.protocolVersion = Float.parseFloat(protocolVersion);
	}

	public String getLocalhost() {return localhost;}
	public MessageManager getInbox(){return messageHandler;}
	public FileManager getFiles(){return files;}
	public Chunks getChunks(){return chunks;}
	public int getPeerId(){return this.PeerID;}
	public float getProtocolVersion(){return protocolVersion;}

	public void genericSubProtocol(int subProtocol) throws IOException{

		calc_bound_index(subProtocol);
		if(this.indexToChose == 0)
			return;

		System.out.printf("\nOption [0-" + (this.indexToChose-1) +"] > ");  
		indexChosed = scanner.nextInt();

		if ( indexChosed >= 0 && indexChosed < indexToChose ){

			switch(subProtocol){
			case 1:	
				System.out.printf("Replication Degree [1-9] > ");
				int desiredReplicationDeg = scanner.nextInt();
				new Backup(this.indexChosed, desiredReplicationDeg, this);
				break;			
			case 2: 
				new Restore(this.indexChosed, this);
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
	}

	private int calc_bound_index(int subProtocol) {
		if(subProtocol != 3){
			if(files.printAllFilesStored() == -1)
				return -1;
			this.indexToChose = this.files.getNumberOfFiles();
		}
		else{// RECLAIM
			chunks.printAllChunks();
			this.indexToChose = this.chunks.getNChunk();
		}
		return this.indexToChose;
	}
}