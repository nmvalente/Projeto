package protocols;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import files.*;
import message.MessageManager;

public class Peer{

	private static final int WAITING_TIMES = 5;
	protected static final String CHARSET_NAME = "utf-8";
	private static int Id = 1;
	private String localhost;
	private int peerId;
	protected MessageManager  inbox;
	protected FileManager  files;
	protected int indexToChose;
	protected int indexChosed;
	protected Chunks chunks;
	protected Scanner scanner = new Scanner(System.in);
	private Random random;

	public RestoreFile restoreFile = null;

	public Peer(String localhost){
		this.localhost = localhost;
		inbox  = new MessageManager();
		files  = new FileManager();
		chunks = new Chunks();
		peerId = Id;
		Id++;
		this.random = new Random();
	}

	public String getLocalhost() {return localhost;}
	public MessageManager getInbox(){return inbox;}
	public FileManager getFiles(){return files;}
	public Chunks getChunks(){return chunks;}

	public int getPeerId(){return this.peerId;}

	public int genericSubProtocol(int subProtocol) throws IOException{

		calc_bound_index(subProtocol);

		System.out.printf("\nOption [0-" + (this.indexToChose-1) +"] > ");  
		indexChosed = scanner.nextInt();

		// validar index
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

	public void restoreLoop(int subProtocol, RestoreFile restoreFile){
		int count = 0, alea;
		alea = random.nextInt(400);
		do{
			try{
				System.out.printf(" Try #%d. Sleeping for %4d ms.", count + 1, alea);
				Thread.sleep(alea);
				System.out.printf(" %s.\n", ((restoreFile.isComplete())?" Complete":"Incomplete") );
			}
			catch(InterruptedException e){e.getMessage(); System.err.println("Thread error in restoreLoop");}
			count++;
		}while(count < WAITING_TIMES && !restoreFile.isComplete());
	}

	public int backupLoop(int subProtocol, int desiredReplicationDeg, int i, String msg, BackupFile backupFile){
		int nStored, count=0, alea;
		alea = random.nextInt(400);
		do{ 
			inbox.query("PUTCHUNK","1.0", backupFile.getSenderId() ,backupFile.getFileId(),i,desiredReplicationDeg,msg);

			try{
				System.out.printf(" Try #%d. Sleeping for %4d ms.", count+1, alea);
				Thread.sleep(alea);
				nStored = backupFile.getNSTORED(i);
				System.out.printf(" CHUNK #%d with %d/%d STORED.\n", i, nStored, desiredReplicationDeg);
			}
			catch(InterruptedException e){e.getMessage(); System.err.println("Thread error in backupLoop");}

			count++;
			nStored = backupFile.getNSTORED(i);

		} while( count < WAITING_TIMES && nStored < desiredReplicationDeg );
		return count;
	}
}