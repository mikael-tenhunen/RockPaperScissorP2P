package userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import rockpaperscissor.Peer;
import rockpaperscissor.RockPaperScissor;

/**
 *
 * This window is to give the information of which port you want to listen for 
 * incoming connections and optionally which IP and port you want to connect to.
 * At least one peer in the swarm must be known to connect to be a part of it.
 * 
 * The buttons call the static method RockPaperScissor.startServer (which returns
 * a Peer Object) and the connect button calls connectToPeer method at a Peer object.
 * 
 */
public class ConnectWindow extends javax.swing.JFrame {
    int portInt;
    int localPortNumber;
    int remotePortInt;
    String remoteIp;
    Peer peer;
    /**
     * Creates new form ConnectWindow
     */
    public ConnectWindow() {
        initComponents();       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        localPortField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        remoteIpField = new javax.swing.JTextField();
        connectButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        remotePortField = new javax.swing.JTextField();
        startButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        localPortField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localPortFieldActionPerformed(evt);
            }
        });

        jLabel1.setText("Local Port");

        jLabel2.setText("Remote IP");

        remoteIpField.setText("127.0.0.1");
        remoteIpField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remoteIpFieldActionPerformed(evt);
            }
        });

        connectButton.setText("Connect");
        connectButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String localPort = localPortField.getText();
                    String remoteIP = remoteIpField.getText();
                    String remotePort = remotePortField.getText();
                    if (localPortField.getText().trim().length() == 0 ||
                        localPortField.getText().trim().equals(""))
                    {
                        JOptionPane.showMessageDialog(null, "You need to enter a Local Port");
                    }
                    else if (remoteIpField.getText().trim().length() == 0 ||
                        remoteIpField.getText().trim().equals(""))
                    {
                        JOptionPane.showMessageDialog(null, "You need to enter a Remote IP");
                    }
                    else if (remotePortField.getText().trim().length() == 0 ||
                        remotePortField.getText().trim().equals(""))
                    {
                        JOptionPane.showMessageDialog(null, "You need to enter a Remote Port");
                    }
                    else
                    {
                        localPortNumber = Integer.parseInt(localPort);
                        remotePortInt = Integer.parseInt(remotePort);
                        try
                        {
                            peer = RockPaperScissor.startServer(localPortNumber);
                        }
                        catch(IOException exep)
                        {
                        }
                        peer.connectToPeer(remoteIp, remotePortInt, true);
                        ConnectWindow.this.dispose();
                        MainWindow.startMainWindow(ConnectWindow.this.peer);
                    }

                }
            }    );

            exitButton.setText("Exit");
            exitButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        System.exit(0);

                    }
                }    );

                startButton.setText("Start  without connecting");
                startButton.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            //String localPort = localPortField.getText();
                            if (localPortField.getText().trim().length() == 0 ||
                                localPortField.getText().trim().equals(""))
                            {
                                JOptionPane.showMessageDialog(null, "You need to enter a Local Port");
                            }
                            else
                            {
                                portInt = Integer.parseInt(localPortField.getText());
                                try
                                {
                                    peer = RockPaperScissor.startServer(portInt);
                                }
                                catch(IOException exep)
                                {
                                }
                                ConnectWindow.this.dispose();
                                MainWindow.startMainWindow(ConnectWindow.this.peer);

                            }

                        }
                    }    );

                    jLabel3.setText("Remote Port");

                    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                    getContentPane().setLayout(layout);
                    layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(80, 80, 80)
                                    .addComponent(localPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(startButton))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addComponent(jLabel3)
                                        .addComponent(exitButton))
                                    .addGap(20, 20, 20)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(0, 0, Short.MAX_VALUE)
                                            .addComponent(connectButton))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(remotePortField, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(remoteIpField, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(0, 0, Short.MAX_VALUE)))))
                            .addContainerGap())
                    );
                    layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(localPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)
                                .addComponent(startButton))
                            .addGap(24, 24, 24)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(remoteIpField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(remotePortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(connectButton)
                                .addComponent(exitButton))
                            .addContainerGap())
                    );

                    pack();
                }// </editor-fold>//GEN-END:initComponents

    private void localPortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localPortFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_localPortFieldActionPerformed

    private void remoteIpFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remoteIpFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_remoteIpFieldActionPerformed

    /**
     *
     */
    public static void startConnectWindow() {
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
            java.util.logging.Logger.getLogger(ConnectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConnectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConnectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConnectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConnectWindow().setVisible(true);
            }
        });       
    }
    
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ConnectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ConnectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ConnectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ConnectWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new ConnectWindow().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField localPortField;
    private javax.swing.JTextField remoteIpField;
    private javax.swing.JTextField remotePortField;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
