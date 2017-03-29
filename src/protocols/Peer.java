package protocols;
import java.io.IOException;
import java.util.Scanner;

import files.*;
import message.MessageManager;
import java.io.File;

public class Peer
{
	private static final int WAITING_TIMES = 5;
	private static final String CHARSET_NAME = "utf-8";
	private static int Id = 1;
	private String localhost;
	private int peerId;
	public MessageManager  inbox;
	public FileManager  files;
	protected int indexToChose;
	protected int indexChosed;
	public Chunks chunks;
	protected Scanner scanner = new Scanner(System.in);
	private BackupFile backupFile;

	public RestoreFile restoreFile = null;

	public Peer(String localhost){
		this.localhost = localhost;
		inbox  = new MessageManager();
		files  = new FileManager();
		chunks = new Chunks();
		peerId = Id;
		Id++;
	}

	public String getLocalhost() {return localhost;}

	public int getPeerId(){return this.peerId;}

	public int genericSubProtocol(int subProtocol){
		 int i=0;

		// Lista de ficheiros
		if(files.printAllFilesStored() == -1)
			return -1;

		this.indexToChose = this.files.getNumberOfFiles();
		System.out.printf("\nOption [0-" + (this.indexToChose-1) +"] > ");        

		indexChosed = scanner.nextInt();

		// validar index
		if ( indexChosed >= 0 && indexChosed < indexToChose ){

			switch(subProtocol){
			case 1: // BACKUP
				// receber desiredReplicationDeg
				System.out.printf("\nReplication Degree [1-9] > ");
				int desiredReplicationDeg = scanner.nextInt();
				String message = null;
				if (!(files.getFileList().get(indexChosed).getClass().getName().equals(BackupFile.class.getName()))){
					backupFile = files.backup(indexChosed, this.peerId, desiredReplicationDeg);
					try {
						System.out.println(" Receiving chunk backup confirmation");

						for (i=0; i < backupFile.getNChunks(); i++){
							System.out.println("\n**************************************************");
							System.out.println("> Waiting for next STORED reply" );

							message = new String(backupFile.file(i), CHARSET_NAME);

							backupLoop(subProtocol, desiredReplicationDeg, i, message);
						}
						System.out.println("\n**************************************************");
						System.out.println(" File backup finished. " + ((backupFile.isBackupReplicatedEnough()) ? "Successful" : "Incomplete") + ".\n");

						backupFile.displayBackupChunks();
					}
					catch(IOException e){
						e.getMessage();
					}
				}
				else{System.out.println(" File is already in backup.");}
				break;			
			case 2:  // RESTORE
				if (files.getFileList().get(indexChosed).getClass().getName().equals(BackupFile.class.getName())){
					backupFile = (BackupFile) files.getFileList().get(indexChosed);
					restoreFile = new RestoreFile( (InfoFile) backupFile );

					for (i=0; i < backupFile.getNChunks(); i++){
						inbox.query("GETCHUNK","1.0",backupFile.getSenderId(), backupFile.getFileId(),i,1,"");
					}

					System.out.println(" Receiving chunk restore information");
					System.out.println("\n**************************************************");
					restoreLoop(subProtocol);
					System.out.println("\n**************************************************");
					System.out.println(" File restore finished. " + ((restoreFile.isComplete()) ? "Successful" : "Incomplete") + ".\n");

					restoreFile.displayBackedChunks();

					if(restoreFile.isComplete()){
						restoreFile.merge();
						restoreFile.removeDirectory( new File(backupFile.getFileId()));
						restoreFile = null;
					}
				}
				break;
			case 3: // RECLAIM
				break;
			case 4: // DELETE
				
				if (files.getFileList().get(indexChosed).getClass().getName().equals(BackupFile.class.getName())){

					backupFile = (BackupFile) files.getFileList().get(indexChosed);

					inbox.query("DELETE", "1.0", backupFile.getSenderId(),  backupFile.getFileId(), i, 0, "");

					backupFile.removeFile(backupFile.getFileName());
					files.getFileList().remove(indexChosed);

					System.out.println("> File deleted!");
				}
				else{System.out.println("> Not in backup!");}
				break;
			default:
				break;
			}
		}
		else{System.out.println("> Invalid index!");}
		return 0;
	}

	public void restoreLoop(int subProtocol) {
		int count = 0;
		do{
			try{
				System.out.printf(" Try #%d. Sleeping for %4d ms.", count + 1, sleepingTime(count, subProtocol));
				Thread.sleep(400);
				System.out.printf(" %s.\n", ((restoreFile.isComplete())?" Complete":"Incomplete") );
			}
			catch(InterruptedException e)
			{
				e.getMessage();}

			count++;

		} while( count < WAITING_TIMES && !restoreFile.isComplete() );
	}

	public int backupLoop(int subProtocol, int desiredReplicationDeg, int i, String msg) {
		int nStored, count=0;
		do{ 
			inbox.query("PUTCHUNK","1.0", backupFile.getSenderId() ,backupFile.getFileId(),i,desiredReplicationDeg,msg);

			try{
				System.out.printf(" Try #%d. Sleeping for %4d ms.", count+1, sleepingTime(count, subProtocol));
				Thread.sleep(sleepingTime(count, subProtocol));
				nStored = backupFile.getNSTORED(i);
				System.out.printf(" CHUNK #%d with %d/%d STORED.\n", i, nStored, desiredReplicationDeg);
			}
			catch(InterruptedException e){
				e.getMessage();
			}

			count++;
			nStored = backupFile.getNSTORED(i);

		} while( count < WAITING_TIMES && nStored < desiredReplicationDeg );
		return count;
	}

	
	public int sleepingTime(int count, int subProtocol) {
		if(subProtocol == 1)
			return 500 * ((2 * count == 0) ? 1 : 2 * count);
		else
			return 400 * ((2 * count == 0) ? 1 : 2 * count);
	}

	public int reclaim() {
		return 0;
	}
}