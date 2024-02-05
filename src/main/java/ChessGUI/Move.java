/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

/**
 *
 * @author tasma
 */
public class Move implements Comparable<Move>{
    
    public static Move getMove(String moveAlgebraic, Chess chess)
    {
        for (Move move : chess.getAllLegalMoves())
        {
            if (move.toString().equals(moveAlgebraic))
            {
                return move;
            }
        }
        return null;
    }
    
    private final Chess chess;
    
    private Piece pieceToMove;
    private Square fromSquare; //for reversing a move
    
    private Piece pieceToCapture;
    private Square toSquare;
    
    private Pawn enPassantTargetPawn = null;
    
    private boolean isDoublePawnPush = false;
    
    private boolean isCapture = false;
    
    private BoardState boardState;
    
    private SpecialMoves specialMove = null;
    
    private String preBoardFEN;
    private String postBoardFEN; //is added after checking if the move is legal
    
    
    
    public SpecialMoves getSpecialMove()
    {
        return specialMove;
    }
    
    Move(Piece pieceToMove, Square toMoveTo)
    {
        this.pieceToMove = pieceToMove;
        this.toSquare = toMoveTo;
        this.chess = pieceToMove.getChess();
        this.preBoardFEN = chess.getFENString(false);
        this.fromSquare = Square.at(pieceToMove.getX(), pieceToMove.getY());
        if ((this.pieceToCapture =  chess.getPiece(this.toSquare)) != null)
        {
            this.isCapture = true;
        }
    }
    Move(Piece pieceToMove, Square toMoveTo, boolean isDoublePawnPush)
    {
        this(pieceToMove, toMoveTo);
        this.isDoublePawnPush = isDoublePawnPush;
    }
    
    Move(Piece pieceToMove, Square toMoveTo, SpecialMoves specialMove)
    {
        this(pieceToMove,toMoveTo);
        this.specialMove = specialMove;
    }
    
    Move(Piece pieceToMove, Square toMoveTo, Square enPassantTargetPawn)
    {
        this(pieceToMove, toMoveTo);
        this.pieceToCapture =  chess.getPiece(enPassantTargetPawn);
        this.isCapture = true;
        this.specialMove = SpecialMoves.ENPASSANT;
    }

    /**
     * @return the pieceToMove
     */
    public Piece getPieceToMove() {
        return pieceToMove;
    }

    /**
     * @return the toMoveTo
     */
    public Square getToSquare() {
        return toSquare;
    }

    /**
     * @return the enPassantTargetPawn
     */
    public Pawn getEnPassantTargetPawn() {
        return enPassantTargetPawn;
    }

    /**
     * @return the isDoublePawnPush
     */
    public boolean isDoublePawnPush() {
        return isDoublePawnPush;
    }

    /**
     * @return the pieceSquare
     */
    public Square getFromSquare() {
        return fromSquare;
    }

    /**
     * @return the pieceToCapture
     */
    public Piece getPieceToCapture() {
        return pieceToCapture;
    }

    /**
     * @return the isCapture
     */
    public boolean isCapture() {
        return isCapture;
    }
    
    public void addBoardState(BoardState boardState)
    {
        this.boardState = new BoardState(boardState);
    }
    
    public BoardState getBoardState()
    {
        return boardState;
    }
    
    
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Move)
        {
            return this.getFromSquare().equals(((Move) o).getFromSquare()) && this.getToSquare().equals(((Move) o).getToSquare());
        } else return false;
    }

    @Override
    public int hashCode() {
        int pieceToMoveHash = this.getPieceToMove().hashCode();
        int toSquareHash = this.getToSquare().hashCode();
        int thisHash = toSquareHash + (pieceToMoveHash * 100); 
        return thisHash;
    }
    
    @Override
    public String toString()
    {
        String out = this.getFromSquare().toAlgebraic() + this.getToSquare().toAlgebraic();
        if (specialMove != null)
        {
            switch(specialMove)
            {
                case PROMOTOQUEEN:
                    out += "q";
                    break;
                case PROMOTOROOK:
                    out += "r";
                    break;
                case PROMOTOBISHOP:
                    out += "b";
                    break;
                case PROMOTOKNIGHT:
                    out += "n";
                    break;
            }
        }
        return out;
    }
    
    private Integer rankMove()
    {
        int rank = 0;
        
        if (specialMove != null)
        {
            rank++;
            
            switch(specialMove)
            {
                case PROMOTOQUEEN:
                case PROMOTOROOK:
                case PROMOTOBISHOP:
                case PROMOTOKNIGHT:
                    rank += 3;
                    break;
                case ENPASSANT:
                    rank++;
                    break;
                case WCASTLEK:
                case WCASTLEQ:
                case BCASTLEK:
                case BCASTLEQ:
                    rank += 2;
            }
        }
        if (pieceToCapture != null)
        {
            rank += 2;
            if (pieceToMove.getPieceID() + pieceToCapture.getPieceID() <= 0) {
                rank += 3;
            }
        }
        
        return rank;
        
    }

    @Override
    public int compareTo(Move anotherMove) {
        return this.rankMove().compareTo(anotherMove.rankMove()) * (this.pieceToMove.isWhite() ? 1 : -1);
    }

    /**
     * @return the preBoardFEN
     */
    public String getBoardFEN() {
        return preBoardFEN;
    }

    /**
     * @return the postBoardFEN
     */
    public String getPostBoardFEN() {
        return postBoardFEN;
    }

    /**
     * @param postBoardFEN the postBoardFEN to set
     */
    public void setPostBoardFEN(String postBoardFEN) {
        this.postBoardFEN = postBoardFEN;
    }
}
