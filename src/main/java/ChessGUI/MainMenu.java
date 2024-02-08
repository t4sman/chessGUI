/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ChessGUI;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Tasman James Keenan
 * studentID: 21147547
 */
public class MainMenu extends javax.swing.JPanel {

    /**
     * Creates new form MainMenu
     */
    Image backgroundImage;
    ChessGUI gui;
    public MainMenu(ChessGUI gui) {
        this.gui = gui;
        try {
            // Load the image using classpath-relative path
            InputStream inputStream = getClass().getResourceAsStream("/assets/mainMenu.png");

            if (inputStream != null) {
                // Read the image from the input stream
                backgroundImage = ImageIO.read(inputStream);
            } else {
                // Handle the case when the input stream is null (file not found)
                System.err.println("Image file not found: assets/mainMenu.png");
            }
        } catch (IOException ex) {
            // Handle IOException
            Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
        }

        initComponents();
    }
    
    @Override
    public void paint(Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, 900, 600, this);
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(102, 102, 102));
        setToolTipText("");
        setLayout(null);

        jButton1.setText("Play");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1);
        jButton1.setBounds(340, 360, 247, 101);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Check if the "Play" button was pressed
        if (evt.getSource() == jButton1) {
            gui.changeActivePanel(new OptionsMenu(gui));
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}
