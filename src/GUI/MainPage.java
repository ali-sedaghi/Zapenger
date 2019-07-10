package GUI;

import SocketStuff.Client;
import SocketStuff.Threads.InputStream;
import SocketStuff.Threads.OutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainPage extends JPanel
{
    private boolean visitedChatOnce= false;
    private static JFrame frame;
    private JButton setting;
    private JButton newChat;
    private ArrayList<JButton> buttons= new ArrayList<>();

    public MainPage(boolean visitedChatOnce)
    {
        this.visitedChatOnce=visitedChatOnce;
        //construct components
        setting = new JButton ("Setting");
        newChat = new JButton ("Start a new chat");

        OutputStream.send("##ChatWith-"+Client.getUserName());
        String receiveMessage = InputStream.read();
        ArrayList<String> listOfChats = new ArrayList<>();
        System.out.println("message received : "+receiveMessage);
        String[] temp = receiveMessage.split("##");
        for (int k=0;k<temp.length;k++)
            listOfChats.add(temp[k]);
        for (int k =0;k<listOfChats.size();k++)
            buttons.add(new JButton (listOfChats.get(k)));


        //adjust size and set layout
        setPreferredSize (new Dimension(313, 375));
        setLayout (null);

        //add components
        add (setting);
        add (newChat);
        for (int k=0;k<buttons.size();k++)
            add(buttons.get(k));

        //set component bounds (only needed by Absolute Positioning)
        setting.setBounds (0, 0, 155, 25);
        newChat.setBounds (160, 0, 155, 25);
        for (int k=0;k<buttons.size();k++)
            buttons.get(k).setBounds (0, 45+k*27, 315, 25);

        for (int k=0;k<buttons.size();k++)
        {
            String name = listOfChats.get(k);
            buttons.get(k).addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    setVisible(false);
                    LogIn.Hide();
                    if (visitedChatOnce)
                        ChatPage.Hide();
                    frame = new JFrame ("Zapenger");
                    frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
                    frame.getContentPane().add (new ChatPage(name));
                    frame.pack();
                    frame.setVisible (true);
                }
            });
        }
    }
    public static void Hide()
    {
        frame.setVisible(false);
    }
}
