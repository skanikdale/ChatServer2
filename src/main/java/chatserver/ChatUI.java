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
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

class ChatClientConnector {
    
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    
    public static final int PORT = 10;
    
    ChatClientConnector() throws IOException {
        
        s = new Socket(InetAddress.getByName("skanikdale2l"), PORT);
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

class ClientThread implements Runnable {
    DataInputStream dis;
    ChatUI client;

    ClientThread(DataInputStream dataInputStream, ChatUI myClient) {
        this.dis = dataInputStream;
        this.client = myClient;
    }

    @Override
    public void run() {
        String string;

        block2:
        do {
            try {
                do {
                    if ((string = this.dis.readUTF()).startsWith("updateuserslist:")) {
                        this.updateUsersList(string);
                    } else {
                        if (string.equals("#logout")) {
                            break block2;
                        }
                        
                        /* Put message in text area window. */
                        this.client.getBroadcastTextArea().append("\n" + string);
                    }
                    
                    int n = this.client.getBroadcastTextArea().getLineStartOffset(this.client.getBroadcastTextArea().getLineCount() - 1);
                    this.client.getBroadcastTextArea().setCaretPosition(n);
                    
                } while (true);
            } catch (IOException | BadLocationException e) {
                this.client.getBroadcastTextArea().append("\nClientThread run : " + e);
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
        
        this.client.getUsersList().setListData(vector);
    }
}


/**
 *
 * @author skanikdale
 */
public class ChatUI extends javax.swing.JFrame {
    
    ChatClientConnector conn;
    
    /**
     * Creates new form ChatUI
     * @param conn
     */
    public ChatUI(ChatClientConnector conn) {
        initComponents();
        this.conn = conn;
        
        this.setTitle("Chat Messenger !");
    }
    
    public JTextArea getUserTextArea() {
        return jTextUserMsg;
    }
    
    public JTextArea getBroadcastTextArea() {
        return jTextBroadCastMsg;
    }
    
    public JList getUsersList() {
        return jListUsers;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextBroadCastMsg = new javax.swing.JTextArea();
        jButtonSubmit = new javax.swing.JButton();
        jButtonLogin = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListUsers = new javax.swing.JList();
        jButtonLogout = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextUserMsg = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextBroadCastMsg.setEditable(false);
        jTextBroadCastMsg.setColumns(20);
        jTextBroadCastMsg.setRows(5);
        jTextBroadCastMsg.setDragEnabled(true);
        jScrollPane1.setViewportView(jTextBroadCastMsg);

        jButtonSubmit.setText("Submit");
        jButtonSubmit.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jButtonSubmitStateChanged(evt);
            }
        });
        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitActionPerformed(evt);
            }
        });

        jButtonLogin.setText("Login");
        jButtonLogin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jButtonLoginStateChanged(evt);
            }
        });
        jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoginActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(jListUsers);

        jButtonLogout.setText("Logout");
        jButtonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogoutActionPerformed(evt);
            }
        });

        jTextUserMsg.setColumns(20);
        jTextUserMsg.setRows(5);
        jScrollPane2.setViewportView(jTextUserMsg);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonLogin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonLogout)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonSubmit)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonLogin)
                    .addComponent(jButtonLogout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jButtonSubmit)))
                .addGap(50, 50, 50))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSubmitStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jButtonSubmitStateChanged
    }//GEN-LAST:event_jButtonSubmitStateChanged

    private void jButtonLoginStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jButtonLoginStateChanged
    }//GEN-LAST:event_jButtonLoginStateChanged

    private void jButtonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoginActionPerformed

        String userName = JOptionPane.showInputDialog(this, (Object) "Enter your name: ");
        this.conn.clientChat(userName);

        ClientThread clientThread = new ClientThread(this.conn.dis, this);
        Thread thread = new Thread(clientThread);
        thread.start();

        this.setTitle(userName + " Chat Window");
        this.jButtonLogout.setEnabled(true);
        this.jButtonLogin.setEnabled(false);
    }//GEN-LAST:event_jButtonLoginActionPerformed

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed

        this.conn.logoutSession();
        this.jButtonLogout.setEnabled(false);
        this.jButtonLogin.setEnabled(true);
        this.setTitle("Login for Chat");
    }//GEN-LAST:event_jButtonLogoutActionPerformed

    private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed

        if (this.conn.s == null) {
            JOptionPane.showMessageDialog(this, "You have not logged in. Please login first");
            return;
        }

        try {
            this.conn.dos.writeUTF(this.jTextUserMsg.getText());
            this.jTextUserMsg.setText("");
        } catch (Exception e) {
            this.jTextBroadCastMsg.append("\nSend button click exception :" + e);
        }
    }//GEN-LAST:event_jButtonSubmitActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        final ChatClientConnector conn = new ChatClientConnector();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatUI(conn).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLogin;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JList jListUsers;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextBroadCastMsg;
    private javax.swing.JTextArea jTextUserMsg;
    // End of variables declaration//GEN-END:variables
}
