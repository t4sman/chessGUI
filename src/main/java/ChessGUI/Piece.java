/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

/**
 *
 *
 *
 * @author Tasman Keenan 
 * StudentID 21147547
 *
 */

import java.util.ArrayList;

public abstract class Piece extends Square{
    final int ID;
    static int numOfPieces = 0;
    private final int pieceID;
    
    private final boolean isWhite;
    private final double value;
    
    protected Chess chess;
    
    
    Piece(int x, int y, boolean isWhite, double value, int id, Chess chess)
    {
        super(x,y);
        this.isWhite = isWhite;
        this.value = value * 100;
        this.pieceID = numOfPieces++;
        this.chess = chess;
        this.ID = id;
    }
    
    public int getPieceID()
    {
        return this.pieceID;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public double getValue() {
        return value;
    }
    
    public Chess getChess()
    {
        return chess;
    }
    
    @Override
    public boolean equals(Object piece){
        
        if (piece instanceof Piece)
        {
            return this.hashCode() == piece.hashCode();
        } else if (piece instanceof Square)
        {
            return super.equals(piece);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((ID*64) + super.hashCode());
    }
    
    //Piece moves
    
    protected ArrayList<Move> getPromotionMoves(ArrayList<Move> identifiedMoves) //for every pawn move to promote, add all 4 promotion moves
    {
        ArrayList<Move> moves = new ArrayList();
        for (Move move : identifiedMoves)
        {
            Move promoteToQueen = new Move(move.getPieceToMove(),move.getToSquare(), SpecialMoves.PROMOTOQUEEN);
            moves.add(promoteToQueen);
            
            Move promoteToRook = new Move(move.getPieceToMove(),move.getToSquare(), SpecialMoves.PROMOTOROOK);
            moves.add(promoteToRook);
            
            Move promoteToBishop = new Move(move.getPieceToMove(),move.getToSquare(), SpecialMoves.PROMOTOBISHOP);
            moves.add(promoteToBishop);
            
            Move promoteToKnight = new Move(move.getPieceToMove(),move.getToSquare(), SpecialMoves.PROMOTOKNIGHT);
            moves.add(promoteToKnight);
        }
        return moves;
    }
    
    private ArrayList<Move> getPrimitiveKingMoves(Square square)
    {
        ArrayList<Move> moves = new ArrayList<>();
        
        int x = square.getX();
        int y = square.getY();
        
        Square newSquare;
        Piece pieceToCapture;
        
        for (int xChange = -1; xChange <= 1; xChange++) //king can move in all 8 directions
        {
            for (int yChange = -1; yChange <= 1; yChange++)
            {
                if (xChange == 0 && yChange == 0)
                {
                    continue;
                }
                
                int newX = x + xChange;
                int newY = y + yChange;
                
                
                if (newX >= 0 && newX <= 7 && newY >= 0 && newY <= 7)
                {
                    newSquare = Square.at(newX, newY);
                    pieceToCapture = chess.getPiece(newSquare);
                    if (pieceToCapture == null || pieceToCapture.isWhite() != this.isWhite)
                    {
                        Move move = new Move(this, newSquare);
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }
    
    protected ArrayList<Move> getKingMoves(Square square)
    {
        
        ArrayList<Move> moves = new ArrayList<>();
        
        moves.addAll(getPrimitiveKingMoves(square));
        
        
        if (this.isWhite()) // checking castling rights.
        {
            if (chess.canCastle(this.isWhite(), true))
            {
                Move move = new Move(this, Square.G1, SpecialMoves.WCASTLEK);
                moves.add(move); 
            }
            
            if (chess.canCastle(this.isWhite(), false))
            {
                Move move = new Move(this, Square.C1, SpecialMoves.WCASTLEQ);
                moves.add(move); 
            }
            
        } else {
            if (chess.canCastle(this.isWhite(), true))
            {
                Move move = new Move(this, Square.G8, SpecialMoves.BCASTLEK);
                moves.add(move); 
            }
            
            if (chess.canCastle(this.isWhite(), false))
            {
                Move move = new Move(this, Square.C8, SpecialMoves.BCASTLEQ);
                moves.add(move); 
            }
        }
        
        return moves;
    }
    
    protected ArrayList<Move> getPawnMoves(Square square)
    {
        
        ArrayList<Move> moves = new ArrayList<>();
        
        int x = square.getX();
        int y = square.getY();
        
        int side = (this.isWhite() ? 1 : -1);               
        
        if (y+side < 0 || y+side > 7)
        {
            return moves;
        }
            
        if (chess.getPiece(x, y + side) == null){ //if this pawn can push one space
            if(((side + 7) % 7 == y) && chess.getPiece(x, y + (2 * side)) == null ){//check if this pawn is on its starting square and can move 2 squares
                moves.add(new Move(this, Square.at(x, y + (2 * side)), true)); //pawn can move 2 squares
            }
            moves.add(new Move(this, Square.at(x, y + (1*side)))); //pawn moves one square
        }

        Piece pieceToCapture;
        Square toSquare;
        if(x - 1 >= 0){ //if we aren't out of bounds
            
            toSquare = Square.at(x-1, y+side);
            pieceToCapture = chess.getPiece(toSquare);
            if (pieceToCapture != null) //if there is a piece in the possible capture square to the left
            {
                if (this.isWhite() != pieceToCapture.isWhite()){ //if this piece is an enemy piece
                    moves.add(new Move(this, toSquare)); //add it to possible moves
                }
            }
            if (chess.getEnPassant() != null && chess.getEnPassant().equals(toSquare)) //if en passant is possible
            {
                moves.add(new Move(this, toSquare, Square.at(x-1, y)));
            }
        }
        if(x + 1 <= 7){ //if we aren't out of bounds
            toSquare = Square.at(x+1, y+side);
            pieceToCapture = chess.getPiece(toSquare);
            if (pieceToCapture != null) //if there is a piece in the possible capture square to the right
            {
                if (this.isWhite() != pieceToCapture.isWhite()){ //if this piece is an enemy piece
                    moves.add(new Move(this, toSquare)); //add it to possible moves
                }
            }
            if (chess.getEnPassant() != null && chess.getEnPassant().equals(toSquare)) //if en passant is possible
            {
                moves.add(new Move(this, toSquare, Square.at(x+1, y))); //add en passant
            }
        }

        if (((side * -1) + 7) % 7 == y) // if we are about to promote
        {
            moves = this.getPromotionMoves(moves);
        }
        
        return moves;
    }
    
    protected ArrayList<Move> getRookMoves(Square square)
    {
        
        ArrayList<Move> moves = new ArrayList<>();
        
        int originalX = square.getX();
        int originalY = square.getY();

        int x;
        int y;
        
        boolean anotherPieceEncountered; // to stop searching once a piece is found
        
        
        anotherPieceEncountered = false;
        x = originalX;
        y = originalY;
        x++;
        while(x <= 7 && !anotherPieceEncountered) //while we are not out of bounds and haven't hit another piece
        {
            Square toMoveTo = Square.at(x,y);
            Piece piece = (chess.getPiece(toMoveTo) == null ? null : chess.getPiece(toMoveTo));
            if (piece == null) // if there is nothing on this square
            {
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
            } else if (piece.isWhite() != this.isWhite()) { // if there is something on the square and the piece is an opposing piece
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            } else { // if this square has a friendly piece
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            }

            x++; //increment x
        }
        anotherPieceEncountered = false;
        x = originalX;
        y = originalY;
        x--;
        
        while(x >= 0 && !anotherPieceEncountered) //while we are not out of bounds and haven't hit another piece
        {
            Square toMoveTo = Square.at(x,y);
            Piece piece = (chess.getPiece(toMoveTo) == null ? null : chess.getPiece(toMoveTo));
            if (piece == null) // if there is nothing on this square
            {
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
            } else if (piece.isWhite() != this.isWhite()) { // if there is something on the square and the piece is an opposing piece
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            } else { // if this square has a friendly piece
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            }

            x--;
        }
        anotherPieceEncountered = false;
        x = originalX;
        y = originalY;
        y++;
        while(y <= 7 && !anotherPieceEncountered) //while we are not out of bounds and haven't hit another piece
        {
            Square toMoveTo = Square.at(x,y);
            Piece piece = (chess.getPiece(toMoveTo) == null ? null : chess.getPiece(toMoveTo));
            if (piece == null) // if there is nothing on this square
            {
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
            } else if (piece.isWhite() != this.isWhite()) { // if there is something on the square and the piece is an opposing piece
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            } else { // if this square has a friendly piece
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            }

            y++;
        }
        anotherPieceEncountered = false;
        x = originalX;
        y = originalY;
        y--;
        
        while(y >= 0 && !anotherPieceEncountered) //while we are not out of bounds and haven't hit another piece
        {
            Square toMoveTo = Square.at(x,y);
            Piece piece = (chess.getPiece(toMoveTo) == null ? null : chess.getPiece(toMoveTo));
            if (piece == null) // if there is nothing on this square
            {
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
            } else if (piece.isWhite() != this.isWhite()) { // if there is something on the square and the piece is an opposing piece
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            } else { // if this square has a friendly piece
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            }

            y--;
        }        
        return moves;
    }
    
    protected ArrayList<Move> getBishopMoves(Square square)
    {
        
        ArrayList<Move> moves = new ArrayList<>();
        
        int originalX = square.getX();
        int originalY = square.getY();
        
        boolean anotherPieceEncountered; // to stop searching once a piece is found
        
        int x;
        int y; 
        
        anotherPieceEncountered = false;
        x = originalX;
        y = originalY;
        x++;
        y++;

        while(x <= 7&&y <= 7 && !anotherPieceEncountered) //for all diagonal moves in the positive x and positive y directions
        {
            Square toMoveTo = Square.at(x,y);
            Piece piece = (chess.getPiece(toMoveTo) == null ? null : chess.getPiece(toMoveTo));
            if (piece == null) // if there is nothing on this square
            {
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
            } else if (piece.isWhite() != this.isWhite()) { // if there is something on the square and the piece is an opposing piece
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            } else { // if this square has a friendly piece
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            }

            x++; 
            y++;
        }

        anotherPieceEncountered = false;
        x = originalX;
        y = originalY;
        x++;
        y--;

        while(x <= 7&&y >= 0 && !anotherPieceEncountered) // for all diagonal moves in the positive x and negative y directions
        {
            Square toMoveTo = Square.at(x,y);
            Piece piece = (chess.getPiece(toMoveTo) == null ? null : chess.getPiece(toMoveTo));
            if (piece == null) // if there is nothing on this square
            {
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
            } else if (piece.isWhite() != this.isWhite()) { // if there is something on the square and the piece is an opposing piece
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            } else { // if this square has a friendly piece
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            }

            x++;
            y--;
        }

        anotherPieceEncountered = false;
        x = originalX;
        y = originalY;
        x--;
        y++;

        while(x >= 0 &&y <= 7 && !anotherPieceEncountered) // for all diagonal moves in the negative x and positive y directions
        {
            Square toMoveTo = Square.at(x,y);
            Piece piece = (chess.getPiece(toMoveTo) == null ? null : chess.getPiece(toMoveTo));
            if (piece == null) // if there is nothing on this square
            {
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
            } else if (piece.isWhite() != this.isWhite()) { // if there is something on the square and the piece is an opposing piece
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            } else { // if this square has a friendly piece
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            }

            x--;
            y++;
        }

        anotherPieceEncountered = false;
        x = originalX;
        y = originalY;
        x--;
        y--;

        while(x >= 0 && y >= 0 && !anotherPieceEncountered) // for all diagonal moves in the negative x and negative y directions
        {
            Square toMoveTo = Square.at(x,y);
            Piece piece = (chess.getPiece(toMoveTo) == null ? null : chess.getPiece(toMoveTo));
            if (piece == null) // if there is nothing on this square
            {
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
            } else if (piece.isWhite() != this.isWhite()) { // if there is something on the square and the piece is an opposing piece
                moves.add(new Move(this, Square.at(x,y))); // this is a possible move, add it to the list
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            } else { // if this square has a friendly piece
                anotherPieceEncountered = true; // we are at another piece, stop the loop
            }

            x--;
            y--;
        }
        return moves;
    }
    
    protected ArrayList<Move> getKnightMoves(Square square)
    {
        
        ArrayList<Move> moves = new ArrayList<>();
        
        int originalX = square.getX();
        int originalY = square.getY();
        
        
        int[] xMoves = {-2,-1,1,2}; //matching indexes for x and y (or negative Y) to make knight moves
        int[] yMoves = {1,2,2,1};
        int[] negativeYMoves = {-1,-2,-2,-1};
        
        Square toMoveTo;
        Piece toCapture;
        int newX;
        int newY;
        
        for (int i = 0; i < 4; i++){
            if (!(((newX = xMoves[i] + originalX) >= 0)&&(newX <= 7)))
            {
                continue;
            }
            
            if (((newY = yMoves[i] + originalY) >= 0)&&(newY <= 7))
            {
                toMoveTo = Square.at(newX, newY);
                toCapture = chess.getPiece(toMoveTo);
                if (toCapture == null)
                {
                    moves.add(new Move(this, toMoveTo));
                } else {

                    if (toCapture.isWhite() != this.isWhite())
                    {
                        moves.add(new Move(this, toMoveTo));
                    }
                }
            }
            
            if (((newY = negativeYMoves[i] + originalY) >= 0)&&(newY <= 7))
            {
                toMoveTo = Square.at(newX, newY);
                toCapture = chess.getPiece(toMoveTo);
                if (toCapture == null)
                {
                    moves.add(new Move(this, toMoveTo));
                } else {
                    if (toCapture.isWhite() != this.isWhite())
                    {
                        moves.add(new Move(this, toMoveTo));
                    }
                }
            }
        }
        return moves;
    }
    
    protected ArrayList<Move> getQueenMoves(Square square)
    {
        ArrayList<Move> moves = new ArrayList();
        moves.addAll(this.getBishopMoves(square));
        moves.addAll(this.getRookMoves(square));
        return moves;
    }
    
    public Square getSquare()
    {
        return Square.at(this.getX(),this.getY());
    }

    boolean isTargetted()
    {
        return this.isTargetted(this);
    }
    
    
    boolean isTargetted(Square checkSquare) {
        Piece tempPiece;
        for (Move move : getBishopMoves(checkSquare)) //Bishop and Queen Detection
        {
            if ((tempPiece = move.getPieceToCapture()) != null && (tempPiece instanceof Bishop || tempPiece instanceof Queen) && tempPiece.isWhite() != this.isWhite())
            {
                return true;
            }
        }
        
        for (Move move : getRookMoves(checkSquare)) //Rook and Queen Detection
        {
            if ((tempPiece = move.getPieceToCapture()) != null && (tempPiece instanceof Rook || tempPiece instanceof Queen) && tempPiece.isWhite() != this.isWhite())
            {
                return true;
            }
        }
        
        for (Move move : getKnightMoves(checkSquare)) //Knight Detection
        {
            if ((tempPiece = move.getPieceToCapture()) != null && tempPiece instanceof Knight && tempPiece.isWhite() != this.isWhite())
            {
                return true;
            }
        }
        
        for (Move move : getPawnMoves(checkSquare)) //Pawn Detection
        {
            if ((tempPiece = move.getPieceToCapture()) != null && tempPiece instanceof Pawn && tempPiece.isWhite() != this.isWhite())
            {
                return true;
            }
        }
        
        for (Move move : getPrimitiveKingMoves(checkSquare)) //King Detection
        {
            if ((tempPiece = move.getPieceToCapture()) != null && tempPiece instanceof King && tempPiece.isWhite() != this.isWhite())
            {
                return true;
            }
        }
        return false;
        
    }
    
    
    public abstract ArrayList<Move> getMoves(); // returns all pseudo legal moves, some will be invalid and put the king in check
}
