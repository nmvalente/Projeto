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

		for (int i = 0; i < super.getNumberChunks(); i++)
			listChunks.add( new LinkedHashSet<String>() );
	}

	public String getFileId() {return fileId;}
	public int getDesiredReplicationDeg() { return this.desiredReplicationDeg; }
	public int getNSTORED(int index){return getListChunks().get(index).size();}
	public int getSenderId(){return senderId;}
	public ArrayList<Set<String>> getListChunks(){ return listChunks;}

	public boolean enoughReplication(){
		for (int i = 0; i < getListChunks().size(); i++)
			if (getListChunks().get(i).size() < desiredReplicationDeg)
				return false;
		return true;
	}

	public boolean removeAddressOfChunk(int index, String address){
		String temp;

		for (Iterator<String> it = getListChunks().get(index).iterator(); it.hasNext();){
			temp = it.next();
			if (temp.equals(address)){
				it.remove();
				return true ;
			}
		}
		return false;
	}

	public byte[] getContent(int chunkNo){
		if (!(chunkNo >= 0 && chunkNo < super.getNumberChunks()))
			return null;

		File f = new File( getFileId() + ".part" + chunkNo );

		if (f.exists()){
			int fsize = (int) f.length();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream( getFileId() + ".part" + chunkNo );
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			byte[] data = new byte[fsize];
			try {
				fis.read(data, 0, fsize);
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			f.delete();
			return data;
		}
		return null;
	}
}