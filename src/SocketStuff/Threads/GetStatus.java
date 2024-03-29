package SocketStuff.Threads;

import java.util.concurrent.TimeUnit;

public class GetStatus extends Thread
{
    private static volatile boolean flag=true;
    @Override
    public void run()
    {
        while (flag)
        {
            OutputGUI.sendRequest();
            try
            {
                TimeUnit.SECONDS.sleep(3);
            }
            catch (InterruptedException error)
            {
                System.err.println(error);
            }
        }
    }
    public static void end()
    {
        flag=false;
    }
    public static void con()
    {
        flag=true;
    }
}
