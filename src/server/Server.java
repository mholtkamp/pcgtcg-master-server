package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable
{
    public ServerSocket serverSocket;
    public boolean finished;
    
    public Server()
    {
        try
        {
            serverSocket  = new ServerSocket(2000);
            finished = false;
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
                Socket newSocket = serverSocket.accept();
                Session newSession = new Session();
                newSession.Set(newSocket);
                Thread newThread = new Thread(newSession);
                newThread.start();
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
        }
    }
}
