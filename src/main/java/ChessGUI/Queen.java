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
public class Queen extends Piece {

    
    
    public static Queen at(int x, int y, boolean isWhite, Chess chess)
    {
        if ((x>=0&&x<=7)&&(y>=0&&y<=7)) //ensures that instatiated coordinates are on the chessboard
        {
            return new Queen(x,y,isWhite,chess);
        }
        return null;
    }
    
    private Queen(int x, int y, boolean isWhite, Chess chess) {
        super(x, y, isWhite,9,5, chess);
    }

    @Override
    public ArrayList<Move> getMoves() //return all possible moves this queen can make on the board
    {
        return super.getQueenMoves(this);
    }
    
    @Override
    public String toString() {
        String toString = "q";
        
        if (super.isWhite()){
            toString = "Q";
        }
        return toString;
    }


    
    
    
}
