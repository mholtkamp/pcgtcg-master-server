package server;

public class Cleaner implements Runnable
{
    public boolean finished;
    
    public Cleaner()
    {
        finished = false;
    }
    
    public void run()
    {
        while (!finished)
        {
            synchronized(Main.lock)
            {
                // Clean up games with no sessions
                for (int i = 0; i < Main.games.size(); i++)
                {
                    if ((Main.games.get(i).host   == null) &&
                        (Main.games.get(i).client == null))
                     {
                        Main.games.remove(i);
                        i--;
                     }
                }
            }
            
            try
            {
                Thread.sleep(1000);
            }
            catch (Exception ex)
            {
                
            }
        }
    }
}
