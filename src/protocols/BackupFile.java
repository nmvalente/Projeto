package protocols;
import java.io.*;
import java.util.*;

import filefunc.*;

public class BackupFile extends Ufile
{
	private String fileId;
	private int senderId;
	private ArrayList<Set<String>> listChunks;
	private int desiredReplicationDeg;


	public BackupFile(String fileName, int senderId, int desiredReplicationDeg)
	{
		super(fileName);
		this.senderId = senderId;
		fileId = sha256();
		listChunks = new ArrayList<Set<String>>();
		this.desiredReplicationDeg = desiredReplicationDeg;

		for (int i = 0; i < super.getNChunks(); i++)
			listChunks.add( new LinkedHashSet<String>() );
	}

	public String getFileId() {return fileId;}

	public String getAllAddress(){

		int i=0;

		String s = "{ ";

		for (; i<getNChunks()-1; i++)
			s += listChunks.get(i) + " , " ;

		s += listChunks.get(i) + " }";

		return s;
	}

	public int getDesiredReplicationDeg() { return this.desiredReplicationDeg; }

	public int getNSTORED(int index){
		return listChunks.get(index).size();
	}

	public boolean isBackupReplicatedEnough(){
		for (int i = 0; i < listChunks.size(); i++)
			if ( listChunks.get(i).size() < desiredReplicationDeg )
				return false;

		return true;
	}

	public void addAddressOfChunk(int index, String address){
		listChunks.get(index).add(address);
	}

	public void removeAddressOfChunk(int index, String address){
		String temp;

		for (Iterator<String> it = listChunks.get(index).iterator(); it.hasNext();){
			temp = it.next();
			if (temp.equals(address)){
				it.remove();
				return ;
			}
		}
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
	public String fileName(){

		return  super.fileName() + " , " +
				fileId + " , " +
				desiredReplicationDeg + " , " +
				getAllAddress();
	}

	public void list()
	{
		int i=0, lastpart;

		System.out.println("\n List of STORED backup chunks" );
		System.out.println(" file   : " + getFileName() );
		System.out.println(" fileId : " + getFileId() );
		System.out.println("\n**************************************************");

		for (; i<getNChunks()-1; i++)
		{
			//System.out.println( temp.simple() );
			System.out.printf("%2d ~ %d/%d , %s bytes\n", i, listChunks.get(i).size(), desiredReplicationDeg, getPartSize());
		}

		lastpart = getFileSize() - ((getNChunks()-1) * getPartSize());
		System.out.printf("%2d ~ %d/%d , %s bytes\n", i, listChunks.get(i).size(), desiredReplicationDeg, lastpart);

		System.out.println("\n**************************************************");
		System.out.printf( " Listed %d chunk%s.\n\n", i, ((i==1)?"":"s"));
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