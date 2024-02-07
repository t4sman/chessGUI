/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author tasma
 */
public class ChessboardPanel extends JPanel {

    private final boolean hasPlayers;
    
    
    private enum Pieces { //pieces in enums
        WKING(0), //white king
        WQUEEN(1), //white queen
        WBISHOP(2), //white bishop
        WKNIGHT(3), //white knight
        WROOK(4), //white rook
        WPAWN(5), //white pawn
        BKING(6), //black king
        BQUEEN(7), //black queen
        BBISHOP(8), //black bishop
        BKNIGHT(9), //black knight
        BROOK(10), //black rook
        BPAWN(11); //black pawn
        
        
        final int index;
        Pieces(int index)
        {
            this.index = index;
        }

        /**
         * @return the index
         */
        
    }
    
    public int BOARDLENGTH; //the length of the board in pixels
    public final int SQUARESACROSS = 8; //how many squares across is the board
    
    
    public int getSquareSize() //defines the size of a square
    {
        return BOARDLENGTH / SQUARESACROSS; 
    }
    
    private final Color darkSquares = new Color(106,155,65); //the color of the dark squares
    private final Color lightSquares = new Color(243,243,244); //the color of the light squares
    
    private Chess chess; //link to the chess instance
    
    private Piece selectedPiece; //the piece that is selected by the mouse
    
    private int screenPieceX; 
    private int screenPieceY;
    
    private ArrayList<Square> selectedSquares = new ArrayList(); //a list of squares selected by the user
    
    private Image pieces[]; //an array of images of the chess pieces
    
    private boolean humanIsWhite; //if a human is playing as white
    private boolean humanIsBlack; //if a humna is playing as black
    
    private Human white; //quick link to a possible white human player. if null, it is ignored
    private Human black; //quick link to a possible black human player. if null, it is ignored
    
    private final Object repaintLock = new Object(); // Object for synchronization
    
    public void safeRepaint() { //thread safe repaint
        SwingUtilities.invokeLater(() -> {
            synchronized (repaintLock) {
                repaint();
            }
        });
    }
    
