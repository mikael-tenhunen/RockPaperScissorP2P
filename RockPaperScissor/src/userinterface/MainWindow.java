package userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import rockpaperscissor.Gesture;
import rockpaperscissor.Peer;
import rockpaperscissor.RockPaperScissor;

/**
 * MainWindow is the JFrame for the main game window. This is where the game is
 * played, gestures sent, game state shown.
 * 
 * Right now it is a bastard hybrid of AWT and Swing. There are methods to
 * update the GUI from within the game (e.g. updatePlayers) and to enable the
 * send button.
 * 
 * When a gesture is sent, the send button gets disabled until allowGestureSend 
 * is called.
 */
public class MainWindow extends javax.swing.JFrame {
    private Peer peer;

    /**
     * Creates new form MainWindow
     * @param peer 
     */
//    public MainWindow() {
//        initComponents();
//    }
    
    
    public MainWindow(Peer peer) {
        this.peer = peer;
        initComponents();
        System.out.println("Peer object: " + peer);
        peer.setMainWindow(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        choicesList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        scoreList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rockRadio = new javax.swing.JRadioButton();
        paperRadio = new javax.swing.JRadioButton();
        scissorRadio = new javax.swing.JRadioButton();
        sendButton = new javax.swing.JButton();
        disconnectButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        playerList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(choicesList);

        jLabel1.setText("Last round's choices");

        jScrollPane2.setViewportView(scoreList);

        jLabel2.setText("Total score");

        jLabel3.setText("Players");

        buttonGroup1.add(rockRadio);
        rockRadio.setText("Rock");
        rockRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rockRadioActionPerformed(evt);
            }
        });

        buttonGroup1.add(paperRadio);
        paperRadio.setText("Paper");
        paperRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paperRadioActionPerformed(evt);
            }
        });

        buttonGroup1.add(scissorRadio);
        scissorRadio.setText("Scissors");

        sendButton.setText("Send Choice");
        sendButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if(rockRadio.isSelected())
                    {
                        MainWindow.this.peer.playGesture(Gesture.ROCK);
                    }
                    else if(paperRadio.isSelected())
                    {
                        MainWindow.this.peer.playGesture(Gesture.PAPER);
                    }
                    else if(scissorRadio.isSelected())
                    {
                        MainWindow.this.peer.playGesture(Gesture.SCISSOR);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "You need to pick your gesture");
                    }

                    sendButton.setEnabled(false);

                }
            }    );

            disconnectButton.setText("Disconnect");
            disconnectButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        MainWindow.this.peer.disconnectMe();
                        //                ConnectWindow.startConnectWindow();
                        MainWindow.this.dispose();
                        RockPaperScissor.startProgram();
                    }
                }    );
                disconnectButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        disconnectButtonActionPerformed(evt);
                    }
                });

                playerList.setModel(new javax.swing.AbstractListModel() {
                    String[] strings = { "Currently no players..." };
                    public int getSize() { return strings.length; }
                    public Object getElementAt(int i) { return strings[i]; }
                });
                jScrollPane4.setViewportView(playerList);

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(disconnectButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rockRadio)
                        .addGap(4, 4, 4)
                        .addComponent(paperRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scissorRadio)
                        .addGap(27, 27, 27)
                        .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(46, 46, 46))
                );
                layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane4)
                                    .addComponent(jScrollPane1)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2)))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(disconnectButton)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(paperRadio)
                                .addComponent(scissorRadio)
                                .addComponent(rockRadio))
                            .addComponent(sendButton))
                        .addContainerGap(13, Short.MAX_VALUE))
                );

                pack();
            }// </editor-fold>//GEN-END:initComponents

    private void rockRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rockRadioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rockRadioActionPerformed

    private void paperRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paperRadioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paperRadioActionPerformed

    private void disconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_disconnectButtonActionPerformed

    /**
     *
     * @param peer
     */
    public static void startMainWindow(final Peer peer){
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
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow(peer).setVisible(true);
            }
        });
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JList choicesList;
    private javax.swing.JButton disconnectButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JRadioButton paperRadio;
    private javax.swing.JList playerList;
    private javax.swing.JRadioButton rockRadio;
    private javax.swing.JRadioButton scissorRadio;
    private javax.swing.JList scoreList;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables

    /**
     *
     * @param allGestures, List with all players' gestures (including the local
     * player.
     */
    public void updateGestures(List<Gesture> allGestures) {
        DefaultListModel<Gesture> gestureListModel = new DefaultListModel();
        for (Gesture score : allGestures) {
            gestureListModel.addElement(score);
        }
        choicesList.setModel(gestureListModel);   
    }
    
    /**
     *
     * @param allScores, list with all players' scores
     */
    public void updateScores(List<Integer> allScores) {
         DefaultListModel<Integer> scoreListModel = new DefaultListModel();
        for (Integer score : allScores) {
            scoreListModel.addElement(score);
        }
        scoreList.setModel(scoreListModel);       
    }
    
    /**
     * 
     * @param allPlayerServers, all players' addresses to their ServerSockets
     */
    public void updatePlayers(List<InetSocketAddress> allPlayerServers) {
        DefaultListModel<InetSocketAddress> playerServerListModel = new DefaultListModel();
        for (InetSocketAddress address : allPlayerServers) {
            playerServerListModel.addElement(address);
        }
        playerList.setModel(playerServerListModel);        
    }

    /**
     * Enables the send button, so gestures can be sent to other players.
     */
    public void allowGestureSend() {
//        sendButton.setEnabled(true);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                sendButton.setEnabled(true);
            }
        });
    }

//    public void removeGestureAt(final int index) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                scoreList.remove(index + 1);
//            }
//        });        
//    }
}
