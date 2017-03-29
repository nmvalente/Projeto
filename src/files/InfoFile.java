package files;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class InfoFile
{
    private static final int CHUNK_MAX_SIZE = 64000;
    private String fileName;
    private int fileSize;
    private int nChunks;
    private File file = null;

    public InfoFile(String fileName){
        this.fileName = fileName;
        file = new File(fileName);
        fileSize = (int) file.length();
        nChunks  = (int) Math.ceil(fileSize / Math.max(1.0, CHUNK_MAX_SIZE * 1.0));
        //nChunks  = (int) Math.ceil(fileSize/CHUNK_MAX_SIZE);
    }

    public InfoFile(InfoFile file){
        this.fileName = file.getFileName();
        this.fileSize = file.getFileSize();
        this.nChunks  = file.getNChunks();
    }

    public String getFileName(){return fileName;}

    public int getPartSize(){return CHUNK_MAX_SIZE;}

    public int getFileSize(){return fileSize;}

    public int getNChunks(){return nChunks;}

    public File getFile() {return file;}

    @Override
    public String toString() {
        return "InfoFile{" +
                "fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", nChunks=" + nChunks +
                ", file=" + file +
                '}';
    }

    public void split()
    {
        FileInputStream inputStream;
        String newFileName, fileId=sha256();
        FileOutputStream filePart;

        int     i,
                chunkNo = 0,
                read = 0,
                readLength = CHUNK_MAX_SIZE,
                currentFileSize = fileSize;

        byte[] byteChunkPart;

        try
        {
            inputStream = new FileInputStream(file);

            System.out.println("\n File Split" );
            System.out.println(" file   : " + getFileName() );
            System.out.println(" fileId : " + fileId );
    		System.out.println("******************************");

            for (i = 0; currentFileSize > 0; i++, chunkNo++)
            {
                readLength = Math.min(currentFileSize, CHUNK_MAX_SIZE);

                // System.out.println(" " + currentFileSize + " / " + readLength);

                byteChunkPart = new byte[readLength];

                read = inputStream.read(byteChunkPart, 0, readLength);
                currentFileSize -= read;

                assert (read == byteChunkPart.length);

                // sha256.partX - Sha-256
                newFileName = fileId + ".part" + chunkNo;

                System.out.printf("%2d ~ %s , %d bytes\n", i, newFileName, readLength);

                filePart = new FileOutputStream(new File(newFileName));
                filePart.write(byteChunkPart);
                filePart.flush();
                filePart.close();

            }

    		System.out.println("******************************");
            System.out.printf(" File split into %d chunk%s.\n\n", i, ((i == 1) ? "" : "s"));

            inputStream.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void merge()
    {
        File ofile = new File(fileName);

        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead = 0;

        List<File> list = new ArrayList<File>();

        String fileId = sha256();

        for (int i = 0; i<nChunks; i++)
        {
            list.add(new File( fileId + File.separator + fileId + ".part" + i));
        }

        try
        {
            fos = new FileOutputStream(ofile,true);

            for (File file : list)
            {
                fis = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fis.read(fileBytes, 0,(int)  file.length());
                assert(bytesRead == fileBytes.length);
                assert(bytesRead == (int) file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }

            fos.close();
            fos = null;
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public String sha256()
    {
        String hashname = null;

        try
        {
            hashname = Hash2String(String2Hash(fileName));
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return hashname;
    }

    /*
	 * source from http://www.mkyong.com/java/java-sha-hashing-example/
	 */
    private byte[] String2Hash(String msg) throws NoSuchAlgorithmException
    {
        // algorithm can be "MD5", "SHA-1", "SHA-256"
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] inputBytes = msg.getBytes(); // get bytes array from message
        byte[] hashBytes = digest.digest(inputBytes);
        return hashBytes; // convert hash bytes to string (usually in hexadecimal form)
    }

    private String Hash2String(byte[] bytes) throws NoSuchAlgorithmException
    {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }


    public void addFolder(String name) throws FileNotFoundException
    {
        File dir = new File( name );

        if (!dir.exists())
        {
            dir.mkdir();
        }
    }

    public void addChunk(String name, String content) throws IOException
    {
        FileOutputStream fos;

        fos = new FileOutputStream(new File( name ));
        fos.write(content.getBytes() );
    }

    // removi static
    public boolean removeDirectory(File directory)
    {
        // System.out.println("removeDirectory " + directory);

        if (directory == null)
            return false;
        if (!directory.exists())
            return true;
        if (!directory.isDirectory())
            return false;

        String[] list = directory.list();

        // Some JVMs return null for File.list() when the
        // directory is empty.
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File entry = new File(directory, list[i]);

                //        System.out.println("\tremoving entry " + entry);

                if (entry.isDirectory())
                {
                    if (!removeDirectory(entry))
                        return false;
                }
                else
                {
                    if (!entry.delete())
                        return false;
                }
            }
        }

        return directory.delete();
    }

    public void removeFile(String filepath)
    {
        File f = new File(filepath);
        if ( f.exists() )
        {
            f.delete();
        }
    }
    
    public void printHeadList(String id){
    	System.out.println("\n List of chunks");
		System.out.println(" file   : " + getFileName());
		System.out.println(" fileId : " + id);
		System.out.println("\n**************************************************");
    }
    
    public void printTailList(int i){
    	System.out.println("\n**************************************************");
		System.out.printf( " Listed %d chunk%s.\n\n", i, ((i==1)?"":"s"));
    }
}
