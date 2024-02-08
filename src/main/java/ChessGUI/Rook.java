/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

import java.util.ArrayList;
/**
 *
 * @author Tasman James Keenan
 * studentID: 21147547
 */
public class Rook extends Piece {

    public static Rook at(int x, int y, boolean isWhite, Chess chess)
    {
        if ((x>=0&&x<=7)&&(y>=0&&y<=7)) //ensures that instatiated coordinates are on the chessboard
        {
            return new Rook(x,y,isWhite,chess);
        }
        return null;
    }
    
    private Rook(int x, int y, boolean isWhite, Chess chess) {
        super(x, y, isWhite,5,4,chess);
    }

    @Override
    public ArrayList<Move> getMoves() { //return all possible moves this Rook can make on the board
        return super.getRookMoves(this);
    }
    
    @Override
    public String toString() {
        String toString = "r";
        
        if (super.isWhite()){
            toString = "R";
        }
        return toString;
    }

    
}
