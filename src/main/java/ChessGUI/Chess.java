/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author tasman
 * studentID: 21147547
 */
public class Chess {

    
    public static final String STARTINGPOS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    Player white; //the white player
    Player black; //the black player
    
    private boolean hasPlayers = false; //if the chessboard should ask the human for a move
    
    private ConcurrentHashMap<Integer, Piece> board = new ConcurrentHashMap<>(64);
    
    private Stack<Piece> capturedPieces = new Stack(); //a stack of pieces captured throughout the game
    private Stack<Pawn> promotedPawns = new Stack();
    
    private ArrayList<King> whiteKings = new ArrayList(); // list of kings on the board, done to counter having multiple kings on the board. 
    private ArrayList<King> blackKings = new ArrayList();
    
    private BoardState boardState;
    
    private Stack<Move> moveList = new Stack(); // Moves played in this game
    private Stack<Move> nextMoves = new Stack(); // a buffer of unsortedMoves to be played. used for rewatching games and for being able to return to the current game state during a game if the user rewinds the game.
    
    
    private ChessboardPanel chessBoard; //link to the chessboard panel
    
    private java.awt.List guiMoveList; //link to the gui move list
    
    
    
    private ChessClock clock; // link to clock
    private ClockPanel clockPanel; //link to clock display
    
    boolean clockSet = false;
    
    private boolean outOfTime = false; //the clock changes this value when a players time is up
    
    private boolean gameInProgess = false; //if a game is currently in progress
    
    private boolean endOfGame = false; //if we have reached the end of the game
    private Boolean winnerOfGameIsWhite = null; //null until a winner has been decided
    
    private static DBManager db = new DBManager(); //a link to the database, used for opening book
    
    private static StockfishConnector stockfish; //a link to stockfish
    
    private Position currentPosition; //the position we are in currently
    
