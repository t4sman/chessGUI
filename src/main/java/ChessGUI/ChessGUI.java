/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ChessGUI;



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 *
 * @author tasma
 */
public class ChessGUI extends JFrame {
    
    public static final int FRAMEHEIGHT = 600;
    public static final int FRAMEWIDTH = 900;
    
    private JPanel activePanel; //the panel being displayed on the frame
    
    public void changeActivePanel(JPanel panel) //update which panel is being shown
    {
        SwingUtilities.invokeLater(() -> {
            if (activePanel != null)
            {
                this.remove(activePanel);
            }

            activePanel = panel;
            this.add(activePanel);
            this.revalidate();
            this.repaint();
        });
    }

    public static void main(String[] args) {
        
        ChessGUI chessGui = new ChessGUI(); //make new ChessGUI
        
        chessGui.setTitle("Chess"); //set title to chess
        chessGui.setResizable(false); //cannot resize
        chessGui.setBounds(0,0,FRAMEWIDTH,FRAMEHEIGHT); //set the size to the bounds defined FRAMEHEIGHT and FRAMEWIDTH
        
        MainMenu mainMenu = new MainMenu(chessGui); //make a new main menu panel to display
        chessGui.changeActivePanel(mainMenu); //change the active panel to this panel
        
        chessGui.setDefaultCloseOperation(EXIT_ON_CLOSE); //default close operation is exit
        chessGui.setVisible(true); //set visibility to true
    }
}
