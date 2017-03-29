package files;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.File;

public class FileManager
{
	private ArrayList<InfoFile> fileList;

	public FileManager()
	{
		fileList = new ArrayList<InfoFile>();
	}

	public ArrayList<InfoFile> getFileList() { return fileList; }

	public int getNumberOfFiles(){return fileList.size();}
	
	public void add(InfoFile u)
	{
		fileList.add(u);
	}

	public boolean add(String pathfile)
	{
		File f = new File(pathfile);
		if ( !f.isFile() )
		{
			return false;
		}

		InfoFile u = new InfoFile(pathfile);
		add(u);

		return true;
	}

	public boolean remove(String fileName)
	{
		InfoFile temp;

		for (Iterator<InfoFile> it = fileList.iterator(); it.hasNext();)
		{
			temp = it.next();

			if (temp.getFileName().equals(fileName))
			{
				it.remove();
				return true;
			}
		}

		return false;
	}

	public InfoFile find(String fileName)
	{
		InfoFile temp;

		for (Iterator<InfoFile> it = fileList.iterator(); it.hasNext();)
		{
			temp = it.next();

			if (temp.getFileName().equals(fileName))
			{
				return temp;
			}
		}

		return null;
	}

	public void getAllFilesFromStorage()
	{
		File folder = new File(".");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++)
		{ 
			if (listOfFiles[i].isFile())
			{
				add(listOfFiles[i].getName());
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

			System.out.printf("-> Files Stored : \n");

			for (Iterator<InfoFile> it = fileList.iterator(); it.hasNext(); i++){
				temp = it.next();
				if(i == 0)
					System.out.println("Number  Name\t\t\t\tSize - bytes");


				System.out.printf("%d\t", i);

				if(temp.getFileName().length() > 22)
					System.out.printf("%s...", temp.getFileName().subSequence(0, 22));
				else System.out.printf("%25s", rightpad(temp.getFileName(), 25));

				System.out.printf("\t%d", temp.getFileSize());  

				if(temp instanceof BackupFile)
					System.out.printf("[backup]");
				System.out.println("");

			}
			if(i == 1)
				System.out.printf("\nTotal: %d file\n", i);
			else
				System.out.printf("\nTotal: %d files\n", i);
		}else{

			System.out.printf("\nNo files in current directory!\n");
			System.out.println("\n**************************************************");
			return -1;
		}

		System.out.println("\n**************************************************");
		return 0;
	}

	public void addSTORED(String address, String fileId, int chunkNo)
	{
		InfoFile temp;
		BackupFile b;

		for (Iterator<InfoFile> it = fileList.iterator(); it.hasNext();)
		{
			temp = it.next();

			if ( temp instanceof BackupFile )
			{
				if ( ((BackupFile) temp).getFileId().equals(fileId) )
				{
					b = (BackupFile) temp;
					b.addAddressOfChunk(chunkNo,address);

					return;
				}

			}
		}
	}

	public void removeSTORED(String address, String fileId, int chunkNo)
	{
		InfoFile temp;
		BackupFile b;

		for (Iterator<InfoFile> it = fileList.iterator(); it.hasNext();)
		{
			temp = it.next();

			if ( temp instanceof BackupFile )
			{
				if ( ((BackupFile) temp).getFileId().equals(fileId) )
				{
					b = (BackupFile) temp;
					b.removeAddressOfChunk(chunkNo,address);

					return;
				}
			}
		}
	}

	public BackupFile backup(int fileIndex, int senderID, int desiredReplicationDeg)
	{
		InfoFile u  = fileList.get(fileIndex);
		BackupFile b = new BackupFile(u.getFileName(), senderID ,desiredReplicationDeg);

		fileList.set(fileIndex, b);

		b.split();

		return b;
	} 	
}
