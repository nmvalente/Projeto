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
		return Arrays.toString(chunkList) + " , " +	fileId;
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