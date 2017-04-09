package files;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.io.*;

import message.Message;
import utils.Utils;

public class Chunks{

	private ConcurrentHashMap<String, List<ChunkFile>> hashmap;

	public Chunks(){
		hashmap = new ConcurrentHashMap<String, List<ChunkFile>>();
	}

	public ConcurrentHashMap<String, List<ChunkFile>> getChunksList(){return hashmap;}

	public void add(Message m){ //
		String ip   = m.getAddress();
		String name = ip + File.separator + Utils.convertBytetoString(m.getHeader().getFileId()) + ".part" + Utils.convertBytetoInt(m.getHeader().getChunkNo()) ;

		try{

			File dir = new File(ip);
			if (!dir.exists())
				dir.mkdir();

			addChunk( name, Utils.convertBytetoString(m.getBody().getBody()));

			ChunkFile c = new ChunkFile(m);

			if(!getChunksList().containsKey(ip)){
				List<ChunkFile> list= new ArrayList<ChunkFile>();
				list.add(c);
				getChunksList().put(ip,list);
			}
			else{ //////
				boolean v = false;
				for(ChunkFile elem : getChunksList().get(ip)){
					if(elem.equals(c)){
						v = true;
					}
				}
				if(!v){
					getChunksList().get(ip).add(c);
				}
			}

		}
		catch(FileNotFoundException e){e.getMessage();}
		catch(IOException e){e.getMessage();}
	}

	public boolean removeAll(String address, String fileId){
		if(getChunksList().containsKey(address)){
			ChunkFile temp;
			for(Iterator<ChunkFile> it = getChunksList().get(address).iterator(); it.hasNext();){
				temp = it.next();
				if(Utils.convertBytetoString(temp.getFileId()).equals(fileId)){
					removeChunk(address + File.separator + fileId + ".part" + Utils.convertBytetoInt(temp.getChunkNo()));
					it.remove();
				}
			}
			return true;
		}
		return false;
	}

	public ChunkFile findOne(String address, String fileId, int chunkNo){
		if(getChunksList().containsKey(address)){
			ChunkFile temp;
			for(Iterator<ChunkFile> it = getChunksList().get(address).iterator(); it.hasNext();){
				temp = it.next();

				if(Utils.convertBytetoString(temp.getFileId()).equals(fileId) && Utils.convertBytetoInt(temp.getChunkNo()) == chunkNo){
					return temp;
				}
			}
		}
		return null;
	}

	public byte[] file(String address, ChunkFile c){
		File f = new File(address + File.separator + Utils.convertBytetoString(c.getFileId()) + ".part" + Utils.convertBytetoInt(c.getChunkNo()));
		if (f.exists()){
			int fsize = (int) f.length();
			FileInputStream fis;
			try {
				fis = new FileInputStream(address + File.separator + Utils.convertBytetoString(c.getFileId()) + ".part" + Utils.convertBytetoInt(c.getChunkNo()));
				byte[] data = new byte[fsize];
				fis.read(data, 0, fsize);
				return data;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	public void printAllChunks(){
		ChunkFile temp;
		int counter = 0;

		if(getChunksList().isEmpty()){
			System.out.println("You have no chunks stored in your local storage!");
			return;
		}

		System.out.println("\n List of chunks" );
		System.out.println("\n**************************************************");

		for(Map.Entry<String, List<ChunkFile>> entry : getChunksList().entrySet()){
			for(Iterator<ChunkFile> it = entry.getValue().iterator(); it.hasNext();){
				temp = it.next();
				if(Utils.convertBytetoString(temp.getFileId()).length() > 10)
					System.out.printf("%2d - %s...\n", counter, Utils.convertBytetoString(temp.getFileId()).subSequence(0, 10));
				else System.out.printf("%2d - %s\n", counter, Utils.convertBytetoString(temp.getFileId()));
				counter++;		
			}
		}

		System.out.println("\n**************************************************");
		System.out.println( " Listed " + counter + " chunks.");
	}

	public ChunkFile selectChunk(int selection){
		ChunkFile temp = null;
		int counter = 0;

		for(Map.Entry<String, List<ChunkFile>> entry : getChunksList().entrySet()){
			for(Iterator<ChunkFile> it = entry.getValue().iterator(); it.hasNext();){
				temp = it.next();
				if(counter == selection)
					return temp;
				counter++;
			}
		}

		return temp;
	}

	public int getNChunk(){
		ChunkFile temp = null;
		int counter = 0;

		for(Map.Entry<String, List<ChunkFile>> entry : getChunksList().entrySet()){
			for(Iterator<ChunkFile> it = entry.getValue().iterator(); it.hasNext();){
				temp = it.next();
				counter++;
			}
		}
		return counter;
	}

	public String getAdrresforSelection(int selection){
		ChunkFile temp;
		int counter = 0;

		for(Map.Entry<String, List<ChunkFile>> entry : getChunksList().entrySet()){
			for(Iterator<ChunkFile> it = entry.getValue().iterator(); it.hasNext();){
				temp = it.next();
				if(counter == selection)
					return entry.getKey();
				counter++;
			}
		}
		return null;
	}

	private void addChunk(String name, String content) throws IOException{
		FileOutputStream fos;
		fos = new FileOutputStream(new File( name ));
		fos.write( content.getBytes() );
		fos.close();
	}

	private void removeChunk(String filepath){
		File f = new File(filepath);
		if (f.exists())
			f.delete();
	}
}