package protocols;

import java.io.IOException;
import java.util.Random;

import files.BackupFile;
import files.InfoFile;
import interfaces.Main;
import protocols.Peer;
import utils.Utils;
public class Backup {

	private Random random = new Random();
	BackupFile backupFile;

	public Backup(int indexChosed, int desiredReplicationDeg, Peer peer) {

		String message = null;
		if (!(peer.getFiles().getFileList().get(indexChosed).getClass().getName().equals(BackupFile.class.getName()))){
			backupFile = backup(indexChosed, peer.getPeerId(), desiredReplicationDeg, peer);
			try {
				System.out.println(" Receiving chunk backup confirmation");

				for (int i = 0; i < backupFile.getNumberChunks(); i++){
					System.out.println("\n**************************************************");
					System.out.println("> Waiting for next STORED reply" );

					message = new String(backupFile.getContent(i), Utils.CHARSET_NAME);

					backupLoop(desiredReplicationDeg, i, message, backupFile, peer);
				}
				System.out.println("\n**************************************************");
				System.out.println(" File backup finished. " + ((backupFile.enoughReplication()) ? "Successful" : "Incomplete") + ".\n");
			}
			catch(IOException e){e.getMessage(); System.err.println("Generic subProtocol case 1");}
		}
		else{System.out.println(" File is already in backup.");}
	}

	public Backup(BackupFile backupFile, int desiredReplicationDeg, Peer peer) {
		BackupFile backupFile2 = backup(backupFile.getIndex(), peer.getPeerId(), desiredReplicationDeg, peer);

		String message = null;
		try { 
			System.out.println(" Receiving chunk backup confirmation");

			for (int i = 0; i < backupFile2.getNumberChunks(); i++){
				System.out.println("\n**************************************************");
				System.out.println("> Waiting for next STORED reply" );

				message = new String(backupFile2.getContent(i), Utils.CHARSET_NAME);

				backupLoop(desiredReplicationDeg, i, message, backupFile2, peer);
			}
			System.out.println("\n**************************************************");
			System.out.println(" File backup finished. " + ((backupFile2.enoughReplication()) ? "Successful" : "Incomplete") + ".\n");
		}
		catch(IOException e){e.getMessage(); System.err.println("Generic subProtocol case 1");}
			Main.displayMenu();
	}

	public int backupLoop(int desiredReplicationDeg, int i, String msg, BackupFile backupFile, Peer peer){
		int nStored, count=0, alea;
		alea = random.nextInt(400);
		do{ 
			peer.messageHandler.buildMessage("PUTCHUNK", Float.toString(peer.getProtocolVersion()), backupFile.getSenderId(), backupFile.getFileId(), i, desiredReplicationDeg,msg);

			try{
				System.out.printf(" Try #%d. Sleeping for %4d ms.", count+1, alea);
				Thread.sleep(alea);
				nStored = backupFile.getNSTORED(i);
				System.out.printf(" CHUNK #%d with %d/%d STORED.\n", i, nStored, desiredReplicationDeg);
			}
			catch(InterruptedException e){e.getMessage(); System.err.println("Thread error in backupLoop");}

			count++;
			nStored = backupFile.getNSTORED(i);

		} while( count < Utils.WAITING_TIMES && nStored < desiredReplicationDeg );
		return count;
	}

	public BackupFile backup(int fileIndex, int senderID, int desiredReplicationDeg, Peer peer){
		InfoFile info  = peer.files.getFileList().get(fileIndex);
		BackupFile backupFile = new BackupFile(info.getFileName(), senderID ,desiredReplicationDeg, fileIndex, peer);

		peer.files.getFileList().set(fileIndex, backupFile);
		backupFile.splitFile(peer);
		return backupFile;
	} 
}
