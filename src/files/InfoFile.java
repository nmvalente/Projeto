package files;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class InfoFile{
	
	private static final String HASH_TYPE = "SHA-256";
	private static final int CHUNK_MAX_SIZE = 64000;
	private String fileName;
	private int fileSize;
	private int numberOfChunks;
	private File file = null;

	public InfoFile(String fileName){
		this.fileName = fileName;
		file = new File(fileName);
		fileSize = (int) file.length();
		numberOfChunks  = (int) Math.ceil(fileSize / Math.max(1.0, CHUNK_MAX_SIZE));
		//nChunks  = (int) Math.ceil(fileSize/CHUNK_MAX_SIZE);
	}

	public InfoFile(InfoFile file){
		this.fileName = file.getFileName();
		this.fileSize = file.getFileSize();
		this.numberOfChunks  = file.getNChunks();
	}

	public String getFileName(){return fileName;}

	public int getPartSize(){return CHUNK_MAX_SIZE;}

	public int getFileSize(){return fileSize;}

	public int getNChunks(){return numberOfChunks;}

	public File getFile() {return file;}

	public void splitFile(){
		FileInputStream inStream;
		String newFileName, fileId=hashFileId();
		FileOutputStream outStream;
		int i, chunkNo = 0, read = 0, readLength = CHUNK_MAX_SIZE, currentFileSize = fileSize;
		byte[] chunkPart;

		try{
			inStream = new FileInputStream(file);

			System.out.println("\n File partitioned");
			System.out.println(" file   : " + getFileName() );
			System.out.println(" fileId : " + fileId.substring(0, 22) );
			System.out.println("******************************");

			for (i = 0; currentFileSize > 0; i++, chunkNo++){
				readLength = Math.min(currentFileSize, CHUNK_MAX_SIZE);

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
		String fileId = hashFileId();
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

	public String hashFileId(){
		String hashname = null;
		try{
			hashname = convertHashToString(convertStringToHash(fileName));
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return hashname;
	}

	private byte[] convertStringToHash(String msg) throws NoSuchAlgorithmException{
		
		MessageDigest digest = MessageDigest.getInstance(HASH_TYPE);
		byte[] hash = digest.digest(msg.getBytes(StandardCharsets.UTF_8));
		return hash;
		
		/*MessageDigest digest = MessageDigest.getInstance(HASH_TYPE);
		byte[] inputBytes = msg.getBytes();
		byte[] hashBytes = digest.digest(inputBytes);
		return hashBytes;
		*/
	}

	private String convertHashToString(byte[] bytes) throws NoSuchAlgorithmException{
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}
	
	protected void addChunk(String name, String content) throws IOException{
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

	public void printHeadList(String id){
		System.out.println("\n List of chunks");
		System.out.println(" file   : " + getFileName());
		System.out.println(" fileId : " + id.substring(0, 22));
		System.out.println("\n**************************************************");
	}

	public void printTailList(int i){
		System.out.println("\n**************************************************");
		System.out.println( " List" + i + " chunks.\n");
	}
}
