package files;

import java.io.*;
import java.util.*;

import message.Message;
import utils.Utils;


public class RestoreFile extends InfoFile{

	private boolean[] chunkList;
	private String fileId;

	public RestoreFile(InfoFile info){
		super(info);
		chunkList = new boolean[super.getNumberChunks()];
		fileId = Utils.hashFileId(info.getFileName());
	}

	public boolean[] getChunkList() {return chunkList;}

	public String getFileId() {return fileId;}

	@Override
	public String getFileName() {
		return Arrays.toString(chunkList) + " , " +
				fileId;
	}

	@Override
	public void displayBackupChunks(){
		int i = 0;
		printHeadList(getFileId());

		for (; i < getNumberChunks() - 1; i++) {
			System.out.printf(" [%s] %2d ~ %d , %d\n", ((chunkList[i]) ? "yes" : ".no"), i, chunkList.length, getPartSize());
		}
		int lastpart = getFileSize() - ((getNumberChunks() - 1) * getPartSize());
		System.out.printf(" [%s] %2d ~ %d , %d bytes\n", ((chunkList[i]) ? "yes" : ".no"), i, chunkList.length, lastpart);

		printTailList(i);
	}


	public boolean completedChunks(){
		for(int i = 0 ; i < getNumberChunks() ; i++){
			if (!chunkList[i])
				return false;
		}
		return true;
	}

	public void deliveryChunk(Message m) throws FileNotFoundException{
		int chunkNo = Utils.convertBytetoInt(m.getHeader().getChunkNo());

		String name = fileId + File.separator +  fileId + ".part" + chunkNo ;

		try{
			File dir = new File(fileId);
			if (!dir.exists())
				dir.mkdir();

			writeChunk(name, Utils.convertBytetoString(m.getBody().getBody()));

			if(!chunkList[chunkNo]){
				chunkList[chunkNo] = true;
			}
		}
		catch(IOException e){
			e.getMessage();
		}
	}
}