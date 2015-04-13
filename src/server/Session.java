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
    public boolean isHost;
    
    public Session()
    {
        socket   = null;
        writer   = null;
        reader   = null;
        finished = false;
        isHost   = false;
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
                finished = true;
            }
        }
        
        synchronized(Main.lock)
        {
            try
            {
                socket.close();
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
            
            if (game != null)
            {
                if (isHost)
                    game.setHost(null);
                else
                    game.setClient(null);
            }
            
            Main.sessions.remove(this);
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
        else if (splitMsg[0].equals("GAME"))
        {
            processGameMessage(splitMsg[1]);
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
            synchronized (Main.lock)
            {
                response = "R_LIST." + Main.games.size();
                
                for (int i = 0; i < Main.games.size(); i++)
                {
                    Game game = Main.games.get(i);
                    response += ".GAME." +
                                game.id     + "." + 
                                game.status + "." +
                                game.host.name;
                }
                response += ".END";
                write(response);
            }
        }
        else if (command.equals("CREATE"))
        {
            // Locking for Main.games
            synchronized(Main.lock)
            {
                Game newGame = null;
                ArrayList<Game> games = Main.games;
                if (games.size() < Main.MAX_GAMES)
                {
                    newGame = new Game();
                    this.game = newGame;
                    this.game.setHost(this);
                    this.isHost = true;
                    games.add(newGame);
                    write("R_CREATE.SUCCESS");
                }
                else
                {
                    write("R_CREATE.FAILURE");
                }
            }
        }
        else if (command.equals("JOIN"))
        {
            // Locking for Main.games
            synchronized(Main.lock)
            {
                int i = 0;
                int jid = Integer.parseInt(params);
                for (i = 0; i < Main.games.size(); i++)
                {
                    Game jgame = Main.games.get(i);
                    if ((jgame.id == jid) &&
                         jgame.status == Game.WAITING)
                    {
                        this.game = jgame;
                        this.game.setClient(this);
                        this.game.status = Game.INPROGRESS;
                        this.isHost = false;
                        write("R_JOIN.SUCCESS");
                        write("READY");
                        game.sendHost("READY");
                        return;
                    }
                }
                
                write("R_JOIN.FAILURE");
            }
        }
        else if (command.equals("SPECTATE"))
        {
            write("R_SPECTATE.FAILURE");
        }
    }
    
    public void processGameMessage(String msg)
    {
        if (isHost)
            game.sendClient(msg);
        else
            game.sendHost(msg);
    }
}
