package filefunc;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import message.Message;

import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

public class Chunks
{
    // Key é o address remetente
    private Map<String, List<Chunk>> hashmap;

    public Chunks()
    {
        hashmap = new HashMap<String, List<Chunk>>();
    }

    public void add(Message m)
    {
        String ip   = m.getAddress();
        String name = ip + File.separator + m.header.getFileId() + ".part" + m.header.getChunkNo() ;

        try
        {
            addFolder( ip );
            addChunk( name, m.body.getMessage() );

            Chunk c = new Chunk(m);

            if(!hashmap.containsKey( ip ))
            {
                List<Chunk> list= new ArrayList<Chunk>();
                list.add(c);
                hashmap.put(ip,list);
            }
            else
            {
                hashmap.get(ip).add(c);
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

    public boolean remove(String address, String fileId, int chunkNo)
    {
        if(hashmap.containsKey(address))
        {
            Chunk temp;

            for (Iterator<Chunk> it = hashmap.get(address).iterator(); it.hasNext();)
            {
                temp = it.next();

                if (temp.getFileId().equals(fileId) && temp.getChunkNo() == chunkNo)
                {
                    removeChunk(address+File.separator+fileId+".part"+chunkNo);
                    it.remove();
                    return true;
                }
            }
        }

        return false;
    }

    public boolean remove(String address, String fileId)
    {
        if(hashmap.containsKey(address))
        {
            Chunk temp;

            for (Iterator<Chunk> it = hashmap.get(address).iterator(); it.hasNext();)
            {
                temp = it.next();

                if (temp.getFileId().equals(fileId))
                {
                    removeChunk(address+File.separator+fileId+".part"+temp.getChunkNo());
                    it.remove();
                }
            }

            return true;
        }

        return false;
    }

    public boolean remove(String address)
    {
        removeFolder(address);

        if(hashmap.containsKey(address))
        {
            hashmap.remove(address);
        }

        return false;
    }

    public Chunk find(String address, String fileId, int chunkNo)
    {
        if(hashmap.containsKey(address))
        {
            Chunk temp;

            for (Iterator<Chunk> it = hashmap.get(address).iterator(); it.hasNext();)
            {
                temp = it.next();

                if (temp.getFileId().equals(fileId) && temp.getChunkNo() == chunkNo )
                {
                    return temp;
                }
            }
        }

        return null;
    }

    public ArrayList<Chunk> find(String address, String fileId)
    {
        if(hashmap.containsKey(address))
        {
            ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
            Chunk temp;

            for (Iterator<Chunk> it = hashmap.get(address).iterator(); it.hasNext();)
            {
                temp = it.next();

                if (temp.getFileId().equals(fileId) )
                {
                    chunkList.add(temp);
                }
            }

            return chunkList;
        }

        return null;
    }

    public ArrayList<Chunk> find(String address)
    {
        if(hashmap.containsKey(address))
        {
            ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
            Chunk temp;

            for (Iterator<Chunk> it = hashmap.get(address).iterator(); it.hasNext();)
            {
                temp = it.next();
                chunkList.add(temp);
            }

            return chunkList;
        }

        return null;
    }

    public byte[] file(String address, Chunk c) throws IOException
    {
        File f = new File( address + File.separator + c.getFileId() + ".part" + c.getChunkNo() );

        if ( f.exists() )
        {
            int fsize = (int) f.length();
            FileInputStream fis = new FileInputStream( address + File.separator + c.getFileId() + ".part" + c.getChunkNo() );
            byte[] data = new byte[fsize];

            fis.read(data, 0, fsize);

            return data;
        }

        return null;
    }

    public void list()
    {
        Chunk temp;
        int i=0;

        System.out.println("\n List of chunks" );
        System.out.println("==========================================" );

        for (Map.Entry<String, List<Chunk>> entry : hashmap.entrySet())
        {
            System.out.println( " $ " + entry.getKey() );

            for (Iterator<Chunk> it = entry.getValue().iterator(); it.hasNext();)
            {
                temp = it.next();
                i++;
                System.out.printf("     %2d ~ %s\n", i, temp.simple());

                //System.out.println(i + " " + temp.simple() );
            }
        }

        System.out.println("==========================================" );
        System.out.printf( " Listed %d chunk%s.\n", i, ((i==1)?"":"s"));
    }

    private void addFolder(String name) throws FileNotFoundException
    {
        File dir = new File( name );

        if (!dir.exists())
        {
            dir.mkdir();
        }
    }

    private void addChunk(String name, String content) throws IOException
    {
        FileOutputStream fos;

        fos = new FileOutputStream(new File( name ));
        fos.write( content.getBytes() );
    }

    private void removeFolder(String name)
    {
        File dir = new File(name);
        removeDirectory(dir);
    }

    // source from http://www.java2s.com/Tutorial/Java/0180__File/Removeadirectoryandallofitscontents.htm
    private static boolean removeDirectory(File directory)
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

    private void removeChunk(String filepath)
    {
        File f = new File(filepath);
        if ( f.exists() )
        {
            f.delete();
        }
    }


}
