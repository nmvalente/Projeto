package files;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import protocols.Peer;
import utils.Utils;

public class InfoFile{


	private String fileName;
	private int fileSize;
	private int numberOfChunks;
	private File file = null;

	public InfoFile(String fileName){
		this.fileName = fileName;
		file = new File(fileName);
		fileSize = (int) file.length();
		numberOfChunks  = (int) Math.ceil(fileSize / Math.max(1.0, Utils.CHUNK_MAX_SIZE));
	}

	public InfoFile(InfoFile file){
		this.fileName = file.getFileName();
		this.fileSize = file.getFileSize();
		this.numberOfChunks  = file.getNumberChunks();
		this.file = file.getFile();
	}

	// Get methods

	public String getFileName(){return fileName;}
	public int getPartSize(){return Utils.CHUNK_MAX_SIZE;}
	public int getFileSize(){return fileSize;}
	public int getNumberChunks(){return numberOfChunks;}
	public File getFile() {return file;}

	public void splitFile(Peer peer){
		FileInputStream inStream;
		String newFileName, fileId=Utils.hashFileId(this.fileName);
		FileOutputStream outStream;
		int i, chunkNo = 0, read = 0, readLength = Utils.CHUNK_MAX_SIZE, currentFileSize = fileSize;
		byte[] chunkPart;

		try{
			inStream = new FileInputStream(file);

			System.out.println("\n File partitioned");
			System.out.println(" file   : " + getFileName() );
			System.out.println(" fileId : " + fileId.substring(0, Utils.WIDTH_SIZE) );
			System.out.println("******************************");

			for (i = 0; currentFileSize > 0; i++, chunkNo++){
				readLength = Math.min(currentFileSize, Utils.CHUNK_MAX_SIZE);

				chunkPart = new byte[readLength];

				read = inStream.read(chunkPart, 0, readLength);
				currentFileSize -= read;

				//assert (read == chunkPart.length);

				newFileName = fileId + ".part" + chunkNo;

				System.out.printf("%2d ~ %s , %d bytes\n", i, newFileName, readLength);
				
				outStream = new FileOutputStream(new File(newFileName));
				outStream.write(chunkPart);
				outStream.flush();

				try{if(outStream != null) outStream.close();}catch (Exception e) {}
			}
			System.out.println("******************************");
			System.out.println(" File split into" + i + " chunks.\n");
			try{
				if(inStream != null)
					inStream.close();
			}catch (Exception e) {}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public void merge() throws IOException{	
		File aux_file = new File(fileName);

		FileOutputStream fOutStream = new FileOutputStream(aux_file,true);
		FileInputStream fInStream;
		byte[] fileBytes;

		List<File> list = new ArrayList<File>();
		String fileId = Utils.hashFileId(this.fileName);
		for (int i = 0 ; i < numberOfChunks ; i++){
			list.add(new File( fileId + File.separator + fileId + ".part" + i));
		}
		for (File file : list){
			fInStream = new FileInputStream(file);
			fileBytes = new byte[(int) file.length()];
			fInStream.read(fileBytes, 0,(int) file.length());
			fOutStream.write(fileBytes);
			fOutStream.flush();
			fileBytes = null;
			fInStream.close();
			fInStream = null;
		}
		fOutStream.close();
		fOutStream = null;
	}



	protected void writeChunk(String name, String content) throws IOException{
		FileOutputStream fos;
		fos = new FileOutputStream(new File(name));
		fos.write(content.getBytes());
		fos.close();
	}

	// removi static
	public boolean deleteDirectory(File directory){
		if((directory == null) || (!directory.exists()) || (!directory.isDirectory()))
			return false;
		String[] list = null;
		try{list = directory.list();}
		catch (Exception e) {System.err.println("Error in list the directory");}
		for (int i = 0 ; i < list.length ; i++) {
			File entry = new File(directory, list[i]);
			if (entry.isDirectory())
				if (!deleteDirectory(entry))
					return false;
				else{
					if(!entry.delete())
						return false;
				}
		}
		return directory.delete();
	}

	public void deleteFile(String pathName){
		File f = new File(pathName);
		if (f.exists()){
			f.delete();
		}
	}

	public void displayBackupChunks(){}

	public void printHeadList(String id){
		System.out.println("\n List of chunks");
		System.out.println(" file   : " + getFileName());
		System.out.println(" fileId : " + id.substring(0, Utils.WIDTH_SIZE));
		System.out.println("\n**************************************************");
	}

	public void printTailList(int i){
		System.out.println("\n**************************************************");
		System.out.println( " List" + i + " chunks.\n");
	}
}
