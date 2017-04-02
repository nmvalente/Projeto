package files;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

import message.Message;
import utils.Utils;

public class Chunks{

	private Map<String, List<ChunkFile>> hashmap;

	public Chunks(){
		hashmap = new HashMap<String, List<ChunkFile>>();
	}

	public Map<String, List<ChunkFile>> getChunksList(){
		return hashmap;
	}

	public void add(Message m){
		String ip   = m.getAddress();
		String name = ip + File.separator + Utils.convertBytetoString(m.getHeader().getFileId()) + ".part" + Utils.convertBytetoInt(m.getHeader().getChunkNo()) ;

		try{

			File dir = new File(ip);
			if (!dir.exists())
				dir.mkdir();

			addChunk( name, Utils.convertBytetoString(m.getBody().getBody()));

			ChunkFile c = new ChunkFile(m);

			if(!hashmap.containsKey(ip)){
				List<ChunkFile> list= new ArrayList<ChunkFile>();
				list.add(c);
				hashmap.put(ip,list);
			}
			else{ //////
				boolean v = false;
				for(ChunkFile elem : hashmap.get(ip)){
					if(elem.equals(c)){
						v = true;
					}
				}
				if(!v){
					hashmap.get(ip).add(c);
					System.out.print("\n\n Adiciona" + c);
					System.out.print("\n\n Lista" + hashmap.get(ip));
				}
			}

		}
		catch(FileNotFoundException e){e.getMessage();}
		catch(IOException e){e.getMessage();}
	}

	public boolean removeOne(String address, String fileId, int chunkNo){
		if(hashmap.containsKey(address)){
			ChunkFile temp;
			for (Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
				temp = it.next();
				if (Utils.convertBytetoString(temp.getFileId()) == fileId && Utils.convertBytetoInt(temp.getChunkNo()) == chunkNo){
					removeChunk(address+File.separator+fileId+".part"+chunkNo);
					it.remove();
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeAll(String address, String fileId){
		if(hashmap.containsKey(address)){
			ChunkFile temp;
			for(Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
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

	/*public boolean remove(String address){
		removeFolder(address);
		if(hashmap.containsKey(address)){
			hashmap.remove(address);
		}
		return false;
	}
	 */

	public ChunkFile findOne(String address, String fileId, int chunkNo){
		if(hashmap.containsKey(address)){
			ChunkFile temp;
			for(Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
				temp = it.next();
				if(Utils.convertBytetoString(temp.getFileId()) == fileId && Utils.convertBytetoInt(temp.getChunkNo()) == chunkNo){
					return temp;
				}
			}
		}
		return null;
	}

	public ArrayList<ChunkFile> findAll(String address, String fileId){
		if(hashmap.containsKey(address)){
			ArrayList<ChunkFile> chunkList = new ArrayList<ChunkFile>();
			ChunkFile temp;
			for (Iterator<ChunkFile> it = hashmap.get(address).iterator(); it.hasNext();){
				temp = it.next();
				if (Utils.convertBytetoString(temp.getFileId()) == fileId){
					chunkList.add(temp);
				}
			}
			return chunkList;
		}
		return null;
	}

	/*public ArrayList<ChunkFile> find(String address){
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
	 */
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
				System.out.printf("     %2d ~ %s\n", counter, temp.toString());
				counter++;
				//System.out.println(i + " " + temp.simple() );
			}
		}

		System.out.println("==========================================" );
		System.out.println( " Listed " + counter + " chunks.");
	}

	public ChunkFile selectChunk(int selection){
		ChunkFile temp = null;
		int counter = 0;

		for(Map.Entry<String, List<ChunkFile>> entry : hashmap.entrySet()){
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

		for(Map.Entry<String, List<ChunkFile>> entry : hashmap.entrySet()){
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

		for(Map.Entry<String, List<ChunkFile>> entry : hashmap.entrySet()){
			for(Iterator<ChunkFile> it = entry.getValue().iterator(); it.hasNext();){
				temp = it.next();
				if(counter == selection)
					return entry.getKey();
				counter++;
				//System.out.println(i + " " + temp.simple() );
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

	/*private void removeFolder(String name){
		File dir = new File(name);
		removeDirectory(dir);
	}

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
	 */
	private void removeChunk(String filepath){
		System.out.println(filepath);
		File f = new File(filepath);
		if (f.exists())
			f.delete();
	}
}