/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 *
 * @author tasma
 */
public class ClockPanel extends JPanel {
    
    private final int WIDTH = 250;
    private final int HEIGHT = 150;
    
    private final Color whiteBright = new Color(242,242,242);
    private final Color whiteDark = new Color(208,208,208);
    private final Color blackBright = new Color(50,50,50);
    private final Color blackDark = new Color(10,10,10);
    private final Color grey = new Color(128,128,128);
    
    
    private final boolean orientation; // should be true if the perspective is white.
    
    private static final java.awt.Font verdana_12 = new java.awt.Font("Verdana", java.awt.Font.BOLD, 30);
    
    ChessClock clock;
    Chess chess;
    
    ClockPanel(Chess chess)
    {
        this.chess = chess;
        this.clock = chess.getClock();
        chess.addClockPanel(this); 
        this.setSize(WIDTH, HEIGHT);
        
        Human[] humans = chess.getHumans();
        if (humans[1] != null)
        {
            orientation = false;
        } else {
            orientation = true;
        }
    }
    
    @Override
    public void paint(Graphics g)
    {
        String whiteTime = clock.getTimeToString(true);
        String blackTime = clock.getTimeToString(false);
        
        
        
        //paint white background
        g.setColor(chess.isWhiteToMove() ? whiteBright : whiteDark);
        g.fillRect(0,orientation ? HEIGHT/2 : 0,WIDTH,HEIGHT/2);
        //paint black background;
        g.setColor(!chess.isWhiteToMove() ? blackDark : blackBright);
        g.fillRect(0,!orientation ? HEIGHT/2 : 0,WIDTH,HEIGHT/2);
        
        
        g.setFont(verdana_12);
        
        //paint white time left
        g.setColor(chess.isWhiteToMove() ? blackDark : blackBright);
        g.drawString(whiteTime,WIDTH / 4, (orientation ? ((HEIGHT*3/4) + 10) : ((HEIGHT / 4) + 10)));
        //paint black time left
        g.setColor(!chess.isWhiteToMove() ? whiteBright : whiteDark);
        g.drawString(blackTime,WIDTH / 4, (!orientation ? ((HEIGHT*3/4) + 10) : ((HEIGHT / 4) + 10)));
        
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setStroke(new BasicStroke(10));
        g2.setColor(grey);
        g2.drawRoundRect(0, 0, WIDTH, HEIGHT, 10, 10);
        
        
    }
    
    
    
}
