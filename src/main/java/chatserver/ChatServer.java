/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

class ChatServerConnection {

    ArrayList clientSockets;
    TreeSet<String> users;
    ServerSocket ss;
    Socket s;
    
    public final static int PORT = 10;
    public final static String USERS_PREFIX = "updateuserslist:";
    public final static String LOGOUT_MESSAGE = "logout";

    // Constructor
    ChatServerConnection() throws IOException {
        
        clientSockets = new ArrayList();
        users = new TreeSet<>();
    
        ss = new ServerSocket(PORT);

        while (true) {
            s = ss.accept();
            Runnable r = new ServerThread(s, clientSockets, users);
            Thread t = new Thread(r);
            t.start();
        }
    }
}

class ServerThread implements Runnable {

    Socket s;
    ArrayList clientSockets;
    TreeSet<String> users;
    String userName;

    ServerThread(Socket s, ArrayList clientSockets, TreeSet users) {
        
        this.s = s;
        this.clientSockets = clientSockets;
        this.users = users;
        
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            userName = dis.readUTF();
            
            clientSockets.add(s);
            
            users.add(userName);
            
            // Pass 'userName' logged in message to all clients
            updateAllClients("****** " + userName + " Logged in at " + (new Date()) + " ******");
            
            // Pass userName to all clients to update user's list
            updateUsersList();
            
        } catch (Exception e) {
            System.err.println("ServerThread constructor  " + e);
        }
    }

    ///////////////////////
    @Override
    public void run() {
        String s1;
        
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            
            do {
                
                // read client message here.
                s1 = dis.readUTF();
                
                if (s1.toLowerCase().equals(ChatServerConnection.LOGOUT_MESSAGE)) {
                    break;
                }
                
                // pass msg from one client to all clients
                updateAllClients(userName + " :: " + s1);
            } while (true);
            
            DataOutputStream tdos = new DataOutputStream(s.getOutputStream());
            
            tdos.writeUTF(ChatServerConnection.LOGOUT_MESSAGE);
            tdos.flush();
            
            users.remove(userName);
            updateAllClients("****** " + userName + " Logged out at " + (new Date()) + " ******");
            
            updateUsersList();
            clientSockets.remove(s);
            s.close();

        } catch (Exception e) {
            System.out.println("ServerThread Run" + e);
        }
    }
////////////////////////

    public void updateUsersList() {
    
        updateAllClients(ChatServerConnection.USERS_PREFIX + users.toString());
    }
    
    ////////////////////////
    // Inform all clients.
    public void updateAllClients(String s1) {
        
        Iterator i = clientSockets.iterator();
        
        while (i.hasNext()) {
            try {
                Socket temp = (Socket) i.next();
                
                DataOutputStream dos = new DataOutputStream(temp.getOutputStream());
                dos.writeUTF(s1);
                dos.flush();
                
                System.out.println("sent to port no. : "+ temp.getPort()+"  : "+ s1);
                
            } catch (Exception e) {
                System.err.println("TellEveryOne " + e);
            }
        }
    }
}

/**
 *
 * @author skanikdale
 */
public class ChatServer {
       public static void main(String[] args) throws IOException {
           
          ChatServerConnection chatServerConnection = new ChatServerConnection();
           
    }
}
