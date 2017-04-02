package protocols;

import java.io.File;
import java.io.IOException;

import files.BackupFile;
import files.InfoFile;
import files.RestoreFile;

public class Restore {


	BackupFile backupFile;
	
	public Restore(int indexChosed, Peer peer, RestoreFile restoreFile) {
		
		
	if (peer.getFiles().getFileList().get(indexChosed).getClass().getName().equals(BackupFile.class.getName())){
		backupFile = (BackupFile) peer.getFiles().getFileList().get(indexChosed);
		restoreFile = new RestoreFile( (InfoFile) backupFile );

		for (int i=0; i < backupFile.getNChunks(); i++){
			peer.inbox.query("GETCHUNK","1.0",backupFile.getSenderId(), backupFile.getFileId(),i,1,"");
		}

		System.out.println(" Receiving chunk restore information");
		System.out.println("\n**************************************************");
		peer.restoreLoop(2, restoreFile);
		System.out.println("\n**************************************************");
		System.out.println(" File restore finished. " + ((restoreFile.isComplete()) ? "Successful" : "Incomplete") + ".\n");

		restoreFile.displayBackedChunks();

		if(restoreFile.isComplete()){
			try {
				restoreFile.merge();
			} catch (IOException e) {e.printStackTrace();}
			restoreFile.deleteDirectory( new File(backupFile.getFileId()));
			restoreFile = null;
		}
	}
	
	
	}
}
