/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ChessGUI;


/**
 *
 * @author Tasman James Keenan
 * studentID: 21147547
 */
public abstract class Player {
    protected Chess chess = null;
    protected boolean isWhite;
    
    Player(boolean isWhite)
    {
        this.isWhite = isWhite;
    }
    
    public void addBoard(Chess chess)
    {
        this.chess = chess;
    }
    
    public boolean hasBoard()
    {
        return this.chess != null;
    }
    
    public abstract Move toMove();
    
    public boolean isWhite() {
        return isWhite;
    }
}
