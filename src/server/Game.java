package server;

public class Game
{
    public static enum GameStatus
    {
        WAITING,
        INPROGRESS,
        CLOSED
    }
    public static int nextId = 0;
    public Session host;
    public Session client;
    public GameStatus status;
    public int id;
    
    
    public Game()
    {
        host   = null;
        client = null;
        id     = nextId;
        status = GameStatus.WAITING;
        nextId++;
    }
    
    public void setHost(Session host)
    {
        this.host = host;
    }
    
    public void setClient(Session client)
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
