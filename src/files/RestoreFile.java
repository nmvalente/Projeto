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
		chunkList = new boolean[super.getNChunks()];
		fileId = Utils.hashFileId(info.getFileName());
	}

	public boolean[] getChunkList() {return chunkList;}

	public String getFileId() {return fileId;}

	@Override
	public String getFileName() {
		return Arrays.toString(chunkList) + " , " +
				fileId;
	}

	public void displayBackedChunks(){
		int i = 0;
		printHeadList(getFileId());
		
		for (; i < getNChunks() - 1; i++) {
			System.out.printf(" [%s] %2d ~ %d , %d\n", ((chunkList[i]) ? "yes" : ".no"), i, chunkList.length, getPartSize());
		}
		int lastpart = getFileSize() - ((getNChunks() - 1) * getPartSize());
		System.out.printf(" [%s] %2d ~ %d , %d bytes\n", ((chunkList[i]) ? "yes" : ".no"), i, chunkList.length, lastpart);

		printTailList(i);
	}


	public boolean isComplete(){
		for(int i = 0 ; i<getNChunks() ; i++){
			if (!chunkList[i]){
				return false;
			}
		}
		return true;
	}

	public void add(Message m) throws FileNotFoundException{
		int chunkNo = Utils.convertBytetoInt(m.getHeader().getChunkNo());

		String name = fileId + File.separator +  fileId + ".part" + chunkNo ;

		try{
			File dir = new File(fileId);
			if (!dir.exists())
				dir.mkdir();
				
			addChunk(name, Utils.convertBytetoString(m.getBody().getBody()));

			if(!chunkList[chunkNo]){
				chunkList[chunkNo] = true;
			}
		}
		catch(IOException e){
			e.getMessage();
		}
	}
}