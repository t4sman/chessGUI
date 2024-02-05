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
public class Pawn extends Piece {

    
    public static Pawn at(int x, int y, boolean isWhite, Chess chess)
    {
        if ((x>=0&&x<=7)&&(y>=0&&y<=7)) //ensures that instatiated coordinates are on the chessboard
        {
            return new Pawn(x,y,isWhite,chess);
        }
        return null;
    }
    
    private Pawn(int x, int y, boolean isWhite, Chess chess){
        super(x, y, isWhite,1,1, chess);
    }
    
    
    @Override
    public ArrayList<Move> getMoves() {//return all possible moves this pawn can make on the board
        return super.getPawnMoves(this);
    }

    
    @Override
    public String toString() {
        String toString = "p";
        
        if (super.isWhite()){
            toString = "P";
        }
        return toString;
    } 
    
    
    
}
