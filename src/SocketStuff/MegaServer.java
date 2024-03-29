package SocketStuff;

import DataBase.AccountDB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MegaServer
{
    private static AccountDB accountDB = new AccountDB();
    private static ArrayList<ClientHandler> clientList = new ArrayList<>();
    private static volatile boolean finished = false;

    public static void main(String[] args)
    {
        AccountDB.setAllUserToOffline();
        Thread input = new ServerInput();
        input.start();
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(37424);
        }
        catch (IOException error)
        {
            System.err.println(error +"(Error setting up Server on Desired port!)");
        }

        try
        {
            while (!finished)
            {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clientList.add(clientHandler);
                clientHandler.start();
            }
            for (int k = 0;k<clientList.size();k++)
                clientList.get(k).close();

            serverSocket.close();
        }
        catch (IOException error)
        {
            System.err.println(error + "(Error Accepting SocketStuff.Client "+(clientList.size()+1)+")");
        }
    }

    public static ArrayList<ClientHandler> getClientList()
    {
        return clientList;
    }

    public static void end()
    {
        finished = true;
    }

    public static int getClientNumber(String userName)
    {
        for (int k=0; k < clientList.size(); k++)
        {
            if (clientList.get(k).getUserName().equalsIgnoreCase(userName))
                return k;
        }
        return -1;
    }
}
