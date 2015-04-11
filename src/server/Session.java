package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Session implements Runnable
{
    public int user;
    public Game game;
    public String name;
    
    public Socket socket;
    public PrintWriter writer;
    public BufferedReader reader;
    public boolean finished;
    
    public Session()
    {
        socket   = null;
        writer   = null;
        reader   = null;
        finished = false;
    }
    
    public void Set(Socket socket)
    {
        try
        {
            this.socket = socket;
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    
    public void run()
    {
        while(!finished)
        {
            try
            {
                String msg = reader.readLine();
                parseMessage(msg);
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
        }
    }
    
    public synchronized void write(String msg)
    {
        writer.println(msg);
    }
    
    public void parseMessage(String msg)
    {
        String[] splitMsg = msg.split("[.]", 2);
        if (splitMsg[0].equals("SERVER"))
        {
            processServerMessage(splitMsg[1]);
        }
    }
    
    public void processServerMessage(String msg)
    {
        String[] splitString;
        String   command;
        String   params;
        String   response;
        
        splitString = msg.split("[.]",2);
        command = splitString[0];
        params  = "none";
        try
        {  
            params  = splitString[1];
        }
        catch(ArrayIndexOutOfBoundsException ex)
        {
            params = "none";
        }
        
        System.out.println("Command: " + command);
        System.out.println("Params: "  + params);
        
        if (command.equals("LOGIN"))
        {
            if (params.equals("none"))
                name = "Player";
            else
                name = params;
            write("R_LOGIN.SUCCESS");
        }
        else if (command.equals("REGISTER"))
        {
            write("R_REGISTER.FAIL");
        }
        else if (command.equals("LIST"))
        {
            response = "R_LIST.";
            
            for (int i = 0; i < Main.games.size(); i++)
            {
                Game game = Main.games.get(i);
                response += "GAME." +
                            game.id        + "." + 
                            game.status    + "." +
                            game.host.name;
            }
        }
        else if (command.equals("CREATE"))
        {
            Game newGame = null;
            ArrayList<Game> games = Main.games;
            if (games.size() < Main.MAX_GAMES)
            {
                newGame = new Game();
                this.game = newGame;
                this.game.setHost(this);
                write("R_CREATE.SUCCESS");
            }
            else
            {
                write("R_CREATE.FAILURE");
            }
        }
        else if (command.equals("JOIN"))
        {
            
        }
        else if (command.equals("SPECTATE"))
        {
            
        }
    }
}
