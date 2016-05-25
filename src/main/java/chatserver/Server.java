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

class ServerConnection {
    
    ServerSocket ss;
    Socket s;
    public final static int PORT = 10;
    public final static String UPDATE_USERS = "updateuserslist:";
    public final static String LOGOUT_MESSAGE = "@@logoutme@@:";
    
    ServerConnection() throws IOException {
            
        ss = new ServerSocket(PORT);
        
        while(true) {
            s = ss.accept(); // try to establish connection with the client; returns new socket
            
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String info = dis.readUTF();
            System.out.println("Client name::" + info);
            
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF("server info !!!!!!!!");
        }
    }
    
}
/**
 *
 * @author skanikdale
 */
public class Server {
    
    public static void main(String[] args) throws IOException {
        new ServerConnection();
    }
    
}
