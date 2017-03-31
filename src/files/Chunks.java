package files;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import message.Message;

import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

public class Chunks
{
	private Map<String, List<ChunkFile>> hashmap;

	public Chunks(){
		hashmap = new HashMap<String, List<ChunkFile>>();
	}

	public void add(Message m){
		String ip   = m.getAddress();
		String name = ip + File.separator + m.header.getFileId() + ".part" + m.header.getChunkNo() ;

		try{
			addFolder( ip );
			addChunk( name, m.body.getMessage() );

			ChunkFile c = new ChunkFile(m);

			if(!hashmap.containsKey(ip)){
				List<ChunkFile> list= new ArrayList<ChunkFile>();
				list.add(c);
				hashmap.put(ip,list);
			}
			else hashmap.get(ip).add(c);
		}
		catch(FileNotFoundException e){e.getMessage();}
		catch(IOException e){e.getMessage();}
	}

	public boolean remove(String address, String fileId, int chunkNo){
		if(hashmap.containsKey(address)){
			ChunkFile temp;
			for (Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
				temp = it.next();
				if (temp.getFileId() == fileId && temp.getChunkNo() == chunkNo){
					removeChunk(address+File.separator+fileId+".part"+chunkNo);
					it.remove();
					return true;
				}
			}
		}
		return false;
	}

	public boolean remove(String address, String fileId){
		if(hashmap.containsKey(address)){
			ChunkFile temp;
			for(Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
				temp = it.next();
				if(temp.getFileId() == fileId){
					removeChunk(address + File.separator + fileId + ".part" + temp.getChunkNo());
					it.remove();
				}
			}
			return true;
		}
		return false;
	}

	public boolean remove(String address){
		removeFolder(address);
		if(hashmap.containsKey(address)){
			hashmap.remove(address);
		}
		return false;
	}

	public ChunkFile find(String address, String fileId, int chunkNo){
		if(hashmap.containsKey(address)){
			ChunkFile temp;
			for(Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
				temp = it.next();
				if(temp.getFileId() == fileId && temp.getChunkNo() == chunkNo){
					return temp;
				}
			}
		}
		return null;
	}

	public ArrayList<ChunkFile> find(String address, String fileId){
		if(hashmap.containsKey(address)){
			ArrayList<ChunkFile> chunkList = new ArrayList<ChunkFile>();
			ChunkFile temp;
			for (Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
				temp = it.next();
				if (temp.getFileId() == fileId){
					chunkList.add(temp);
				}
			}
			return chunkList;
		}
		return null;
	}

	public ArrayList<ChunkFile> find(String address){
		if(hashmap.containsKey(address)){
			ArrayList<ChunkFile> chunkList = new ArrayList<ChunkFile>();
			ChunkFile temp;
			for (Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
				temp = it.next();
				chunkList.add(temp);
			}
			return chunkList;
		}
		return null;
	}

	public byte[] file(String address, ChunkFile c) throws IOException{
		File f = new File(address + File.separator + c.getFileId() + ".part" + c.getChunkNo());
		if (f.exists()){
			int fsize = (int) f.length();
			FileInputStream fis = new FileInputStream(address + File.separator + c.getFileId() + ".part" + c.getChunkNo());
			byte[] data = new byte[fsize];
			fis.read(data, 0, fsize);
			return data;
		}
		return null;
	}

	public void list(){
		ChunkFile temp;
		int counter = 0;

		System.out.println("\n List of chunks" );
		System.out.println("==========================================" );

		for(Map.Entry<String, List<ChunkFile>> entry : hashmap.entrySet()){
			System.out.println(" $ " + entry.getKey());

			for(Iterator<ChunkFile> it = entry.getValue().iterator(); it.hasNext();){
				temp = it.next();
				counter++;
				System.out.printf("     %2d ~ %s\n", counter, temp.toString());
				//System.out.println(i + " " + temp.simple() );
			}
		}

		System.out.println("==========================================" );
		System.out.println( " Listed " + counter + " chunks.");
	}

	private void addFolder(String name) throws FileNotFoundException{
		File dir = new File(name);
		if (!dir.exists())
			dir.mkdir();
	}

	private void addChunk(String name, String content) throws IOException{
		FileOutputStream fos;
		fos = new FileOutputStream(new File( name ));
		fos.write( content.getBytes() );
	}

	private void removeFolder(String name){
		File dir = new File(name);
		removeDirectory(dir);
	}

	// source from http://www.java2s.com/Tutorial/Java/0180__File/Removeadirectoryandallofitscontents.htm
	private static boolean removeDirectory(File directory){
		if(directory == null | !directory.exists() | !directory.isDirectory())
			return false;

		String[] list = directory.list();

		if(list != null) {
			for(int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);
				if(entry.isDirectory()){
					if(!removeDirectory(entry))
						return false;
				}
				else{
					if(!entry.delete())
						return false;
				}
			}
		}
		return directory.delete();
	}

	private void removeChunk(String filepath){
		File f = new File(filepath);
		if (f.exists())
			f.delete();
	}
}