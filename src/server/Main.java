package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    
    // Constants
    public static final int MAX_SESSIONS = 64;
    public static final int MAX_GAMES = 32;
	
    // Static variables
    public static ServerSocket serverSocket;
    
    public static Object             lock;
    public static ArrayList<Session> sessions;
    public static ArrayList<Game>    games;
    public static Cleaner            cleaner;
    
	public static void main(String[] args)
	{
	    lock     = new Object();
	    sessions = new ArrayList<Session>();
	    games    = new ArrayList<Game>();
	    cleaner  = new Cleaner();
	    
	    try
	    {
	        (new Thread(cleaner)).start();
	        serverSocket = new ServerSocket(2000);
	        
	        while (true)
	        {
	            if (sessions.size() >= MAX_SESSIONS)
	            {
	                Thread.sleep(100);
	                continue;
	            }
                Socket newSocket = serverSocket.accept();
                Session newSession = new Session();
                newSession.Set(newSocket);
                
                // Lock for sessions
                synchronized(lock)
                {
                    sessions.add(newSession);
                }
                
                // Kick off thread.
                Thread newThread = new Thread(newSession);
                newThread.start();
	        }
	    }
	    catch (Exception ex)
	    {
	        System.out.println(ex);
	    }
	}
}
