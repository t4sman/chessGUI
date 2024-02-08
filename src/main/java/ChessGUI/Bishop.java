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
public class Bishop extends Piece {
    
    public static Bishop at(int x, int y, boolean isWhite, Chess chess)
    {
        if ((x>=0&&x<=7)&&(y>=0&&y<=7)) //ensures that instatiated coordinates are on the chessboard
        {
            return new Bishop(x,y,isWhite,chess);
        }
        return null;
    }
    
    private Bishop(int x, int y, boolean isWhite, Chess chess) { //Bishop Constructor
        super(x, y, isWhite,3.25,3, chess);
    }

    @Override
    public ArrayList<Move> getMoves() { //return all possible moves this bishop can make on the board
        return super.getBishopMoves(this);
    }    
    
    @Override
    public String toString() { //return its string representation that would be in a FEN string
        String toString = "b";
        
        if (super.isWhite()){
            toString = "B";
        }
        return toString;
    } 
}
