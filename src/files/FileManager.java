package files;

import java.util.ArrayList;
import java.io.File;

public class FileManager{

	private ArrayList<InfoFile> fileList;

	public FileManager(){
		fileList = new ArrayList<InfoFile>();
	}

	public ArrayList<InfoFile> getFileList() {return fileList;}

	public int getNumberOfFiles(){return fileList.size();}

	public boolean add(String pathfile){
		File f = new File(pathfile);
		if (!f.isFile()){
			return false;
		}
		InfoFile info = new InfoFile(pathfile);
		fileList.add(info);
		return true;
	}

	public void getAllFilesFromStorage(String allOrRegular){
		File folder = new File(".");
		File[] listOfFiles = folder.listFiles();

		for(int i = 0; i < listOfFiles.length; i++){ 
			if(listOfFiles[i].isFile()){
				if(allOrRegular.equals("all"))
					add(listOfFiles[i].getName());
				else if(allOrRegular.equals("regular") && !(listOfFiles[i].getName().contains(".part"))){
					add(listOfFiles[i].getName());
				}
			}
		}
	}

	public String rightpad(String text, int length) {
		return String.format("%-" + length + "." + length + "s", text);
	}

	public int printAllFilesStored(){

		InfoFile temp;
		int i=0;

		System.out.println("\n**************************************************");
		if(!fileList.isEmpty()){
			System.out.println("-> Files Stored : \n");

			for(i = 0 ; i < fileList.size() ; i++){
				temp = fileList.get(i);

				if(i == 0)
					System.out.println("Number  Name\t\t\t\tSize - bytes");

				System.out.printf("%d\t", i);

				if(temp.getFileName().length() > 22)
					System.out.printf("%s...", temp.getFileName().subSequence(0, 22));
				else System.out.printf("%25s", rightpad(temp.getFileName(), 25));

				System.out.printf("\t%d", temp.getFileSize());  

				if(temp instanceof BackupFile)
					System.out.print("[backup]");
				System.out.println("");

			}
			if(i == 1)
				System.out.println("\nTotal: " + i + " file");
			else
				System.out.println("\nTotal: " + i + " files");
		}else{

			System.out.printf("\nNo files in current directory!\n");
			System.out.println("\n**************************************************");
			return -1;
		}

		System.out.println("\n**************************************************");
		return 0;
	}

	public void stored(String address, String fileId, int chunkNo, String addOrRemove){
		InfoFile temp;
		BackupFile backupFile;

		int i;
		for(i = 0 ; i < fileList.size() ; i++){
			temp = fileList.get(i);
			if(temp instanceof BackupFile){
				if(((BackupFile) temp).getFileId().equals(fileId)){
					backupFile = (BackupFile) temp;
					if(addOrRemove.equals("add"))
						backupFile.getListChunks().get(chunkNo).add(address);
					else if(addOrRemove.equals("remove"))
						backupFile.removeAddressOfChunk(chunkNo,address);
					return;
				}
			}
		}
	}
}