package protocols;

import java.io.*;
import java.util.*;
import filefunc.*;
import message.Message;


public class RestoreFile extends Ufile
{
    private boolean[] chunkList;
    private String fileId;

    public RestoreFile(Ufile u)
    {
        super(u);
        chunkList = new boolean[super.getNChunks()];
        fileId = sha256();
    }

    public boolean[] getChunkList() { return chunkList; }

    public String getFileId() { return fileId; }

    @Override
    public String toString() {
        return "RestoreFile{" +
                "chunkList=" + Arrays.toString(chunkList) +
                ", fileId='" + fileId + '\'' +
                '}';
    }

    @Override
    public String fileName() {
        return Arrays.toString(chunkList) + " , " +
                fileId;
    }

    public void list(){
    	
        int i = 0;

        System.out.println("\n List of chunks");
        System.out.println(" file   : " + getFileName());
        System.out.println(" fileId : " + getFileId());
		System.out.println("\n**************************************************");

        for (; i < getNChunks() - 1; i++) {
            System.out.printf(" [%s] %2d ~ %d , %d\n", ((chunkList[i]) ? "yes" : ".no"), i, chunkList.length, getPartSize());
        }

        int lastpart = getFileSize() - ((getNChunks() - 1) * getPartSize());
        System.out.printf(" [%s] %2d ~ %d , %d bytes\n", ((chunkList[i]) ? "yes" : ".no"), i, chunkList.length, lastpart);

		System.out.println("\n**************************************************");
        System.out.printf(" Listed %d chunk%s.\n\n", getNChunks(), ((i == 1) ? "" : "s"));
    }


    public boolean isComplete()
    {
        for (int i = 0; i<getNChunks(); i++)
        {
            if ( !chunkList[i] )
            {
                return false;
            }
        }
        return true;
    }

    public void add(Message m)
    {
        int chunkNo = m.header.getChunkNo();
        String name = fileId + File.separator +  fileId + ".part" + chunkNo ;

        try
        {
            addFolder( fileId );
            addChunk( name, m.body.getMessage() );

            if( !chunkList[chunkNo] )
            {
                chunkList[chunkNo] = true;
            }
        }
        catch(FileNotFoundException e)
        {
            e.getMessage();
        }
        catch(IOException e)
        {
            e.getMessage();
        }
    }
}