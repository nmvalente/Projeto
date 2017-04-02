package files;
import java.io.*;
import java.util.*;

import utils.Utils;

public class BackupFile extends InfoFile{
	
	private String fileId;
	private int senderId;
	private ArrayList<Set<String>> listChunks;
	private int desiredReplicationDeg;


	public BackupFile(String fileName, int senderId, int desiredReplicationDeg){
		super(fileName);
		this.senderId = senderId;
		fileId = Utils.hashFileId(fileName);
		listChunks = new ArrayList<Set<String>>();
		this.desiredReplicationDeg = desiredReplicationDeg;

		for (int i = 0; i < super.getNChunks(); i++)
			listChunks.add( new LinkedHashSet<String>() );
	}

	public String getFileId() {return fileId;}

	public String getAllAddress(){
		int i = 0;
		String build = "{ ";
		for (; i<getNChunks()-1; i++)
			build += listChunks.get(i) + " , " ;

		build += listChunks.get(i) + " }";

		return build;
	}

	public int getDesiredReplicationDeg() { return this.desiredReplicationDeg; }

	public int getNSTORED(int index){
		return listChunks.get(index).size();
	}

	public boolean isBackupReplicatedEnough(){
		for (int i = 0; i < listChunks.size(); i++)
			if (listChunks.get(i).size() < desiredReplicationDeg)
				return false;
		return true;
	}

	public void addAddressOfChunk(int index, String address){
		listChunks.get(index).add(address);
	}

	public boolean removeAddressOfChunk(int index, String address){
		String temp;

		for (Iterator<String> it = listChunks.get(index).iterator(); it.hasNext();){
			temp = it.next();
			if (temp.equals(address)){
				it.remove();
				return true ;
			}
		}
		return false;
	}

	@Override
	public String toString(){

		return "BackupFile{" +
				super.toString() +
				"fileId='" + fileId + '\'' +
				", replicationDeg=" + getAllAddress() +
				", desiredReplicationDeg=" + desiredReplicationDeg +
				'}';
	}

	@Override
	public String getFileName(){
		
		return  super.getFileName() + " , " +
				fileId + " , " +
				desiredReplicationDeg + " , " +
				getAllAddress();
	}

	public void displayBackupChunks(){
		int i=0;
		printHeadList(getFileId());
		
		for (; i<getNChunks()-1; i++){
			System.out.printf("%2d ~ %d/%d , %s bytes\n", i, listChunks.get(i).size(), desiredReplicationDeg, getPartSize());
		}
		int lastpart = getFileSize() - ((getNChunks()-1) * getPartSize());
		System.out.printf("%2d ~ %d/%d , %s bytes\n", i, listChunks.get(i).size(), desiredReplicationDeg, lastpart);

		printTailList(i);
	}

	public byte[] file(int chunkNo) throws IOException{
		if (!(chunkNo>=0 && chunkNo<super.getNChunks()))
			return null;

		File f = new File( getFileId() + ".part" + chunkNo );
		
		if (f.exists()){
			int fsize = (int) f.length();
			FileInputStream fis = new FileInputStream( getFileId() + ".part" + chunkNo );
			byte[] data = new byte[fsize];
			fis.read(data, 0, fsize);
			f.delete();
			return data;
		}
		return null;
	}

	public int getSenderId() {
		return senderId;
	}
}