    static {
        try {
            stockfish = new StockfishConnector(); //instantiate stockfish
        } catch (IOException ex) {
            Logger.getLogger(Chess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Chess(String FEN) //constructor for fen
    {
        
        try{
        parseFEN(FEN); //setup board  
        } catch (Exception e)
        {
            System.out.println("Something went wrong");
        }
        
    }
    
    public Chess(){};

    public Chess(String FEN, Player white, Player black) //set up a specific board with players
    {
        this(FEN);
        
        this.assignPlayers(white, black);
        setupClock();
    }
    
    public Chess(Player white, Player black) //set up a game with players
    {
        this(STARTINGPOS, white, black);
    }
    
    
   
    public void assignPlayers(Player white, Player black) //assign links to players
    {
        (this.white = white).addBoard(this);
        (this.black = black).addBoard(this);
        this.hasPlayers = true;
    }
    
    public void play(int whiteSeconds, int blackSeconds) // to start playing a game
    {
        if (!clockSet) //if the clock has already been set, skip
        {
            setClockTime(whiteSeconds, blackSeconds);
        } 
        
        this.startClock(); //start the clock
        SoundManager.startGame(); //play start sound
        gameInProgess = true; // game is now in progress
        
        while (!this.isGameOver()) // while the game is not over
        {
            makeMove((this.isWhiteToMove() ? white : black).toMove(), true); //ask the player for a move
            if (this.isInCheck()) //the player played a move, play sound according to board state
            {
                SoundManager.inCheck(); //play incheck sound
            } else {
                SoundManager.movePiece(); // play normal sound
            }
        }
        this.clock.stopClock();
        gameInProgess = false; // game is no longer is progress
        SoundManager.endGame(); // play end game sound
        if (this.isCheckMate()) // if it is checkmate
        {
            if(this.isWhiteToMove()) //white won
            {
                winnerOfGameIsWhite = true; 
            } else { //black won
                winnerOfGameIsWhite = false; 
            }
        } 
        
    }
    
    public static boolean doesFileExist(String filePath) //check if a file exists 
    {  
        File file = new File(filePath); 
        return file.exists();
    }
    
    public static ArrayList<String> loadFromFile(String filePath) {
        ArrayList<String> lines = new ArrayList<>();

        try (InputStream inputStream = Chess.class.getResourceAsStream(filePath);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    
    public static Move calculateBestMove(Chess chess, int depth) //use minimax to calculate best move
    {
        /*
        this method is a wrapper for miniMax.
        */
        if (depth <= 0) return null; 
        if (db.isConnected())
        {
            ResultSet bookMove = db.queryDB("SELECT nextMove FROM OpeningBook WHERE FEN = '" + chess.getFENString(false) + "'");
            //if this position is in the openingBook

            try {
                if (bookMove.next())
                {
                    String bestMove = bookMove.getString("nextMove"); //get the best move
                    Thread.sleep(500);
                    // Process the bestMove and return early.
                    return Move.getMove(bestMove, chess); //return this best move
                }

            } catch (SQLException ex) {
                Logger.getLogger(Chess.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Chess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //database is not up, or position was not in the openingBook, search instead
        Chess miniMaxChess = chess.replica(); //make a copy of the chess instance
        
        miniMax(miniMaxChess, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth);
        
        Position bestNextMove = Position.at(chess.getFENString(false)).getBestNextPosition();
        
        for (Move move : chess.getAllLegalMoves()){ //for all the legal unsortedMoves of the chess instance
            if (move.getPostBoardFEN().equals(bestNextMove.getFEN())) //if this is the right move
            {
                return move; //return the move
            }
        }
        return null; //something happened, please crash
    }
    
    public static double evaluatePosition(String FEN)
    {
        Chess chess = new Chess(FEN);
        return chess.evaluate();
    }
    
    public static Move calculateWorstMove(Chess chess, int depth) //use minimax to calculate best move
    {
        /*
        this method is a wrapper for miniMax.
        */
        if (depth <= 0) return null;
        
        Chess miniMaxChess = chess.replica(); //make a copy of the chess instance
        
        miniMax(miniMaxChess, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth); //search
        
        Position worstNextMove = Position.at(chess.getFENString(false)).getWorstNextPosition();
        
        for (Move move : chess.getAllLegalMoves()){ //for all the legal unsortedMoves of the chess instance
            if (move.getPostBoardFEN().equals(worstNextMove.getFEN())) //if this is the right move
            {
                return move; //return the move
            }
        }
        return null; //something happened, please crash
    }
    
    private static double miniMax(Chess chess, double alpha, double beta, int depth)
    {
        /*
        chess - the chess instance to perform computation on
        alpha - whites best evaluation achievable found down this line of moves
        beta - blacks best evaluation achievable found down this line of moves
        depth - how far down a line of sorted to search
        whiteToMove - which side we are playing for.
        */
        Position thisPosition = Position.at(chess.getFENString(false));
        boolean isGameOver = chess.isGameOver();
        if (depth == 0 || isGameOver) //if we are done searching or the game has ended
        {
            if (!chess.isInCheck() || !chess.moveList.peek().isCapture() || isGameOver) //the game will be evaluated when the king is not in check the last move was not a capture
            {
                double evaluation = chess.evaluate();
                
                thisPosition.setEval(evaluation);
                
                return evaluation; //return a static evaluation
            } else { //if we are in check or last move was a capture
                depth = 2; //search deeper
            }
        }
        
        double currentEval = (chess.isWhiteToMove() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
        
        for (Move move : chess.getAllLegalMoves()) //for all the legal moves in this position
        {
            chess.makeMove(move, false); //make this move

            String FEN = chess.getFENString(false); //get chess fen without move counters
            Position newPosition = Position.at(FEN); //get this new position

            double thisEval = miniMax(chess, alpha, beta, depth - 1); //recursively call this method

            chess.unMakeMove(false); //unmake the move
            
            if (!thisPosition.isChildrenFound())
            {
                thisPosition.addPosition(newPosition);
            } 
            
            currentEval = (chess.isWhiteToMove() ? Math.max(currentEval, thisEval) : Math.min(currentEval, thisEval)); // update currentEval if this move is a better move

            if (chess.isWhiteToMove())
            {
                alpha = Math.max(alpha, currentEval); // update alpha
            } else {
                beta = Math.min(beta, currentEval); // update beta
            }

            if (beta < alpha) break;

        }
        if (!thisPosition.isChildrenFound())
        {
            thisPosition.childrenFound();
        } else {
            thisPosition.resortPositions();
        }
        
        return currentEval;
    }


    
    private static final int[] mgPawnTable = { //middle game pawn bias
            0,   0,   0,   0,   0,   0,  0,   0,
            98, 134,  61,  95,  68, 126, 34, -11,
            -6,   7,  26,  31,  65,  56, 25, -20,
            -14,  13,   6,  21,  23,  12, 17, -23,
            -27,  -2,  -5,  12,  17,   6, 10, -25,
            -26,  -4,  -4, -10,   3,   3, 33, -12,
            -35,  -1, -20, -23, -15,  24, 38, -22,
            0,   0,   0,   0,   0,   0,  0,   0
    };

    private static final int[] egPawnTable = { //end game pawn bias
            0,   0,   0,   0,   0,   0,   0,   0,
            178, 173, 158, 134, 147, 132, 165, 187,
            94, 100,  85,  67,  56,  53,  82,  84,
            32,  24,  13,   5,  -2,   4,  17,  17,
            13,   9,  -3,  -7,  -7,  -8,   3,  -1,
            4,   7,  -6,   1,   0,  -5,  -1,  -8,
            13,   8,   8,  10,  13,   0,   2,  -7,
            0,   0,   0,   0,   0,   0,   0,   0
    };

    private static final int[] mgKnightTable = { //middle game knight bias
            -167, -89, -34, -49,  61, -97, -15, -107,
            -73, -41,  72,  36,  23,  62,   7,  -17,
            -47,  60,  37,  65,  84, 129,  73,   44,
            -9,  17,  19,  53,  37,  69,  18,   22,
            -13,   4,  16,  13,  28,  19,  21,   -8,
            -23,  -9,  12,  10,  19,  17,  25,  -16,
            -29, -53, -12,  -3,  -1,  18, -14,  -19,
            -105, -21, -58, -33, -17, -28, -19,  -23
    };

    private static final int[] egKnightTable = { //end game knight bias
            -58, -38, -13, -28, -31, -27, -63, -99,
            -25,  -8, -25,  -2,  -9, -25, -24, -52,
            -24, -20,  10,   9,  -1,  -9, -19, -41,
            -17,   3,  22,  22,  22,  11,   8, -18,
            -18,  -6,  16,  25,  16,  17,   4, -18,
            -23,  -3,  -1,  15,  10,  -3, -20, -22,
            -42, -20, -10,  -5,  -2, -20, -23, -44,
            -29, -51, -23, -15, -22, -18, -50, -64
    };

    private static final int[] mgBishopTable = { //middle game bishop bias
            -29,   4, -82, -37, -25, -42,   7,  -8,
            -26,  16, -18, -13,  30,  59,  18, -47,
            -16,  37,  43,  40,  35,  50,  37,  -2,
            -4,   5,  19,  50,  37,  37,   7,  -2,
            -6,  13,  13,  26,  34,  12,  10,   4,
            0,  15,  15,  15,  14,  27,  18,  10,
            4,  15,  16,   0,   7,  21,  33,   1,
            -33,  -3, -14, -21, -13, -12, -39, -21
    };
    
    private static final int[] egBishopTable = { //end game bishop bias
            -14, -21, -11,  -8, -7,  -9, -17, -24,
            -8,  -4,   7, -12, -3, -13,  -4, -14,
            2,  -8,   0,  -1, -2,   6,   0,   4,
            -3,   9,  12,   9, 14, 10,   3,   2,
            -6,   3,  13,  19,  7,  10,  -3,  -9,
            -12,  -3,   8,  10, 13,   3,  -7, -15,
            -14, -18,  -7,  -1,  4,  -9, -15, -27,
            -23,  -9, -23,  -5, -9, -16,  -5, -17
    };

    private static final int[] mgRookTable = { // middle game rook bias
            32,  42,  32,  51, 63,  9,  31,  43,
            27,  32,  58,  62, 80, 67,  26,  44,
            -5,  19,  26,  36, 17, 45,  61,  16,
            -24, -11,   7,  26, 24, 35,  -8, -20,
            -36, -26, -12,  -1,  9, -7,   6, -23,
            -45, -25, -16, -17,  3,  0,  -5, -33,
            -44, -16, -20,  -9, -1, 11,  -6, -71,
            -19, -13,   1,  17, 16,  7, -37, -26
    };

    private static final int[] egRookTable = { //end game rook bias
            13, 10, 18, 15, 12,  12,   8,   5,
            11, 13, 13, 11, -3,   3,   8,   3,
            7,  7,  7,  5,  4,  -3,  -5,  -3,
            4,  3, 13,  1,  2,   1,  -1,   2,
            3,  5,  8,  4, -5,  -6,  -8, -11,
            -4,  0, -5, -1, -7, -12,  -8, -16,
            -6, -6,  0,  2, -9,  -9, -11,  -3,
            -9,  2,  3, -1, -5, -13,   4, -20
    };

    private static final int[] mgQueenTable = {//middle game queen bias
            -28,   0,  29,  12,  59,  44,  43,  45,
            -24, -39,  -5,   1, -16,  57,  28,  54,
            -13, -17,   7,   8,  29,  56,  47,  57,
            -27, -27, -16, -16,  -1,  17,  -2,   1,
            -9, -26,  -9, -10,  -2,  -4,   3,  -3,
            -14,   2, -11,  -2,  -5,   2,  14,   5,
            -35,  -8,  11,   2,   8,  15,  -3,   1,
            -1, -18,  -9,  10, -15, -25, -31, -50
    };

    private static final int[] egQueenTable = {//end game queen bias
            -9,  22,  22,  27,  27,  19,  10,  20,
            -17,  20,  32,  41,  58,  25,  30,   0,
            -20,   6,   9,  49,  47,  35,  19,   9,
            3,  22,  24,  45,  57,  40,  57,  36,
            -18,  28,  19,  47,  31,  34,  39,  23,
            -16, -27,  15,   6,   9,  17,  10,   5,
            -22, -23, -30, -16, -16, -23, -36, -32,
            -33, -28, -22, -43,  -5, -32, -20, -41
    };

    private static final int[] mgKingTable = { //middle game king bias
            -65,  23,  16, -15, -56, -34,   2, 13,
            29,  -1, -20,  -7,  -8,  -4, -38, -29,
            -9,  24,   2, -16, -20,   6,  22, -22,
            -17, -20, -12, -27, -30, -25, -14, -36,
            -49,  -1, -27, -39, -46, -44, -33, -51,
            -14, -14, -22, -46, -44, -30, -15, -27,
            1,   7,  -8, -64, -43, -16,   9,   8,
            -15,  36,  12, -54,   8, -28,  24,  14
    };                         
    
    

    private static final int[] egKingTable = { //end game king bias
            -74, -35, -18, -18, -11,  15,   4, -17,
            -12,  17,  14,  17,  17,  38,  23,  11,
            10,  17,  23,  15,  20,  45,  44,  13,
            -8,  22,  24,  27,  26,  33,  26,   3,
            -18,  -4,  21,  24,  27,  23,   9, -11,
            -19,  -3,  11,  21,  23,  16,   7,  -9,
            -27, -11,   4,  13,  14,   4,  -5, -17,
            -53, -34, -21, -11, -28, -14, -24, -43
    };  

    public boolean isEndGame() //definition of if we are in an end game
    {
        int numOfMajorWhitePieces = 0; //declare number of major white pieces
        int numOfMajorBlackPieces = 0; //decalre number of major black pieces
        for (Piece whitePiece : this.getPlayingPieces(true)) //for all the white pieces
        {
            if (!(whitePiece instanceof Pawn)) //for all pieces that are not pawns
            {
                numOfMajorWhitePieces++; //increment white major pieces
            }
        }
        for (Piece blackPiece : this.getPlayingPieces(false)) //for all the black pieces
        {
            if (!(blackPiece instanceof Pawn)) //for all pieces that are not pawns
            {
                numOfMajorBlackPieces++; //increment black major pieces
            }
        }
        
        return Math.min(numOfMajorWhitePieces, numOfMajorBlackPieces) < 5; // if either of these values are below 5, we are in an endgame 
    }
    
    
    public double squareEval(Piece piece) //for any piece anywhere on the board, return its bias
    {
        int[] pieceTable = null; //create link to the piece table we are about to get from
        boolean isEndGame = this.isEndGame(); //whether we are in an end game or not.
        switch (piece.ID) {
            case 1: // pawn
                if (!isEndGame)
                {
                    pieceTable = mgPawnTable;
                } else {
                    pieceTable = egPawnTable;
                }
                break;
            case 2: // knight
                if (!isEndGame)
                {
                    pieceTable = mgKnightTable;
                } else {
                    pieceTable = egKnightTable;
                }
                break;
            case 3: // biship
                if (!isEndGame)
                {
                    pieceTable = mgBishopTable;
                } else {
                    pieceTable = egBishopTable;
                }
                break;
            case 4: // rook
                if (!isEndGame)
                {
                    pieceTable = mgRookTable;
                } else {
                    pieceTable = egRookTable;
                }
                break;
            case 5: // queen
                if (!isEndGame)
                {
                    pieceTable = mgQueenTable;
                } else {
                    pieceTable = egQueenTable;
                }
                break;
            case 6: // king
                if (!isEndGame)
                {
                    pieceTable = mgKingTable;
                } else {
                    pieceTable = egKingTable;
                }
                break;
        }
        int index;
        
        if (piece.isWhite())
        {
            index = piece.getX() + ((7 - piece.getY()) * 8);
        } else {
            index = piece.getX() + (piece.getY() * 8);
        }
        
        return pieceTable[index];
    }
    
    public double evaluate() //evaluate the position
    {        
        double evaluation = 0; //declare evaluation
        if (this.isGameOver()) //if the game is over
        {
            if (this.isCheckMate()) //if it is checkmate
            {
                if (this.isWhiteToMove()) //if it is white to play
                {
                    return -99999; //white lost, return very low value, but a value that is higher than negative infinity
                } else {
                    return 99999; //black lost, return a very high value, but a value that is lower than positive infinity
                }
            } else {
                return 0; //this is stalemate, draw
            }
        }
        for (Piece piece : board.values())
        {
            evaluation += (piece.isWhite() ? 1 : -1) * ((piece.getValue()) + squareEval(piece)); //add the piece value and its bias
        }
        return evaluation; //return the evaluation
    }
    
    public void addMoveList(java.awt.List guiMoveList) //link gui move list
    {
        this.guiMoveList = guiMoveList; //set link to gui move list
        this.refreshMoveList(); //refresh the move list
    }
    
    public void addPiece(Piece piece) //add a piece to the chessboard
    {
        if (piece == null) return; //should never be null
        
        if (piece instanceof King) //if this is a king
        {
            addKing((King) piece); //add it to the list of kings
        }
        board.put(piece.getSquare().hashCode(), piece); //place piece on the chessboard
    }
    
    public void addKing(King king)
    {
        if (king.isWhite()) 
        {
            whiteKings.add(king); //add this king to the white King list
        } else {
            blackKings.add(king); //add this king to the black King list
        }
              
    }
    
    public void addChessboard(ChessboardPanel chessBoard) //add a link to the JFrame chessboard
    {
        this.chessBoard = chessBoard;
    }
    
    public void addClockPanel(ClockPanel clockPanel) //add a link to the JFrame ChessClock
    {
        this.clockPanel = clockPanel;
    }
    
    public ChessboardPanel getChessboard() 
    {
        return chessBoard;
    }
    
    public ClockPanel getClockPanel()
    {
        return this.clockPanel;
    }
    
    private void capturePiece(Piece piece) //capture a piece on the board
    {
        if (piece == null) return; //should never be null
        capturedPieces.push(piece); // add this piece to the stack of captured pieces
        removePieceFromBoard(piece); //remove this piece from the chessboard
    }
    
    public boolean canCastle(boolean isWhite, boolean isKingside) { //can the white/black player castle kingside/queenside
        if (this.isInCheck() || !boardState.getCastlingRights(isWhite, isKingside)) return false; //we cannot castle if we are in check, or we have already lost castling rights
        
        
        
        Square squareOne; //link to the first square the king must travel
        Square squareTwo; //link to the second square the king must travel
        Square squareThree = null; //the square to ensure is not occupied for the rook to travel over this square when castling queenside
        
         
        // Check if white kingside castling is allowed
        if (isWhite) 
        {
            if (isKingside) //white is trying to castle kingside
            {
                squareOne = Square.F1; 
                squareTwo = Square.G1;
            } else {        //white is trying to castle queenside
                squareOne = Square.D1;
                squareTwo = Square.C1;
                squareThree = Square.B1;
            }
        } else {
             if (isKingside)//black is trying to castle kingside
            {
                squareOne = Square.F8;
                squareTwo = Square.G8;
            } else {        //black is trying to castle queenside
                squareOne = Square.D8;
                squareTwo = Square.C8;
                squareThree = Square.B8; 
            }
        }
        if (this.getPiece(squareOne) != null || this.getPiece(squareTwo) != null || (squareThree == null ? false : this.getPiece(squareThree) != null)) //make sure non of these square are occupied
        {
            return false; 
        }
        King king = (King) this.getPiece((isWhite ? Square.E1 : Square.E8)); //get the king trying to castle
        
        return !(king.isTargetted(squareOne) || king.isTargetted(squareTwo)); //return true if the king is not targetted on either squareOne or squareTwo
        
    }
    
    public boolean isWhiteToMove()
    {
        return boardState.isWhiteToMove();
    }
    
    public void makeMove(Move move, boolean isGameMove) //make a move on the board
    {
        this.getMoveList().push(move);//push this move on the movelist stack
        move.addBoardState(boardState); //add the current boardstate to the move
        this.movePiece(move); //move the piece on the chessboard
        
        
        if (move.getSpecialMove() != null) //if this is a special chess move; Castling, Promotion, or enPassant
        {
            this.makeSpecialMove(move);
            boardState.setEnPassant(null); //no enPassant Square in this position
        } else if (move.isDoublePawnPush()) //if this was a double pawn push, we need to set the enPassant Square
        {
            boardState.setEnPassant(Square.at(move.getFromSquare().getX(), move.getToSquare().getY() + (this.isWhiteToMove() ? -1 : 1))); //set EnPassant Square
        } else {
            boardState.setEnPassant(null); //no enPassant Square in this position
        }
        
        this.postMoveBoardAssessment(); //check castling rights, and change half and full move counters
        
        if (isGameMove) { //if this was a move played in a game
            this.repaintBoard(); //repaint the chessboard
            clock.endTurn(); //switch turns on the clock
        }
        
    }
    
    private void makeSpecialMove(Move move) //make the special move changes to the board
    {
        SpecialMoves specialMove = move.getSpecialMove();
        switch(specialMove)
        {
            case WCASTLEK:
            case WCASTLEQ:
            case BCASTLEK:
            case BCASTLEQ: //if we are trying to castle
                makeCastle(specialMove);
                break;
            case ENPASSANT: //if we are performing enPassant
                makeEnPassant(move);
                break;
            case PROMOTOROOK:
            case PROMOTOQUEEN:
            case PROMOTOBISHOP:
            case PROMOTOKNIGHT: //if we are promoting
                makePromo(move);
                break;
        }
    }
    
    private void makeCastle(SpecialMoves YcastleX) //make the changes to the board for castling
    {
        Piece rookFromSquare; //the original square of the rook
        Square rookToSquare; //the placement of the rook after castling
        
        
        
        switch (YcastleX) {
            case WCASTLEK: //if white is castling kingside
                rookFromSquare = this.getPiece(Square.H1);
                rookToSquare = Square.F1;
                this.moveCastle(rookFromSquare, rookToSquare);
                break;

            case WCASTLEQ: //if white is castling queenside
                rookFromSquare = this.getPiece(Square.A1);
                rookToSquare = Square.D1;
                this.moveCastle(rookFromSquare, rookToSquare);
                break;

            case BCASTLEK: //if black is castling kingside
                rookFromSquare = this.getPiece(Square.H8);
                rookToSquare = Square.F8;
                this.moveCastle(rookFromSquare, rookToSquare);
                break;

            case BCASTLEQ: //if black is castling queenside
                rookFromSquare = this.getPiece(Square.A8);
                rookToSquare = Square.D8;
                this.moveCastle(rookFromSquare, rookToSquare);
                break;
        }    
    }
    
    private void makeEnPassant(Move move) //make the changes to the board after enPassant, capturing the pawn
    {
        this.capturePiece(move.getPieceToCapture()); //capture the enPassant piece
    }
    
    private void makePromo(Move move) //make the changes to the board after Promotion
    {
        SpecialMoves specialMove = move.getSpecialMove(); 
        removePieceFromBoard(move.getPieceToMove()); //remove the promoting pawn from the board
        promotedPawns.push((Pawn) move.getPieceToMove()); //add this pawn to the promotedPawns list
        
        boolean isWhite = move.getPieceToMove().isWhite();
        int x = move.getPieceToMove().getX();
        int y = move.getPieceToMove().getY();
        
        Piece promotionPiece = null;
        
        switch(specialMove)
        {
            case PROMOTOQUEEN:
                promotionPiece = Queen.at(x,y, isWhite, this); //promote to queen
                break;
            case PROMOTOROOK:
                promotionPiece = Rook.at(x,y, isWhite, this); //promote to rook
                break;
            case PROMOTOKNIGHT:
                promotionPiece = Knight.at(x,y, isWhite, this); //promote to knight
                break;
            case PROMOTOBISHOP:
                promotionPiece = Bishop.at(x,y, isWhite, this); //promote to bishop
                break;                    
        }
        this.addPiece(promotionPiece); //add the promoted piece to the board
        
    }
    
    public ArrayList<Piece> getPlayingPieces()
    {
        ArrayList<Piece> playingPieces = new ArrayList(); //make new arrayList
        playingPieces.addAll(board.values()); //add all values that are on the boards hashmap - the pieces
        return playingPieces; //return the pieces that are on the board
    }
    
    public  ArrayList<Piece> getPlayingPieces(boolean isWhite)
    {
        ArrayList<Piece> pieces = new ArrayList();
        for (Piece piece : this.getPlayingPieces()) //for all the playing pieces
        {
            if (piece.isWhite() == isWhite) //only select the color we want
            {
                pieces.add(piece); //add this piece to the list
            }
        }
        return pieces; //return the sided pieces
    }
    
    public Square getEnPassant() //return the enPassant Square
    {
        return boardState.getEnPassant();
    }
    
    public String getFENString() //get the current fen String
    {
        return this.getFENString(true);
    }
    
    public String getFENString(boolean getMoveCounters) //get fen string but without move counters, for transposition
    {
        String FEN = "";
        
        for (int y = 7; y >= 0; y--) { //create string representation of board
            int gaps = 0;
            for (int x = 0; x <= 7; x++) {
                if (this.getPiece(x, y) != null) {
                    if (gaps != 0) {
                        FEN += String.valueOf(gaps);
                        gaps = 0;
                    }
                    FEN += this.getPiece(x, y).toString();
                } else if (x == 7) {
                    gaps++;
                    FEN += String.valueOf(gaps);
                } else {
                    gaps++;
                }
            }
            if (y != 0) {
                FEN += "/";
            }
        }
        
        FEN += " " + (this.isWhiteToMove() ? "w" : "b"); // append whos turn it is to string
        
        
        if (!boardState.isBlackCanCastleKingside() && !boardState.isBlackCanCastleQueenside() && !boardState.isWhiteCanCastleKingside() && !boardState.isWhiteCanCastleQueenside()) { // append castling rights to string
            FEN += " -";
        } else {
            FEN += " ";
            if (boardState.isWhiteCanCastleKingside()) { //white can castle Kingside
                FEN += "K";
            }
            if (boardState.isWhiteCanCastleQueenside()) { //white can castle Queenside
                FEN += "Q";
            }
            if (boardState.isBlackCanCastleKingside()) { //black can castle Kingside
                FEN += "k";
            }
            if (boardState.isBlackCanCastleQueenside()) { //white can caslte Queenside
                FEN += "q";
            }
        }
        if (boardState.getEnPassant() == null) { //append en passant to string
            FEN += " -";
        } else {
            FEN += " " + boardState.getEnPassant().toAlgebraic();
        }
        if (!getMoveCounters) //should we get move counters
        {
            return FEN; //return the fen early
        }
        FEN += " " + String.valueOf(boardState.getHalfMove()) + " " + String.valueOf(boardState.getFullMove()); //append half and full unsortedMoves to string
        return FEN;
    }
    
    public Piece getPiece(Square square) //get the piece from the board
    {
        return board.get(square.hashCode()); // return the piece
    }
    
    public Piece getPiece(int x, int y) //get the piece at a certain coordinate
    {
        return board.get((x + (y * 8))); //return the piece
    }
    
    private Move getPreviousMove() //return the previous move and removing it from the move list
    {
        return this.getPreviousMove(true);
    }
    
    private Move getPreviousMove(boolean pop) //return the previous move with option to remove from the move list
    {
        if (!this.moveList.isEmpty()){ //if the move list is not empty
            return (pop ? getMoveList().pop() : getMoveList().peek()); //return the last move
        } else return null; //the move list is empty, return null
    }
    
    public ArrayList<Move> getAllLegalMoves() //return all legal moves in the position with option to sort the moves
    {
        ArrayList<Move> unsortedMoves = new ArrayList();
        for (Piece piece : (this.getPlayingPieces(this.isWhiteToMove()))) //for all pieces in this position
        {
            for (Move move : this.getLegalPieceMoves(piece)) //for all the legal moves for this piece
            {
                unsortedMoves.add(move); //add this move to the unsortedMoves list
            }
        }
        
        Position thisPosition = Position.at(this.getFENString(false));
        
        if (thisPosition.isChildrenFound()) //if we should sort the list, use past evaluations to check which moves we should do first
        {
            ArrayList<Move> sortedMoves = new ArrayList();
            
            Position[] nextPositions = thisPosition.getNextPositions(); //get sorted next position fens
            for (Position position : nextPositions) //for all possible next positions
            {
                for (Move move : unsortedMoves) //for all moves
                {
                    if (move.getPostBoardFEN().equals(position.getFEN())) //if this is the right move
                    { 
                        sortedMoves.add(move); //add it to the list
                        break;
                    }
                }
            }
            return sortedMoves; //return list of sortedMoves;
        }
        
        //we dont need to sort the list
        Collections.sort(unsortedMoves); //use move to move sorting method
        
        return unsortedMoves; //return 
    }
    
    public ChessClock getClock() //return the clock object
    {
        return this.clock;
    }
    
    public ArrayList<Move> getLegalPieceMoves(Piece piece) //get all the legal moves for a piece
    {
        ArrayList<Move> moves = new ArrayList(); //make a list of moves
        if (piece.isWhite() != this.isWhiteToMove()) return moves; //if this piece shouldnt be making a move, return early
        for (Move move : piece.getMoves()) //for all the moves that this piece can play in this position
        {
            if (this.isInCheckAfterMove(move)) continue; //if this move puts this king in check after making this move, do not add it 
            moves.add(move); //add this move to the list
        }
        return moves; //return list
    }
    
    public ArrayList<King> getKings(boolean isWhite) //return the list of king for a given side
    {
        return (isWhite ? whiteKings : blackKings); 
    }
    
    public boolean hasNextMove() //if the board state is not at the latest state, used for looking at past moves in the current game
    {
        return (!nextMoves.isEmpty()); 
    }
    
    public boolean hasChessBoard() 
    {
        return this.chessBoard != null;
    }
    
    public boolean isGameOver() 
    {
        return endOfGame = this.isCheckMate() || this.isStaleMate() || this.isOutOfTime() || this.isRepeatedMoves();
    }
    
    public boolean isCheckMate() 
    {
        return this.getAllLegalMoves().isEmpty() && this.isInCheck();
    }
    
    public boolean isStaleMate() 
    {
        return this.getAllLegalMoves().isEmpty() && !this.isInCheck();
    }
    
    public boolean isOutOfTime()
    {
        return outOfTime;
    }
    
    public boolean isRepeatedMoves()
    {
        if (getMoveList().size() < 10) return false;
        
        
        //check if corrosponding moves match
        boolean isRepeated = this.getNthToLastMove(0).equals(this.getNthToLastMove(4)) && this.getNthToLastMove(4).equals(this.getNthToLastMove(8)); 
        isRepeated = isRepeated && (this.getNthToLastMove(1).equals(this.getNthToLastMove(5)) && this.getNthToLastMove(5).equals(this.getNthToLastMove(9))); 
        isRepeated = isRepeated && this.getNthToLastMove(2).equals(this.getNthToLastMove(6)) && this.getNthToLastMove(3).equals(this.getNthToLastMove(7));
        
        return isRepeated; //return true if corrospoding moves match
    }
    
    public boolean isFiftyMoveRule() //if we have reached the fifty move rule
    {
        if (boardState.getHalfMove() >= 100) //if the half move counter is 100 or over
        {
            return true; //we have reached the fifty move rule, end game
        } else {
            return false; //we have not yet reached the fifty move rule
        }
    }
    
    private Move getNthToLastMove(int n) {
        if (getMoveList().size() > n) {
            return getMoveList().get(getMoveList().size() - (n+1));
        } else return null;
    }
    
    private boolean isInCheck() //if the king is in check in this position
    {
        ArrayList<King> kingList = getKings(this.isWhiteToMove()); //get the list of kings to check
        for (King king : kingList) //for all the kings on a side
        {
            if (king.isTargetted()) //if the king is targetted
            {
                return true; //return true, the king is in check
            }
        }
        return false; //return false, the king is not in check
    }
    
    public void nextMove() //make next move
    {
        if (this.hasNextMove())
        {
            this.makeMove(nextMoves.pop(), true);
        }
    }
    
    public void movePiece(Move move) //move a piece on the board
    {
        board.remove(move.getPieceToMove().getSquare().hashCode()); //remove the piece from the board
        
        if (move.isCapture()) //if this move captures a piece
        {
            capturePiece(move.getPieceToCapture()); //capture the piece to be captured
        }
        move.getPieceToMove().updateLocation(move.getToSquare()); //update the location of the piece
        board.put(move.getToSquare().hashCode(), move.getPieceToMove()); //put the piece back on the board
    }
    
    public void parseFEN(String FEN) throws Exception //setup board with a FEN string
    { //given a string, construct the chessboard
        Exception invalidFEN = new Exception("Invalid FEN String");
        
        String[] fenParts = FEN.split(" ");
        
        //Configure Board
        String[] rowsOfPieces = fenParts[0].split("/");
        int y = 7;
        for (String pieces : rowsOfPieces) { //create the chessboard with pieces
            int x = 0;
            for (String piece : pieces.split("")) {
                try {
                    int decrement = Integer.parseInt(piece);
                    for (int i = 0; i < decrement; i++) {
                        x++;
                    }
                } catch (NumberFormatException e) {
                    switch (piece) {
                        case "p":
                            addPiece(Pawn.at(x, y, false, this));
                            break;
                        case "r":
                            addPiece(Rook.at(x, y, false, this));
                            break;
                        case "n":
                            addPiece(Knight.at(x, y, false, this));
                            break;
                        case "b":
                            addPiece(Bishop.at(x, y, false, this));
                            break;
                        case "q":
                            addPiece(Queen.at(x, y, false, this));
                            break;
                        case "k":
                            addPiece(King.at(x, y, false, this));
                            break;
                        case "P":
                            addPiece(Pawn.at(x, y, true, this));
                            break;
                        case "R":
                            addPiece(Rook.at(x, y, true, this));
                            break;
                        case "N":
                            addPiece(Knight.at(x, y, true, this));
                            break;
                        case "B":
                            addPiece(Bishop.at(x, y, true, this));
                            break;
                        case "Q":
                            addPiece(Queen.at(x, y, true, this));
                            break;
                        case "K":
                            addPiece(King.at(x, y, true, this));
                            break;
                        default:
                            throw invalidFEN;
                    }
                    x++;
                }
            }
            if (x != 8) {
                throw invalidFEN;
            }
            y--;
        }
        if (y != -1) {
            throw invalidFEN;
        }
        //Board is configured
        //Set turn
        
        boolean whiteKingsideCastle = false;
        boolean whiteQueensideCastle = false;
        boolean blackKingsideCastle = false;
        boolean blackQueensideCastle = false;

        int halfMove = 0; // halfmoves, increments after every turn and reset to 0 when a pawn is pushed or a piece is captured, game ends in stalemate if this value equals 100
        int fullMove = 0; // the amount of times black has played.

        Square enPassant = null;

        boolean whiteToMove;
        
        switch (fenParts[1]) { //set who it is to move
            case "w":
                whiteToMove = true;
                break;
            case "b":
                whiteToMove = false;
                break;
            default:
                throw invalidFEN;
        }
        //Castling Rights
        if (!fenParts[2].equals("-")) {
            String[] castlingRightsList = fenParts[2].split("");
            for (String right : castlingRightsList) {
                switch (right) { //set castling rights
                    case "K":
                        whiteKingsideCastle = true;
                        break;
                    case "Q":
                        whiteQueensideCastle = true;
                        break;
                    case "k":
                        blackKingsideCastle = true;
                        break;
                    case "q":
                        blackQueensideCastle = true;
                        break;
                    default:
                        throw invalidFEN;
                }
            }
        }
        //En Passant
        if (!fenParts[3].equals("-")) {
            enPassant = Square.at(fenParts[3]);
        } 
        
        if (fenParts.length > 4)
        {
            //halfmove
            try {
            halfMove = Integer.parseInt(fenParts[4]);
            } catch (NumberFormatException e) {
                throw invalidFEN;
            }
            //fullmove
            try {
                fullMove = Integer.parseInt(fenParts[5]);
            } catch (NumberFormatException e) {
                throw invalidFEN;
            }
        }
        currentPosition = Position.at(FEN); //set position
        this.boardState = new BoardState(whiteKingsideCastle,whiteQueensideCastle,blackKingsideCastle,blackQueensideCastle, halfMove, fullMove, enPassant, whiteToMove);
        
    }
    
    public int perft(int depth) //depth search testing.
    {
        if (depth == 0) return 1;
        if (depth < 0) return 0;
        Chess perftChess = this.replica(); //make replica chess object
        int totalNodes = 0;
        for (Move move : perftChess.getAllLegalMoves()) //for all the moves in this position
        {
            perftChess.makeMove(move, false); //make the move on the chessboard
            int thisRoundNodes = perftChess.perft(depth, perftChess); //tally this moves nodes
            totalNodes += thisRoundNodes; //add this tally to total nodes
            perftChess.unMakeMove(false); //unmake this move
        }
        return totalNodes; //return the sum of total nodes
    }
    
    private int perft(int depth, Chess perftChess) //depth search testing
    {        
        if (depth <= 1) //if depth is less that one
        {
            return 1; //this is one node
        } 
        if (perftChess.isGameOver()) //if the game is now over
        {
            return 0; //return 0, no moves
        }
        int totalNodes = 0; 
        for (Move move : perftChess.getAllLegalMoves()) //for all legal moves in this position
        {
            perftChess.makeMove(move, false); //make the move
            totalNodes += perftChess.perft(depth - 1, perftChess); //tally the total nodes
            perftChess.unMakeMove(false); //unmake the move
        } 
        return totalNodes; //return sum of total nodes
    }
    
    public void previousMove() //go back one move
    {
        if (!this.moveList.isEmpty())
        {
            this.unMakeMove(true);
        }   
    }
    private boolean putMyselfInCheck() //if it is blacks turn and white is in check, or vise versa
    {
        ArrayList<King> kingList = (this.isWhiteToMove() ? blackKings : whiteKings);
        for (King king : kingList)
        {
            if (king.isTargetted(king))
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isInCheckAfterMove(Move move) //if this move puts the king in check
    {
        makeMove(move, false); //make the move
        boolean out = putMyselfInCheck(); //did this move put me in check
        move.setPostBoardFEN(this.getFENString(false)); //while we are here, add post chessboard fen to move, since we just did that
        unMakeMove(false); //unmake the move
        return out; //return if we put ourselves in check
    }
    
    private void repaintBoard() //repaint the chessboard
    { 
        if (this.hasChessBoard()) //if there is a chessboard to repaint
        {
            chessBoard.safeRepaint(); //repaint the chessboard
        }
        
        if(this.guiMoveList != null) //if there is a gui move list to repaint
        {
            this.refreshMoveList(); //repaint the gui move list
        }
    }
        
    private void returnPiece(Move move) //return piece, used after making a move
    {
        board.remove(move.getToSquare().hashCode());
        
        if (move.isCapture())
        {
            this.restoreCapturedPiece();
        }
        move.getPieceToMove().updateLocation(move.getFromSquare());
        board.put(move.getFromSquare().hashCode(), move.getPieceToMove());
    }
    
    private void restoreCapturedPiece() //restore captured piece
    {
        this.addPiece(capturedPieces.pop()); //remove this piece from the capturedPiece stack and add it to the chessboard
    }
    
    private void removePieceFromBoard(Piece piece) //remove this piece from the chessboard
    {
        board.remove(piece.getSquare().hashCode()); 
    }    
    
    public Chess replica() //create same but unique chess object
    {
        return new Chess(this.getFENString());  
    }
    
    private void setupClock() //setup the chessclock
    {
        this.clock = new ChessClock(this);
    }
    
    private void setClockTime(int whiteSeconds, int blackSeconds) //set the chess clock time
    {
        clock.setTime(whiteSeconds, blackSeconds);
        clockSet = true;
    }
    
    private void startClock() //start the chess clock
    {
        clock.start();
    }
    
    @Override
    public String toString() 
    { //ascii representation of chess board
        String textBoard = "";
        if (this.isWhiteToMove())
        {
            textBoard += "    a   b   c   d   e   f   g   h\n  --------------------------------\n";
            for (int y = 7; y >= 0; y--) {
                textBoard += String.valueOf(y + 1);
                for (int x = 0; x <= 7; x++) {
                    textBoard += " | " + (this.getPiece(x, y) == null ? " " : this.getPiece(x, y).toString());
                }
                textBoard += " |";
                textBoard += String.valueOf(y + 1);
                textBoard += "\n  --------------------------------\n"; 
            }
            textBoard += "    a   b   c   d   e   f   g   h\n";
        } else {
            textBoard += "    h   g   f   e   d   c   b   a\n  --------------------------------\n";
            for (int y = 0; y <= 7; y++) {
                textBoard += String.valueOf(y + 1);
                for (int x = 7; x >= 0; x--) {
                    textBoard += " | " + (this.getPiece(x, y) == null ? " " : this.getPiece(x, y).toString());
                }
                textBoard += " |";
                textBoard += String.valueOf(y + 1);
                textBoard += "\n  --------------------------------\n"; 
            }
            textBoard += "    h   g   f   e   d   c   b   a\n";
        }
        
        return textBoard;
    }
    
    public void unMakeMove(boolean inGame) //unmake a move on the chessboard
    {
        Move move = this.getPreviousMove();
        
        if (move.getSpecialMove() != null) //if last move was a special move; Castling, promotion, or enPassant
        {
            this.unMakeSpecialMove(move); //undo these changes
        }
        
        this.returnPiece(move); //return the piece to the board
        boardState.update(move.getBoardState()); //update this board with the board state from the move
        if (inGame) this.repaintBoard(); //if this is a move in a game, repaint the board
        
    }
    
    private void unMakeSpecialMove(Move move) //undo changes made by the special move
    {
        SpecialMoves specialMove = move.getSpecialMove(); //get the speical move 
        switch(specialMove) 
        {
            case WCASTLEK:
            case WCASTLEQ:
            case BCASTLEK:
            case BCASTLEQ: //if the last move was castling
                unMakeCastle(specialMove); //undo castling changes
                break;
            case ENPASSANT: //if the last move was enPassant
                this.restoreCapturedPiece(); //restore the captured pawn
                break;
            case PROMOTOROOK:
            case PROMOTOQUEEN:
            case PROMOTOBISHOP:
            case PROMOTOKNIGHT: //if the last move was promotion
                unMakePromo(move); //undo changes made when promoting
                break;
        }
    }
    
    private void unMakeCastle(SpecialMoves YcastleX) //undo changes made by castling
    {
        Piece fromSquare; //the destination of the rook after castling
        Square toSquare; //the position of the rook before castling
        
        switch (YcastleX) {
            case WCASTLEK: //if white castled kingside
                fromSquare = this.getPiece(Square.F1);
                toSquare = Square.H1;
                this.moveCastle(fromSquare, toSquare);
                break;

            case WCASTLEQ: //if white castled queenside
                fromSquare = this.getPiece(Square.D1);
                toSquare = Square.A1;
                this.moveCastle(fromSquare, toSquare);
                break;

            case BCASTLEK: //if black caslted kingside
                fromSquare = this.getPiece(Square.F8);
                toSquare = Square.H8;
                this.moveCastle(fromSquare, toSquare);
                break;

            case BCASTLEQ: //if black caslted queenside
                fromSquare = this.getPiece(Square.D8);
                toSquare = Square.A8;
                this.moveCastle(fromSquare, toSquare);
                break;
        }    
    }
    
    private void unMakePromo(Move move) //undo changes made when promoting
    {
        removePieceFromBoard(move.getPieceToMove());  //remove the promoted piece from the board
        addPromotedPawn(); //add back the promoted pawn
    }

    private void moveCastle(Piece fromSquare, Square toSquare)  //move castle
    {
        this.movePiece(new Move(fromSquare,toSquare)); //move the castle
    }

    private void refreshMoveList() //update the gui move list
    {
        if (getMoveList().isEmpty()) return;
        guiMoveList.add(moveList.peek().toString());
    }

    public void outOfTime(boolean whitesTurn) //used by the clock, the clock tell the chess game when the clock is out of time
    {
        outOfTime = true;
    }

    public Human[] getHumans() //return an array of human objects if they exist
    {
        boolean whiteHuman = false;
        boolean blackHuman = false;
        if (white instanceof Human)
        {
            whiteHuman = true;
        } 
        if (black instanceof Human)
        {
            blackHuman = true;
        } 
        return new Human[]{(whiteHuman ? (Human)white : null), (blackHuman ? (Human)black : null)};
    }

    private boolean checkCastlingRights(boolean isWhite, boolean isKingside)
    {
        if (!boardState.getCastlingRights(isWhite, isKingside)) //return early if castling rights have already been provoked
        {
            return false;
        }
        
        if (isWhite)
        {
            if (!(this.getPiece(Square.E1) instanceof King)) //if there isnt a king on E1
            {
                return false;
            } 
        } else {
            if (!(this.getPiece(Square.E8) instanceof King)) //if there isnt a king on E8
            {
                return false;
            } 
        }
        Piece potentialRook;
        
        if (isKingside)
        {
            potentialRook = (isWhite ? this.getPiece(Square.H1) : this.getPiece(Square.H8));
            if (!(potentialRook instanceof Rook) || potentialRook.isWhite() != isWhite) //if there isnt a rook on the kingside
            {
                return false;
            }
        } else {
            potentialRook =isWhite ? this.getPiece(Square.A1) : this.getPiece(Square.A8);
            if (!(potentialRook instanceof Rook) || potentialRook.isWhite() != isWhite) //if there isnt a rook on the queenside
            {
                return false;
            }
        }
        
        //board passes all checks
        return true;
    }

    private void postMoveBoardAssessment() 
    {
        
        //castling rights check
        for (Boolean isWhite = true; isWhite != null;) //for both sides
        {
            for (Boolean isKingside = true; isKingside != null;) //for both king and queen side castling
            {
                if(!checkCastlingRights(isWhite, isKingside)) //if the board fails this assessment to maintain castling rights
                {
                    boardState.removeCastlingRights(isWhite, isKingside); //remove the rights
                }
                if (isKingside) 
                {
                    isKingside = false;
                } else {
                    isKingside = null;
                }
            }
            if (isWhite)
            {
                isWhite = false;
            } else {
                isWhite = null;
            }
        }
        Move lastMove = getMoveList().peek(); //get the last move
        
        if (lastMove.isDoublePawnPush() || lastMove.isCapture()) //if we should reset the half move counter
        {
            boardState.resetHalfmove(); //reset the half move counter
        } else {
            boardState.incrementHalfmove(); //increment the half move counter
        }
        
        if (!lastMove.getPieceToMove().isWhite()) //if the last player to make a move was black
        {
            boardState.incrementFullMove(); //increment the full move counter
        }
        boardState.flipTurn(); //flip the chessboards turn
    }

    private void addPromotedPawn() //restore the promoted pawn to the board
    {
        addPiece(promotedPawns.pop());
    }
    public boolean hasPlayers() //if the board has players
    {
        return hasPlayers;
    }
    public boolean isGameInProgess() //if this chess object has a game in progress
    {
        return gameInProgess;
    }
    
    private static synchronized void addFEN(String FEN, String moveToPlay) //add fen and best move to opening book table
    {
        String query = "INSERT INTO OpeningBook (FEN, nextMove) VALUES ('"+FEN+"', '"+moveToPlay+"')";
        db.updateDB(query);
        
    }
    
    private static void createOpeningBook() //create a table for an opening book
    {
        db.updateDB("CREATE TABLE OpeningBook (" +
                "    FEN VARCHAR(100) NOT NULL PRIMARY KEY," +
                "    nextMove VARCHAR(5)" +
                ")");
    }
    
    public static void collectOpeningBook() 
    {
        ArrayList<String> FENS = loadFromFile("/assets/openingBook.txt");
        int num = 0;
        for (String fen : FENS)
        {
            Object[] result = stockfish.getEval(fen, 12);
            
            String bestMove = (String) result[0];
            int eval = (int) result[1];
            
            addFEN(fen, bestMove);
            System.out.println(++num + "FEN: " + fen + ", bestMove: " + bestMove);
        }
    }

    
    public static void main(String[] args) 
    {
        createOpeningBook();
        collectOpeningBook();
    }
    
    
    /**
     * @return the endOfGame
     */
    public boolean isEndOfGame() 
    {
        return endOfGame;
    }

    /**
     * @return the winnerOfGameIsWhite
     */
    public Boolean getWinnerOfGameIsWhite() 
    {
        return winnerOfGameIsWhite;
    }

    /**
     * @return the moveList
     */
    public Stack<Move> getMoveList() {
        return moveList;
    }
    
    
}
