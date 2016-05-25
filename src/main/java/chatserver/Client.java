/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class ClientConnection {
    
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    
    public static final int PORT = 10;
    
    ClientConnection() throws IOException {
        s = new Socket(InetAddress.getLocalHost(), PORT);
        this.dis = new DataInputStream(this.s.getInputStream());
        this.dos = new DataOutputStream(this.s.getOutputStream());
        System.out.println("address:: " + InetAddress.getLocalHost());
        
        while(true)
        {
          dos.writeUTF("Sam");
          
          System.out.println("Got info from server::" + dis.readUTF());
        }
    }
}


/**
 *
 * @author skanikdale
 */
public class Client {
    
    public static void main(String[] args) throws IOException {
        new ClientConnection();
    }
    
}
