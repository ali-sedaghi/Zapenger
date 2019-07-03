package SocketStuff;

import DataBase.AccountDB;
import DataBase.UserList;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import DataBase.Chats;

public class ClientHandler extends Thread
{
    private Socket socket;
    private String userName;
    private ArrayList<ClientHandler> chatList = new ArrayList<>();
    private ArrayList<ArrayList<String>> groupList = new ArrayList<>();
    private int chatNumber=-1;
    private int groupNumber=-1;
    InputStream inputStream;
    BufferedReader receiveRead;
    OutputStream outputStream;
    BufferedWriter sentRead;
    private AccountDB accountDB;
    public ClientHandler(Socket socket,AccountDB accountDB)
    {
        this.socket=socket;
        this.accountDB=accountDB;
    }
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                inputStream = socket.getInputStream();
                receiveRead = new BufferedReader(new InputStreamReader(inputStream));
                String receiveMessage;
                while(true)
                {
                    if((receiveMessage = receiveRead.readLine()) != null)
                    {
                        System.out.println("received message is : " +receiveMessage);
                        if (receiveMessage.startsWith("##"))
                        {
                            command(receiveMessage);
                        }
                        else
                        {
                            String[] temp = receiveMessage.split("##");
                            Chats.addToChat(userName+"##"+temp[1],userName,temp[0]);
                        }
                    }
                }
            }
            catch (IOException error)
            {
                System.err.println(error + ":Error setting up input stream!");
            }
        }
    }
    private void send(String receiveMessage,int converNumber,int type)
    {
        if (type==1)
        {
            ArrayList<ClientHandler> clientList = new ArrayList<>();
            clientList.add(chatList.get(converNumber));
            send(receiveMessage,clientList);
        }
        else
        {

        }
    }
    private void send(String receiveMessage,ArrayList<ClientHandler> clientList)
    {
        for (int k=0;k<clientList.size();k++)
        {
            try
            {
                outputStream = clientList.get(k).getSocket().getOutputStream();
                sentRead = new BufferedWriter(new OutputStreamWriter(outputStream));
                sentRead.write(/*"&"+userName+":"+*/receiveMessage);
                sentRead.newLine();
                sentRead.flush();
            }
            catch (IOException error)
            {
                System.err.println(error + ":Error setting up output stream for user "+userName+"!");
            }
        }
    }
    public Socket getSocket()
    {
        return socket;
    }
    public String getUserName()
    {
        return userName;
    }
    /*private int correctClient(ArrayList<ClientHandler> clientList)
    {
        for (int k=0;k<clientList.size();k++)
        {
            if (clientList.get(k).userName.equalsIgnoreCase(this.userName))
                return k;
        }
        return -1;
    }*/
    private int hasChat(String userName)
    {
        for (int k=0;k<chatList.size();k++)
        {
            if (chatList.get(k).getUserName().equalsIgnoreCase(userName))
                return k;
        }
        return -1;
    }
    private ArrayList<ClientHandler> getChatList()
    {
        return chatList;
    }
    private void command(String command)
    {
        String[] temp = command.split("-");
        switch (temp[0])
        {
            case "##Username":
                this.userName=temp[1];
                break;
            case "##Info":
                int isAuthorized = AccountDB.isAuthorized(temp[1],temp[2]);
                this.userName=temp[1];
                ArrayList<ClientHandler> clientList = new ArrayList<>();
                clientList.add(MegaServer.getClientList().get(MegaServer.getClientNumber(userName)));
                switch (isAuthorized)
                {
                    case 1:
                        send("##LogedIn",clientList);
                        userName=temp[1];
                        break;
                    case -1:
                        send("##NotLogedIn",clientList);
                        break;
                    case 0:
                        send("##NotFound",clientList);
                        break;
                }
                break;
            case "##Chat":
                if (hasChat(temp[1])!=-1)
                {
                    chatNumber=hasChat(temp[1]);
                }
                else
                {
                    chatList.add(MegaServer.getClientList().get(MegaServer.getClientNumber(temp[1])));
                    MegaServer.getClientList().get(MegaServer.getClientNumber(temp[1])).getChatList().add(MegaServer.getClientList().get(MegaServer.getClientNumber(userName)));
                    chatNumber=chatList.size()-1;
                }
                break;
            case "##Group":
                if (temp.length==2)
                {
                    for (int k=0;k<groupList.size();k++)
                        if (groupList.get(k).get(0).equalsIgnoreCase(temp[1]))
                        {
                            groupNumber=k;
                            break;
                        }
                }
                else
                {
                    ArrayList<String> tempArray = new ArrayList<>();
                    tempArray.add(temp[1]);
                    for (int k=2;k<temp.length;k++)
                        tempArray.add(temp[k]);
                    groupList.add(tempArray);
                    groupNumber=groupList.size()-1;
                }
            case "##Close":
                close();

                break;
            default:
                System.out.println("You can not send a text starting with ##.");
        }
    }
    public void close()
    {
        try
        {
            receiveRead.close();
            sentRead.close();
            inputStream.close();
            outputStream.close();
        }
        catch (IOException error)
        {
            System.err.println(error);
        }
    }
}
