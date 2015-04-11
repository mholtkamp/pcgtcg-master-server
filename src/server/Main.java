package server;

import java.util.ArrayList;

public class Main {
    
    // Constants
    public static final int MAX_SESSIONS = 64;
    public static final int MAX_GAMES = 32;
	
    // Static variables
    public static Server server;
    public static ArrayList<Session> sessions;
    public static ArrayList<Game>    games;
    
	public static void main(String[] args)
	{
	    sessions = new ArrayList<Session>();
	    games    = new ArrayList<Game>();
	    server   = new Server();
	}
}
