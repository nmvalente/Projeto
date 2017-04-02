package protocols;

import java.io.IOException;

import files.BackupFile;
import protocols.Peer;
public class Backup {
	BackupFile backupFile;

	public Backup(int indexChosed, int desiredReplicationDeg, Peer peer) {
		
		String message = null;
		if (!(peer.getFiles().getFileList().get(indexChosed).getClass().getName().equals(BackupFile.class.getName()))){
			backupFile = peer.getFiles().backup(indexChosed, peer.getPeerId(), desiredReplicationDeg);
			try {
				System.out.println(" Receiving chunk backup confirmation");

				for (int i = 0; i < backupFile.getNChunks(); i++){
					System.out.println("\n**************************************************");
					System.out.println("> Waiting for next STORED reply" );

					message = new String(backupFile.file(i), Peer.CHARSET_NAME);

					peer.backupLoop(1, desiredReplicationDeg, i, message, backupFile);
				}
				System.out.println("\n**************************************************");
				System.out.println(" File backup finished. " + ((backupFile.isBackupReplicatedEnough()) ? "Successful" : "Incomplete") + ".\n");

				backupFile.displayBackupChunks();
			}
			catch(IOException e){e.getMessage(); System.err.println("Generic subProtocol case 1");}
		}
		else{System.out.println(" File is already in backup.");}
	}
}
