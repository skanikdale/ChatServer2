/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;


class ClientUIThread implements Runnable {
    DataInputStream dis;
    ClientUI client;

    ClientUIThread(DataInputStream dataInputStream, ClientUI myClient) {
        this.dis = dataInputStream;
        this.client = myClient;
    }

    @Override
    public void run() {
        String string;
        
        block2 : do {
            try {
                do {
                    if ((string = this.dis.readUTF()).startsWith("updateuserslist:")) {
                        this.updateUsersList(string);
                    } else {
                        if (string.equals("#logout")) break block2;
                        this.client.txtBroadcast.append("\n" + string);
                    }
                    int n = this.client.txtBroadcast.getLineStartOffset(this.client.txtBroadcast.getLineCount() - 1);
                    this.client.txtBroadcast.setCaretPosition(n);
                } while (true);
            }
            catch (IOException | BadLocationException e) {
                this.client.txtBroadcast.append("\nClientUIThread run : " + e);
            }
        } while (true);
    }

    public void updateUsersList(String string) {
        Vector<String> vector = new Vector<String>();
        
        string = string.replace("[", "");
        string = string.replace("]", "");
        string = string.replace("updateuserslist:", "");
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");

        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            vector.add(string2);
        }
        
        this.client.usersList.setListData(vector);
    }
}

class ChatClientConnection {
    
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    
    public static final int PORT = 10;
    
    ChatClientConnection() throws IOException {
        s = new Socket(InetAddress.getLocalHost(), PORT);
        this.dis = new DataInputStream(this.s.getInputStream());
        this.dos = new DataOutputStream(this.s.getOutputStream());
    }
    
    public void clientChat(String string) {

        try {

            this.dos.writeUTF(string);
        }
        catch (Exception e) {
            System.out.println("clientChat expection:: " + e);
        }
    }
    
    public void logoutSession() {
        if (this.s == null) {
            return;
        }
        try {
            this.dos.writeUTF("#logout");
            Thread.sleep(500);
            this.s = null;
        }
        catch (Exception e) {
            System.out.println("logoutSession expection:: " + e);
        }
    }
}

class ClientUI implements ActionListener{
    
    JButton sendButton;
    JButton logoutButton;
    JButton loginButton;
    JButton exitButton;
    JFrame chatWindow;
    JTextArea txtBroadcast;
    JTextArea txtMessage;
    JList usersList;
    ChatClientConnection clientConn;
    
    ClientUI(ChatClientConnection c) {
        this.clientConn = c;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        String string;
        JButton jButton = (JButton) actionEvent.getSource();

        if (jButton == this.sendButton) {
            if (this.clientConn.s == null) {
                JOptionPane.showMessageDialog(this.chatWindow, "You have not logged in. Please login first");
                return;
            }
            try {
                this.clientConn.dos.writeUTF(this.txtMessage.getText());
                this.txtMessage.setText("");
            } catch (Exception var3_3) {
                this.txtBroadcast.append("\nsend button click :" + var3_3);
            }
        }
        if (jButton == this.loginButton && (string = JOptionPane.showInputDialog(this.chatWindow, (Object) "Enter your name: ")) != null) {
            
            this.clientConn.clientChat(string);
            
            ClientUIThread clientThread = new ClientUIThread(this.clientConn.dis, this);
            Thread thread = new Thread(clientThread);
            thread.start();
            
            this.chatWindow.setTitle(string + " Chat Window");
            this.logoutButton.setEnabled(true);
            this.loginButton.setEnabled(false);
        }
        if (jButton == this.logoutButton && this.clientConn.s != null) {
            this.clientConn.logoutSession();
            this.logoutButton.setEnabled(false);
            this.loginButton.setEnabled(true);
            this.chatWindow.setTitle("Login for Chat");
        }
        if (jButton == this.exitButton) {
            if (this.clientConn.s != null) {
                this.clientConn.logoutSession();
            }
            System.exit(0);
        }
    }
    
    public void showGUI() {
        this.chatWindow = new JFrame();
        this.txtBroadcast = new JTextArea(5, 30);
        this.txtBroadcast.setEditable(false);
        this.txtMessage = new JTextArea(2, 20);
        this.usersList = new JList();
        this.sendButton = new JButton("Send");
        this.logoutButton = new JButton("Log out");
        this.loginButton = new JButton("Log in");
        this.exitButton = new JButton("Exit");
        
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add((Component)new JLabel("Broad Cast messages from all online users", 0), "North");
        jPanel.add((Component)new JScrollPane(this.txtBroadcast), "Center");
        
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new FlowLayout());
        jPanel2.add(new JScrollPane(this.txtMessage));
        jPanel2.add(this.sendButton);
        
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new FlowLayout());
        jPanel3.add(this.loginButton);
        jPanel3.add(this.logoutButton);
        jPanel3.add(this.exitButton);
        
        JPanel jPanel4 = new JPanel();
        jPanel4.setLayout(new GridLayout(2, 1));
        jPanel4.add(jPanel2);
        jPanel4.add(jPanel3);
        
        JPanel jPanel5 = new JPanel();
        jPanel5.setLayout(new BorderLayout());
        jPanel5.add((Component)new JLabel("Online Users", 0), "East");
        jPanel5.add((Component)new JScrollPane(this.usersList), "South");
        
        this.chatWindow.add((Component)jPanel5, "East");
        this.chatWindow.add((Component)jPanel, "Center");
        this.chatWindow.add((Component)jPanel4, "South");
        this.chatWindow.pack();
        this.chatWindow.setTitle("Login for Chat");
        this.chatWindow.setDefaultCloseOperation(0);
        this.chatWindow.setVisible(true);
        this.sendButton.addActionListener(this);
        this.logoutButton.addActionListener(this);
        this.loginButton.addActionListener(this);
        this.exitButton.addActionListener(this);
        this.logoutButton.setEnabled(false);
        this.loginButton.setEnabled(true);
        
        this.txtMessage.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent focusEvent) {
                ClientUI.this.txtMessage.selectAll();
            }
        });
        
        this.chatWindow.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (ClientUI.this.clientConn.s != null) {
                    JOptionPane.showMessageDialog(ClientUI.this.chatWindow, "You are logged out right now. ", "Exit", 1);
                    ClientUI.this.clientConn.logoutSession();
                }
                System.exit(0);
            }
        });
    }
}

/**
 *
 * @author skanikdale
 */
public class ChatClient {

     public static void main(String[] args) throws IOException {
        
        final ChatClientConnection conn = new ChatClientConnection();
        
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientUI ui = new ClientUI(conn);
                ui.showGUI();
            }
        });
    }
}
