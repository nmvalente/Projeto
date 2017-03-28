package protocols;
import java.io.IOException;
import java.util.Scanner;

import filefunc.*;
import message.Inbox;

import java.io.File;

public class Peer
{
    private String localhost;

    public Inbox  inbox;
    public FileManager  files;
    public Chunks chunks;

    public RestoreFile restoreFile = null;

    public Peer(String localhost)
    {
        this.localhost = localhost;
        inbox  = new Inbox();
        files  = new FileManager();
        chunks = new Chunks();
    }

    public String getLocalhost() { return localhost; }

    public int backup(){
    	
        BackupFile b;

        Scanner in = new Scanner(System.in);

        int index=0,
            desiredReplicationDeg=0,
            i=0,
            count=0,
            nStored=0,
            fsize;

        String msg = null;

        // Lista de ficheiros
        if(files.printAllFilesStored(1) == -1)
        	return -1;

        // receber index
        fsize = files.getFileList().size();
        System.out.printf("\nOption [0" + ((fsize>0)?"-"+Math.max(0,fsize-1):"") + "] > ");        index = in.nextInt();

        // receber desiredReplicationDeg
        System.out.printf("\nReplication Degree [1-9] > ");
        desiredReplicationDeg = in.nextInt();

        // validar index
        if ( index>=0 && index<files.getFileList().size() )
        {
            // ficheiro não pode estar em backup
            if ( !(files.getFileList().get(index) instanceof BackupFile) )
            {
                b = files.backup(index, desiredReplicationDeg);

                try {

                    System.out.println(" Receiving chunk backup confirmation");

                    for (i=0; i < b.getNChunks(); i++)
                    {
                		System.out.println("\n**************************************************");

                        System.out.println("> Waiting for next STORED reply" );

                        count=0;
                        nStored=0;

                        msg = new String(b.file(i), "utf-8");

                        do
                        {
                            inbox.newRequest("PUTCHUNK","1.0",b.getFileId(),i,desiredReplicationDeg,msg);

                            //inbox.list(0);

                            try
                            {
                                System.out.printf(" Try #%d. Sleeping for %4d ms.", count+1, 500 * ((2 * count == 0) ? 1 : 2 * count));
                                Thread.sleep(500 * ((2 * count == 0) ? 1 : 2 * count));
                                nStored = b.getNSTORED(i);
                                System.out.printf(" CHUNK #%d with %d/%d STORED.\n", i, nStored, desiredReplicationDeg);

                            }
                            catch(InterruptedException e)
                            {
                                e.getMessage();
                            }

                            count++;
                            nStored = b.getNSTORED(i);

                        } while( count<5 && nStored<desiredReplicationDeg );
                    }
            		System.out.println("\n**************************************************");
                    System.out.println(" File backup finished. " + ((b.isBackupReplicatedEnough()) ? "Successful" : "Incomplete") + ".\n");

                    b.list();

                }
                catch(IOException e)
                {
                    e.getMessage();
                }

            }
            else // ficheiro já está em backup
            {
                System.out.println(" File is already in backup.");
            }
        }
        else // index invalido
        {
            System.out.println(" Invalid file index.");
        }
        
        return 0;
    }

    public int restore(){
    	
        BackupFile b;

        Scanner in = new Scanner(System.in);

        int     index=0,
                i=0,
                count=0,
                fsize;
        String msg = null;

        // Lista de ficheiros
        if(files.printAllFilesStored(2) == -1)
        	return -1;

        // receber index
        fsize = files.getFileList().size();
        System.out.println("\n Option [0" + ((fsize>0)?"-"+Math.max(0,fsize-1):"") + "] ~ ");

        index = in.nextInt();

        // validar index
        if ( index>=0 && index<files.getFileList().size() )
        {
            // ficheiro tem de estar em backup
            if ( files.getFileList().get(index) instanceof BackupFile )
            {
                b = (BackupFile) files.getFileList().get(index);
                restoreFile = new RestoreFile( (Ufile) b );

                for (i=0; i < b.getNChunks(); i++)
                {
                    inbox.newRequest("GETCHUNK","1.0",b.getFileId(),i,1,"");
                }

                System.out.println(" Receiving chunk restore information");
        		System.out.println("\n**************************************************");

                do
                {
                    try
                    {
                        System.out.printf(" Try #%d. Sleeping for %4d ms.", count + 1, 400 * ((2 * count == 0) ? 1 : 2 * count));
                        Thread.sleep(400);
                        System.out.printf(" %s.\n", ((restoreFile.isComplete())?" Complete":"Incomplete") );
                    }
                    catch(InterruptedException e)
                    {
                        e.getMessage();
                    }

                    count++;

                } while( count<5 && !restoreFile.isComplete() );

        		System.out.println("\n**************************************************");
                System.out.println(" File restore finished. " + ((restoreFile.isComplete()) ? "Successful" : "Incomplete") + ".\n");

                restoreFile.list();

                if ( restoreFile.isComplete() )
                {
                    restoreFile.merge();
                    restoreFile.removeDirectory( new File(b.getFileId()) );
                    restoreFile = null;
                }

            }
            else // ficheiro nãi está em backup
            {
                System.out.println(" File is not in backup.");
            }
        }
        else // index invalido
        {
            System.out.println(" Invalid file index.");
        }
        
        return 0;
    }

    public int delete(){
    	
        BackupFile b;

        Scanner in = new Scanner(System.in);

        int index=0,
                i=0,
                fsize;
        String msg = null;

        // Lista de ficheiros
        if(files.printAllFilesStored(3) == -1)
        	return -1;

        // receber index
        fsize = files.getFileList().size();
        System.out.println("\n Option [0" + ((fsize>0)?"-"+(fsize-1):"") + "] ~ ");
        index = in.nextInt();

        // validar index
        if ( index>=0 && index<files.getFileList().size() )
        {
            // ficheiro tem de estar em backup
            if ( files.getFileList().get(index) instanceof BackupFile )
            {
                b = (BackupFile) files.getFileList().get(index);

                inbox.newRequest("DELETE", "1.0",  b.getFileId(), i, 0, "");

                // remover fisico
                b.removeFile(b.getFileName());

                // remover lógico
                files.getFileList().remove(index);

                System.out.println("File delete Successful.");
            }
            else // ficheiro nãi está em backup
            {
                System.out.println("File is not in backup.");
            }
        }
        else // index invalido
        {
            System.out.println("Invalid file index.");
        }
        return 0;
    }

}
