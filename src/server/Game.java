package server;

public class Game
{
    public static final int WAITING    = 0;
    public static final int INPROGRESS = 1;
    public static final int CLOSED     = 2;

    public static int nextId = 0;
    public Session host;
    public Session client;
    public int status;
    public int id;
    
    
    public Game()
    {
        host   = null;
        client = null;
        id     = nextId;
        status = WAITING;
        nextId++;
    }
    
    public synchronized void setHost(Session host)
    {
        this.host = host;
    }
    
    public synchronized void setClient(Session client)
    {
        this.client = client;
    }

    public void sendHost(String msg)
    {
        host.write(msg);
    }
    
    public void sendClient(String msg)
    {
        client.write(msg);
    }    
}
