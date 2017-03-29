package protocols;
import java.io.IOException;
import java.util.Scanner;

import filefunc.*;
import message.MessageManager;
import java.io.File;

public class Peer
{
	private int WAITING_TIMES_PUTCHUNK = 5;
	private static int Id = 1;
	private String localhost;
	private int peerId;
	public MessageManager  inbox;
	public FileManager  files;
	protected int numberOfFiles;
	public Chunks chunks;
	protected Scanner scanner = new Scanner(System.in);

	public RestoreFile restoreFile = null;

	public Peer(String localhost){
		this.localhost = localhost;
		inbox  = new MessageManager();
		files  = new FileManager();
		chunks = new Chunks();
		peerId = Id;
		Id++;
		this.numberOfFiles = files.getNumberOfFiles();
	}

	public String getLocalhost() { return localhost; }

	public int getPeerId(){return this.peerId;}

	public int manageBackup(){

		BackupFile backupFile;

		int index,
		desiredReplicationDeg=0,
		i=0,
		count=0,
		nStored=0;

		String msg = null;

		// Lista de ficheiros
		if(files.printAllFilesStored(1) == -1)
			return -1;

		System.out.printf("\nOption [0-" + numberOfFiles +"] > ");        

		index = scanner.nextInt();

		// receber desiredReplicationDeg
		System.out.printf("\nReplication Degree [1-9] > ");
		desiredReplicationDeg = scanner.nextInt();

		// validar index
		if ( index >= 0 && index < numberOfFiles ){
			if ( !(files.getFileList().get(index).getClass().isInstance(BackupFile.class))){
				backupFile = files.backup(index, this.peerId, desiredReplicationDeg);
				try {
					System.out.println(" Receiving chunk backup confirmation");

					for (i=0; i < backupFile.getNChunks(); i++){
						System.out.println("\n**************************************************");
						System.out.println("> Waiting for next STORED reply" );

						count=0;
						nStored=0;

						msg = new String(backupFile.file(i), "utf-8");

						do{ 
							inbox.query("PUTCHUNK","1.0", backupFile.getSenderId() ,backupFile.getFileId(),i,desiredReplicationDeg,msg);

							try{
								System.out.printf(" Try #%d. Sleeping for %4d ms.", count+1, 500 * ((2 * count == 0) ? 1 : 2 * count));
								Thread.sleep(500 * ((2 * count == 0) ? 1 : 2 * count));
								nStored = backupFile.getNSTORED(i);
								System.out.printf(" CHUNK #%d with %d/%d STORED.\n", i, nStored, desiredReplicationDeg);
							}
							catch(InterruptedException e){
								e.getMessage();
							}

							count++;
							nStored = backupFile.getNSTORED(i);

						} while( count<WAITING_TIMES_PUTCHUNK && nStored<desiredReplicationDeg );
					}
					System.out.println("\n**************************************************");
					System.out.println(" File backup finished. " + ((backupFile.isBackupReplicatedEnough()) ? "Successful" : "Incomplete") + ".\n");

					backupFile.list();
				}
				catch(IOException e){
					e.getMessage();
				}
			}
			else{System.out.println(" File is already in backup.");}
		}
		else{System.out.println(" Invalid file index.");}
		return 0;
	}

	public int restore(){
		BackupFile backupFile;
		int index=0, i=0, count=0;

		// Lista de ficheiros
		if(files.printAllFilesStored(2) == -1)
			return -1;

		System.out.printf("\nOption [0-" + (numberOfFiles-1) +"] > ");        

		index = scanner.nextInt();

		if ( index>=0 && index<files.getFileList().size() ){
			if ( (files.getFileList().get(index).getClass().isInstance(BackupFile.class))){
				backupFile = (BackupFile) files.getFileList().get(index);
				restoreFile = new RestoreFile( (Ufile) backupFile );

				for (i=0; i < backupFile.getNChunks(); i++){
					inbox.query("GETCHUNK","1.0", backupFile.getSenderId(),backupFile.getFileId(),i,1,"");
				}

				System.out.println(" Receiving chunk restore information");
				System.out.println("\n**************************************************");

				do{
					try{
						System.out.printf(" Try #%d. Sleeping for %4d ms.", count + 1, 400 * ((2 * count == 0) ? 1 : 2 * count));
						Thread.sleep(400);
						System.out.printf(" %s.\n", ((restoreFile.isComplete())?" Complete":"Incomplete") );
					}
					catch(InterruptedException e){
						e.getMessage();
					}
					count++;

				} while( count<5 && !restoreFile.isComplete() );

				System.out.println("\n**************************************************");
				System.out.println(" File restore finished. " + ((restoreFile.isComplete()) ? "Successful" : "Incomplete") + ".\n");

				restoreFile.list();

				if ( restoreFile.isComplete() ){
					restoreFile.merge();
					Ufile.removeDirectory( new File(backupFile.getFileId()) );
					restoreFile = null;
				}
			}
			else{System.out.println(" File is not in backup.");}
		}
		else{System.out.println(" Invalid file index.");}
		return 0;
	}

	public int delete(){
		BackupFile backupFile;
		int index=0, i=0;

		if(files.printAllFilesStored(3) == -1)
			return -1;

		System.out.printf("\nOption [0-" + (numberOfFiles-1) +"] > ");        
		index = scanner.nextInt();

		if ( index>=0 && index<files.getFileList().size() ){
			if ((files.getFileList().get(index).getClass().isInstance(BackupFile.class))){

				backupFile = (BackupFile) files.getFileList().get(index);

				inbox.query("DELETE", "1.0", backupFile.getSenderId(),  backupFile.getFileId(), i, 0, "");


				backupFile.removeFile(backupFile.getFileName());
				files.getFileList().remove(index);

				System.out.println("File delete Successful.");
			}
			else{System.out.println("File is not in backup.");}
		}
		else{System.out.println("Invalid file index.");}
		return 0;
	}

	public int reclaim() {

		return 0;
	}
}