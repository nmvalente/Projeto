package protocols;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import files.BackupFile;
import files.InfoFile;
import files.RestoreFile;
import utils.Utils;

public class Restore {

	private Random random = new Random();
	BackupFile backupFile;
	RestoreFile restoreFile;

	public Restore(int indexChosed, Peer peer, RestoreFile restoreFile) {

		if (peer.getFiles().getFileList().get(indexChosed).getClass().getName().equals(BackupFile.class.getName())){
			backupFile = (BackupFile) peer.getFiles().getFileList().get(indexChosed);
			restoreFile = new RestoreFile( (InfoFile) backupFile );

			for (int i=0; i < backupFile.getNChunks(); i++){
				peer.inbox.buildMessage("GETCHUNK","1.0",backupFile.getSenderId(), backupFile.getFileId(),i,1,"");
			}

			System.out.println(" Receiving chunk restore information");
			System.out.println("\n**************************************************");
			restoreLoop(2, restoreFile);
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
		}while(count < Utils.WAITING_TIMES && !restoreFile.isComplete());
	}
}
