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
public class King extends Piece {

    
    
    public static King at(int x, int y, boolean isWhite, Chess chess)
    {
        if ((x>=0&&x<=7)&&(y>=0&&y<=7)) //ensures that instatiated coordinates are on the chessboard
        {
            return new King(x,y,isWhite,chess);
        }
        return null;
    }
    
    private King(int x, int y, boolean isWhite, Chess chess) { //King constructor
        super(x, y, isWhite,20, 6, chess);
    }

    @Override
    public ArrayList<Move> getMoves() //return all possible moves this king can make on the board
    {
        return super.getKingMoves(this);
    }
    @Override
    public String toString() {
        String toString = "k";
        
        if (this.isWhite()){
            toString = "K";
        }
        return toString;
    }
    
}