    @Override
    public void paint(Graphics g) { //paint the chessboard
        try {
            synchronized (repaintLock) { 
                super.paint(g);
            
                //painting objects in the order they appear
                paintBoard(g); //paint the board
                paintLastMove(g); //paint last move
                paintLegalMoves(g); //paint the legal moves
                paintSelectedSquares(g); //paint the selected squares
                paintPieces(g); //paint the pieces
                paintSelectedPiece(g); //paint the selected piece
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void paintBoard(Graphics g) //paints the board
    {
        for (int x = 0; x < 8; x++) //for across the board
        {
            for (int y = 0; y < 8; y++) //for up the board
            {
                paintSquare(g,Square.at(x, y),(x+y)%2 == 0 ? lightSquares : darkSquares); //paint the squares
            }
        }
    }
    
    private void paintLastMove(Graphics g) //paints the last move to the board
    {
        if (chess.getMoveList().isEmpty()) //if there isnt a last move
        {
            return; //return early
        }
        Move move = chess.getMoveList().peek(); //get the last move
        
        Color squareColor = new Color(51, 212, 139);
        
        
        paintSquare(g, move.getFromSquare(), squareColor);
        paintSquare(g, move.getToSquare(), squareColor);
        
    }
    
    private void paintSelectedSquares(Graphics g)
    {
        if (selectedSquares.isEmpty())
        {
            return;
        }
        
        for (Square square : selectedSquares)
        {
            paintSquare(g,square,Color.YELLOW);
        }
        
        
    }
    
    private void paintSquare(Graphics g, Square square, Color color)
    {
        
        g.setColor(color);
        if (this.blackOrientation())
        {
            square = Square.inverse(square);
        } 
        g.fillRect(square.getX()*getSquareSize(), (7-square.getY())*getSquareSize(), getSquareSize(), getSquareSize());
        
    }
    
    private boolean blackOrientation() {
        return humanIsBlack && !humanIsWhite;
    }
    
    private void paintSelectedPiece(Graphics g)
    {
        if (selectedPiece == null)
        {
            return;
        }
        Pieces imgPiece = null;
        switch(selectedPiece.toString())
        {
            case "k":
                imgPiece = Pieces.BKING;
                break;
            case "q":
                imgPiece = Pieces.BQUEEN;
                break;
            case "b":
                imgPiece = Pieces.BBISHOP;
                break;
            case "n":
                imgPiece = Pieces.BKNIGHT;
                break;
            case "r":
                imgPiece = Pieces.BROOK;
                break;
            case "p":
                imgPiece = Pieces.BPAWN;
                break;
            case "K":
                imgPiece = Pieces.WKING;
                break;
            case "Q":
                imgPiece = Pieces.WQUEEN;
                break;
            case "B":
                imgPiece = Pieces.WBISHOP;
                break;
            case "N":
                imgPiece = Pieces.WKNIGHT;
                break;
            case "R":
                imgPiece = Pieces.WROOK;
                break;
            case "P":
                imgPiece = Pieces.WPAWN;
                break;
        }
        
        g.drawImage(pieces[imgPiece.index], screenPieceX, screenPieceY, this);
        
    }
    
    private void paintLegalMoves(Graphics g)
    {
        try {
            synchronized (repaintLock) {
                if (selectedPiece == null)
                {
                    return;
                }

                ArrayList<Move> moves = chess.getLegalPieceMoves(selectedPiece);
                
                for (Move move : moves)
                {
                    if (move.getSpecialMove() == SpecialMoves.ENPASSANT)
                    {
                        paintSquare(g, move.getToSquare(), Color.BLUE);
                    } else if(move.isCapture())
                    {
                        paintSquare(g, move.getToSquare(), Color.RED);
                    } else {
                        paintSquare(g,move.getToSquare(), Color.green);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void paintPieces(Graphics g)
    {
        for(Piece piece : chess.getPlayingPieces())
        {
            if (piece == selectedPiece)
            {
                continue;
            }
            Pieces imgPiece = null;
            switch(piece.toString())
            {
                case "k":
                    imgPiece = Pieces.BKING;
                    break;
                case "q":
                    imgPiece = Pieces.BQUEEN;
                    break;
                case "b":
                    imgPiece = Pieces.BBISHOP;
                    break;
                case "n":
                    imgPiece = Pieces.BKNIGHT;
                    break;
                case "r":
                    imgPiece = Pieces.BROOK;
                    break;
                case "p":
                    imgPiece = Pieces.BPAWN;
                    break;
                case "K":
                    imgPiece = Pieces.WKING;
                    break;
                case "Q":
                    imgPiece = Pieces.WQUEEN;
                    break;
                case "B":
                    imgPiece = Pieces.WBISHOP;
                    break;
                case "N":
                    imgPiece = Pieces.WKNIGHT;
                    break;
                case "R":
                    imgPiece = Pieces.WROOK;
                    break;
                case "P":
                    imgPiece = Pieces.WPAWN;
                    break;
                default:
                    System.exit(0);
            }
            if (this.blackOrientation())
            {
                g.drawImage(pieces[imgPiece.index], (7-piece.getX()) * getSquareSize(), piece.getY() * getSquareSize(), this);
            } else {
                g.drawImage(pieces[imgPiece.index], piece.getX() * getSquareSize(), (7 - piece.getY()) * getSquareSize(), this);
            }
            
        }
    }
    
    private Piece getPieceByPixel(MouseEvent e)
    {
        return chess.getPiece(getSquareByPixel(e));
    }
    
    
    private Square getSquareByPixel(MouseEvent e)
    {
        Square square = Square.at(e.getX()/getSquareSize(), 7 - (e.getY()/getSquareSize()));
        if (this.blackOrientation())
        {
            return Square.inverse(square);
        } else {
            return square;
        }
        
    }
    
    
    
    public ChessboardPanel(Chess chess, int width, int height)
    {
        this.setSize(width, height);
        BOARDLENGTH = height - 30;
        this.chess = chess;
        
        chess.addChessboard(this);
        
        if (this.hasPlayers = chess.hasPlayers())
        {
            Human[] humans = chess.getHumans();
            this.humanIsWhite = (humans[0] != null);
            this.humanIsBlack = (humans[1] != null);
            
            if (humanIsWhite)
            {
                white = humans[0];
            }
            if (humanIsBlack)
            {
                black = humans[1];
            }
        }
        
        BufferedImage all = null;
        try {
            // Load the image using classpath-relative path
            InputStream inputStream = getClass().getResourceAsStream("/assets/chessPieces.png");

            if (inputStream != null) {
                // Read the image from the input stream
                all = ImageIO.read(inputStream);
            } else {
                
            }
        } catch (IOException ex) {
            
            ex.printStackTrace();
            return; 
        }
        
        pieces = new Image[12];
        
        int index = 0;
        
        for (int y = 0; y<400; y+=200)
        {
            for (int x = 0; x <1200; x +=200)
            {
                pieces[index] = all.getSubimage(x,y,200,200).getScaledInstance(getSquareSize(),getSquareSize(),BufferedImage.SCALE_SMOOTH);
                index++;
            }
        }
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                
                if (e.getX() >= BOARDLENGTH || e.getX() <=0 || e.getY() >= BOARDLENGTH || e.getY() <=0 || (!humanIsWhite && chess.isWhiteToMove()) || (!humanIsBlack && !chess.isWhiteToMove()))
                {
                    return;
                }
                switch (e.getButton())
                {
                    case MouseEvent.BUTTON1:
                        
                        Piece piece;
                        if ((piece = getPieceByPixel(e)) == null)
                        {
                            return;
                        }
                        
                        if ((piece.isWhite() ==  chess.isWhiteToMove())  && ((chess.isWhiteToMove() == humanIsWhite) || (chess.isWhiteToMove() != humanIsBlack))) 
                        {
                            setSelectedPiece(piece);
                            screenPieceX = e.getX() - getSquareSize() / 2;
                            screenPieceY = e.getY() - getSquareSize() / 2;
                            clearSelectedSquares();
                        }
                        break;
                    case MouseEvent.BUTTON3:
                        Square newSquare;
                        if (selectedSquares.contains(newSquare = getSquareByPixel(e)))
                        {
                            selectedSquares.remove(newSquare);
                        } else {
                            addSelectedSquare(newSquare);
                        }
                        break;
                }
                repaint();
            }
            
            

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getX() >= BOARDLENGTH || e.getX() <=0 || e.getY() >= BOARDLENGTH || e.getY() <=0 || (!humanIsWhite && chess.isWhiteToMove()) || (!humanIsBlack && !chess.isWhiteToMove()))
                {
                    unSetSelectedPiece();
                    clearSelectedSquares();
                    repaint();
                    return;
                }
                
                switch (e.getButton())
                {
                    case MouseEvent.BUTTON1:
                        if ((humanIsWhite && chess.isWhiteToMove()) || (humanIsBlack && !chess.isWhiteToMove()) )
                        if (selectedPiece == null) return;
                        
                        Piece piece = selectedPiece;
                        clearSelectedSquares();
                        unSetSelectedPiece();
                        Move moveToTest = new Move(piece, getSquareByPixel(e));
                        for (Move move : chess.getLegalPieceMoves(piece))
                        {
                            if (move.equals(moveToTest))
                            {
                                (chess.isWhiteToMove() ? white : black).playMove(move);
                                break;
                            }
                            
                        }
                        
                }
                repaint();
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.getX() >= BOARDLENGTH || e.getX() <=0 || e.getY() >= BOARDLENGTH || e.getY() <=0 || (!humanIsWhite && chess.isWhiteToMove()) || (!humanIsBlack && !chess.isWhiteToMove()))
                {
                    selectedPiece = null;
                    return;
                }
                if (selectedPiece != null)
                {
                    screenPieceX = e.getX() - getSquareSize() / 2;
                    screenPieceY = e.getY() - getSquareSize() / 2;
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {}
        });
    }

    /**
     * @return the selectedPiece
     */
    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    /**
     * @param selectedPiece the selectedPiece to set
     */
    public void setSelectedPiece(Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
    }
    
    public void unSetSelectedPiece()
    {
        this.selectedPiece = null; 
    }

    /**
     * @return the selectedSquare
     */
    

    /**
     * @param selectedSquare the selectedSquare to set
     */
    public void addSelectedSquare(Square selectedSquare) {
        selectedSquares.add(selectedSquare);
    }
    
    public void clearSelectedSquares()
    {
        selectedSquares.clear();
    }
    
    
}
