/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

/**
 *
 * @author tasma
 */
public class MaxiMinBot extends Player {

    
    
    public MaxiMinBot(boolean isWhite)
    {
        super(isWhite);
    }
    @Override
    public Move toMove() {
        return Chess.calculateWorstMove(chess, 2);
    }
    
}
