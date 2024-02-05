/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package ChessGUI;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tasma
 */
public class ChessTest {
    
    private Chess chess;
    
    ChessTest(){};
    
    private void setupBoard(String FEN)
    {
        chess = new Chess(FEN);
    }
    
    void setupPromoBoard(boolean isWhiteToPromote)
    {
        if (isWhiteToPromote)
        {
            setupBoard("rn1qkbnr/pP3ppp/3p4/2p2b2/8/3P1P2/1PP3PP/RNBQKBNR w KQkq -");
        } else {
            setupBoard("rnbqkbnr/ppppp2p/8/2PP4/2B5/4PP1p/PP4p1/RNBQK1NR b KQkq -");
        }
    }

    //test perft
    @Test
    void testPerftPositionOne()
    {
        setupBoard("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        
        int expected = 422333;
        int actual = chess.perft(4);
        
        assertEquals(expected, actual, "This position should have 422333 children after 4 moves");
    }
    
    @Test
    void testPerftPositionTwo()
    {
        setupBoard("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        
        int expected = 62379;
        int actual = chess.perft(3);
        
        assertEquals(expected, actual, "This position should have 62379 children after 3 moves");
    }
    
    @Test
    void testPerftPositionThree()
    {
        setupBoard("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        
        int expected = 43238;
        int actual = chess.perft(4);
        
        assertEquals(expected, actual, "This position should have 43238 children after 4 moves");
    }
    
    @Test
    void testWhiteCastleKingside()
    {
        setupBoard("r1bqkbnr/ppp2ppp/2n1p3/3p4/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq -");
        
        String postCastleFEN = "r1bqkbnr/ppp2ppp/2n1p3/3p4/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b kq -";
        String FENToTest = null;
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.WCASTLEK)
            {
                FENToTest = move.getPostBoardFEN();
                break;
            }
        }
        
        assertEquals(postCastleFEN, FENToTest);
    }
    
    @Test
    void testWhiteCastleQueenside()
    {
        setupBoard("rnbqkbnr/pp2p1pp/8/2p2p2/3pP3/2NPBQ2/PPP2PPP/R3KBNR w KQkq -");
        
        String postCastleFEN = "rnbqkbnr/pp2p1pp/8/2p2p2/3pP3/2NPBQ2/PPP2PPP/2KR1BNR b kq -";
        String FENToTest = null;
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.WCASTLEQ)
            {
                FENToTest = move.getPostBoardFEN();
                break;
            }
        }
        
        assertEquals(postCastleFEN, FENToTest);
    }
    
    @Test
    void testBlackCastleKingside()
    {
        setupBoard("rnbqk2r/pppp2pp/4pn2/5p2/1b1P2PP/2P5/P3PP2/RNBQKBNR b KQkq -");
        
        String postCastleFEN = "rnbq1rk1/pppp2pp/4pn2/5p2/1b1P2PP/2P5/P3PP2/RNBQKBNR w KQ -";
        String FENToTest = null;
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.BCASTLEK)
            {
                FENToTest = move.getPostBoardFEN();
                break;
            }
        }
        
        assertEquals(postCastleFEN, FENToTest);
    }
    
    @Test
    void testBlackCastleQueenside()
    {
        setupBoard("r3kbnr/pp2pppp/2n5/2pq1b2/1P1Pp1PP/2P2P2/P7/RNBQKBNR b KQkq g3");
        
        String postCastleFEN = "2kr1bnr/pp2pppp/2n5/2pq1b2/1P1Pp1PP/2P2P2/P7/RNBQKBNR w KQ -";
        String FENToTest = "";
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.BCASTLEQ)
            {
                FENToTest = move.getPostBoardFEN();
                break;
            }
        }
        
        assertEquals(postCastleFEN, FENToTest);
    }
    
    @Test
    void testPlayingEnPassant()
    {
        setupBoard("rnbqkbnr/ppp1pp1p/8/3pP1p1/8/8/PPPP1PPP/RNBQKBNR w KQkq d6");
        String FENToTest = "rnbqkbnr/ppp1pp1p/3P4/6p1/8/8/PPPP1PPP/RNBQKBNR b KQkq -";
        String postBoardFEN = "";
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.ENPASSANT)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;
            }
        }
        assertEquals(FENToTest, postBoardFEN);
    }
    
    @Test
    void testSettingEnPassant()
    {
        setupBoard(Chess.STARTINGPOS);
        
        String FENToTest = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3";
        String postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.toString().equals("e2e4"))
            {
                postBoardFEN = move.getPostBoardFEN();
                break;
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);
    }
    
    @Test
    void testPromotionToQueen()
    {
        setupPromoBoard(true);
        String FENToTest = "Qn1qkbnr/p4ppp/3p4/2p2b2/8/3P1P2/1PP3PP/RNBQKBNR b KQk -";
        String postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.PROMOTOQUEEN)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;        
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);
        
        setupPromoBoard(false);
        FENToTest = "rnbqkbnr/ppppp2p/8/2PP4/2B5/4PP1p/PP6/RNBQK1Nq w Qkq -";
        postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.PROMOTOQUEEN)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;        
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);   
    }
    
    @Test
    void testPromotionToRook()
    {
        setupPromoBoard(true);
        String FENToTest = "Rn1qkbnr/p4ppp/3p4/2p2b2/8/3P1P2/1PP3PP/RNBQKBNR b KQk -";
        String postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.PROMOTOROOK)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;        
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);
        setupPromoBoard(false);
        FENToTest = "rnbqkbnr/ppppp2p/8/2PP4/2B5/4PP1p/PP6/RNBQK1Nr w Qkq -";
        postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.PROMOTOROOK)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;        
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);
    }
    
    @Test
    void testPromotionToKnight()
    {
        setupPromoBoard(true);
        String FENToTest = "Nn1qkbnr/p4ppp/3p4/2p2b2/8/3P1P2/1PP3PP/RNBQKBNR b KQk -";
        String postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.PROMOTOKNIGHT)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;        
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);
        setupPromoBoard(false);
        FENToTest = "rnbqkbnr/ppppp2p/8/2PP4/2B5/4PP1p/PP6/RNBQK1Nn w Qkq -";
        postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.PROMOTOKNIGHT)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;        
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);
    }
    
    @Test
    void testPromotionToBishop()
    {
        setupPromoBoard(true);
        String FENToTest = "Bn1qkbnr/p4ppp/3p4/2p2b2/8/3P1P2/1PP3PP/RNBQKBNR b KQk -";
        String postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.PROMOTOBISHOP)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;        
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);    
        
        setupPromoBoard(false);
        FENToTest = "rnbqkbnr/ppppp2p/8/2PP4/2B5/4PP1p/PP6/RNBQK1Nb w Qkq -";
        postBoardFEN = "";
        
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.getSpecialMove() == SpecialMoves.PROMOTOBISHOP)
            {
                postBoardFEN = move.getPostBoardFEN();
                break;        
            }
        }
        
        assertEquals(FENToTest, postBoardFEN);
    }
    
    @Test
    void testCheckMate()
    {
        setupBoard("rnbqkbnr/ppppp2p/8/2PP3Q/2B2P2/4P2p/PP6/RNB1K1N1 b Qkq -");
        assert(chess.isCheckMate());
    }
    
    @Test
    void testStaleMate()
    {
        setupBoard("4k3/8/5Q2/2N5/8/8/8/4K3 b - -");
        assert(chess.isStaleMate());
    }
}
