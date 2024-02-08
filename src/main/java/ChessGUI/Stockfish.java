/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tasman James Keenan
 * studentID: 21147547
 */
public class Stockfish extends Player {

    StockfishConnector stockfish;

    public Stockfish(boolean isWhite) {
        super(isWhite);
        try {
            this.stockfish = new StockfishConnector();
        } catch (IOException ex) {
            Logger.getLogger(Stockfish.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public Move toMove() {
        String algebraic = stockfish.getBestMove(chess.getFENString(), 500);
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.toString().equals(algebraic))
            {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Stockfish.class.getName()).log(Level.SEVERE, null, ex);
                }
                return move;
            }
        }
        return null;
    }
    
}
