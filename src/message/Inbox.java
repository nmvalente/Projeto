package message;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

public class Inbox
{
    private static final int MAXREAD = 20;

    private Queue<Message> unread;
    private Queue<Message> read;

    public Inbox()
    {
        unread = new LinkedList<Message>();
        read   = new LinkedList<Message>();
    }

    public boolean hasNewMessage() { return unread.size() > 0; }

    public Message getNewMessage()
    {
        if (unread.size() > 0)
        {
            return unread.element();
        }

        return null;
    }

    public void setAsRead()
    {
        if (unread.size() > 0)
        {
            read.add( unread.element() );
            unread.remove();

            if ( read.size() > MAXREAD )
            {
                read.remove();
            }
        }
    }

    public String newMessage(String address, int port, String s)
    {
        Message m=null;

        try
        {
            m = new Message(address.substring(1),port,s);

            unread.add(m);

            return m.simple();
        }
        catch (IllegalArgumentException e)
        {
            e.getMessage();
        }

        return "Error in newMessage";
    }

    public String newRequest(String msgtype, String version, String fileId, int chunkNo, int repl, String msg)
    {
        Message m=null;

        try
        {
            m = new Message(msgtype, version, fileId, chunkNo, repl, msg);

            unread.add(m);

            return m.simple();
        }
        catch (IllegalArgumentException e)
        {
            e.getMessage();
        }

        return "Error in newRequest";
    }

    public void list(int c)
    {
        Message temp;
        int i=0;

        System.out.println("\n List of Messages " + ((c==0)?"Unread":"Read"));
        System.out.println("==========================================" );

        for (Iterator<Message> it = ((c==0)?unread:read).iterator(); it.hasNext();)
        {
            temp = it.next();
            i++;
            System.out.printf("%2d ~ %s\n", i, temp.simple());
        }

        System.out.println("==========================================" );
        System.out.printf( " Listed %d message%s.\n", i, ((i==1)?"":"s"));
    }

    public int nUnreadMessages()
    {
        return unread.size();
    }

}
