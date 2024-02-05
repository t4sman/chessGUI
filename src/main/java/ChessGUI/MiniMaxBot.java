/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

/**
 *
 * @author tasma
 */
public class MiniMaxBot extends Player {
    
    MiniMaxBot(boolean isWhite)
    {
        super(isWhite);
    }

    @Override
    public Move toMove() {
        return Chess.calculateBestMove(chess, 3);
    }
    
    public static void main(String[] args) {
        MiniMaxBot white = new MiniMaxBot(true);
        MiniMaxBot black = new MiniMaxBot(false);
        
        Chess chess = new Chess("r1bq1rk1/pp2nppp/2pb4/8/2B1P3/8/PP3PPP/RNBQ1RK1 b - - 1 10", white, black);
        
        while(!chess.isGameOver())
        {
            Move move = Chess.calculateBestMove(chess, 3);
            chess.makeMove(move, false);
            System.out.println(chess.toString());
            System.out.println(move.toString());
        }
    }
}
