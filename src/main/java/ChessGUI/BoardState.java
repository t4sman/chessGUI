/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

/**
 *
 * @author tasma
 */
public class BoardState //the state of the chessboard when a player is making a move.
{
    private boolean whiteCanCastleKingside; //whites kingside castling rights
    private boolean whiteCanCastleQueenside; //whites queenside castling rights
    private boolean blackCanCastleKingside; //blacks kingside castling rights
    private boolean blackCanCastleQueenside; //blacks queenside castling rights

    private int halfMove = 0; // halfmoves, increments after every turn and reset to 0 when a pawn is pushed or a piece is captured, game ends in stalemate if this value equals 100
    private int fullMove = 0; // the amount of times black has played.

    private Square enPassant; //the enPassant square, null if there isnt one

    private boolean whiteToMove; //if it is white to move

    BoardState(BoardState boardState)
    {
        this.whiteCanCastleKingside = boardState.isWhiteCanCastleKingside();
        this.whiteCanCastleQueenside = boardState.isWhiteCanCastleQueenside();
        this.blackCanCastleKingside = boardState.isBlackCanCastleKingside();
        this.blackCanCastleQueenside = boardState.isBlackCanCastleQueenside();

        this.halfMove = boardState.getHalfMove();
        this.fullMove = boardState.getFullMove();

        this.enPassant = boardState.getEnPassant();
        this.whiteToMove = boardState.isWhiteToMove();
    }

    BoardState(boolean whiteCanCastleKingside, boolean whiteCanCastleQueenside, boolean blackCanCastleKingside, boolean blackCanCastleQueenside, int halfMove, int fullMove, Square enPassant, boolean whiteToMove)
    {
        this.whiteCanCastleKingside = whiteCanCastleKingside;
        this.whiteCanCastleQueenside = whiteCanCastleQueenside;
        this.blackCanCastleKingside = blackCanCastleKingside;
        this.blackCanCastleQueenside = blackCanCastleQueenside;

        this.halfMove = halfMove;
        this.fullMove = fullMove;

        this.enPassant = enPassant;
        this.whiteToMove = whiteToMove;
    }

    public void update(BoardState boardState)
    {
        this.whiteCanCastleKingside = boardState.isWhiteCanCastleKingside();
        this.whiteCanCastleQueenside = boardState.isWhiteCanCastleQueenside();
        this.blackCanCastleKingside = boardState.isBlackCanCastleKingside();
        this.blackCanCastleQueenside = boardState.isBlackCanCastleQueenside();

        this.halfMove = boardState.getHalfMove();
        this.fullMove = boardState.getFullMove();

        this.enPassant = boardState.getEnPassant();
        this.whiteToMove = boardState.isWhiteToMove();
    }
    
    public void flipTurn() //change turns
    {
        whiteToMove = !whiteToMove;
    }
    
    public boolean getCastlingRights(boolean isWhite, boolean isKingside) //if a side has castling rights
    {
        if (isWhite)
        {
            if (isKingside)
            {
                return this.whiteCanCastleKingside;
            } else {
                return this.whiteCanCastleQueenside;
            }
        } else {
            if (isKingside)
            {
                return this.blackCanCastleKingside;
            } else {
                return this.blackCanCastleQueenside;
            }
        }
    }


    /**
     * @return the whiteCanCastleKingside
     */
    public boolean isWhiteCanCastleKingside() {
        return whiteCanCastleKingside;
    }

    /**
     * @return the whiteCanCastleQueenside
     */
    public boolean isWhiteCanCastleQueenside() {
        return whiteCanCastleQueenside;
    }

    /**
     * @return the blackCanCastleKingside
     */
    public boolean isBlackCanCastleKingside() {
        return blackCanCastleKingside;
    }

    /**
     * @return the blackCanCastleQueenside
     */
    public boolean isBlackCanCastleQueenside() {
        return blackCanCastleQueenside;
    }

    public void removeCastlingRights(boolean isWhite, Boolean isKingside)
    {
        if (isKingside == null)
        {
            if (isWhite)
            {
                this.whiteCanCastleKingside = false;
                this.whiteCanCastleQueenside = false;
            } else {
                this.blackCanCastleKingside = false;
                this.blackCanCastleQueenside = false;
            }
        } else if (isWhite)
        {
            if (isKingside)
            {
                this.whiteCanCastleKingside = false;
            } else {
                this.whiteCanCastleQueenside = false;
            }
        } else {
            if (isKingside)
            {
                this.blackCanCastleKingside = false;
            } else {
                this.blackCanCastleQueenside = false;
            }
        }
    }

    /**
     * @return the enPassant
     */
    public Square getEnPassant() {
        return enPassant;
    }

    public void setEnPassant(Square enPassant)
    {
        this.enPassant = enPassant;
    }

/**
 * @return the halfMove
 */
    public int getHalfMove() {
        return halfMove;
    }

    /**
     * @param halfMove the halfMove to set
     */
    public void setHalfMove(int halfMove) {
        this.halfMove = halfMove;
    }
    
    public void resetHalfmove()
    {
        this.halfMove = 0;
    }
    
    public void incrementHalfmove()
    {
        this.halfMove++;
    }
    
    public void incrementFullMove()
    {
        this.fullMove++;
    }

    /**
     * @return the fullMove
     */
    public int getFullMove() {
        return fullMove;
    }

    /**
     * @param fullMove the fullMove to set
     */
    public void setFullMove(int fullMove) {
        this.fullMove = fullMove;
    }

    /**
     * @return the whiteToMove
     */
    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    /**
     * @param whiteToMove the whiteToMove to set
     */
    public void setWhiteToMove(boolean whiteToMove) {
    this.whiteToMove = whiteToMove;
}
}
