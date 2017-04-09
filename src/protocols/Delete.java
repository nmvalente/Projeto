package protocols;

import files.BackupFile;

public class Delete {

	BackupFile backupFile;
	public Delete(int indexChosed, Peer peer) {
		
	
	if (peer.getFiles().getFileList().get(indexChosed).getClass().getName().equals(BackupFile.class.getName())){

		backupFile = (BackupFile) peer.getFiles().getFileList().get(indexChosed);

		peer.messageHandler.buildMessage("DELETE", Float.toString(peer.getProtocolVersion()), backupFile.getSenderId(),  backupFile.getFileId(), 0, 0, "");

		backupFile.deleteFile(backupFile.getFileName());
		peer.getFiles().getFileList().remove(indexChosed);

		System.out.println("> File deleted!");
	}
	else{System.out.println("> Not in backup!");}
	}
}
