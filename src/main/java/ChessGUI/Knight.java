/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

import java.util.ArrayList;

/**
 *
 *
 *
 * @author Tasman Keenan 
 * StudentID 21147547
 *
 */
public class Knight extends Piece {

    
    
    public static Knight at(int x, int y, boolean isWhite, Chess chess)
    {
        if ((x>=0&&x<=7)&&(y>=0&&y<=7)) //ensures that instatiated coordinates are on the chessboard
        {
            return new Knight(x,y,isWhite,chess);
        }
        return null;
    }
    
    private Knight(int x, int y, boolean isWhite, Chess chess) {
        super(x, y, isWhite,3,2, chess);
    }

    
    @Override
    public ArrayList<Move> getMoves() {//return all possible moves this knight can make on the board
        return super.getKnightMoves(this);
    }
    
    
    @Override
    public String toString() {
        String toString = "n";
        
        if (super.isWhite()){
            toString = "N";
        }
        return toString;
    } 
}
