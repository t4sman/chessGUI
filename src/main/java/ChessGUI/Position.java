/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;


import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * @author tasma
 */
public class Position{

    
    private static final ConcurrentHashMap<String, Position> fens = new ConcurrentHashMap<>();
    
    private final String FEN;
    private Double eval;
    
    private final boolean  whiteToMove;
    
    private boolean childFound;
    
    private SortedLinkedList<Double, Position> nextPositions;
    
    public static synchronized Position at(String FEN)
    {
        if (fens.containsKey(FEN)) return fens.get(FEN);
        
        return new Position(FEN);
    }
    
    private Position(String FEN)
    {
        this.FEN = FEN;
        fens.put(FEN, this);
        nextPositions = new SortedLinkedList();
        this.whiteToMove = isWhite(this);
    }
    
    public static boolean contains(String FEN)
    {
        return fens.containsKey(FEN);
    }
    
    public static Position getPosition(String FEN)
    {
        return fens.get(FEN);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Position)
        {
            return this.FEN.equals(((Position) o).FEN);
        } else return false;   
    }
    
    public void addPosition(Position nextMove)
    {
        nextPositions.addNode(nextMove.getEval(),nextMove);
    }
    
    public Double getEval()
    {
        if (!childFound)
        {
            return this.eval;
        }
        
        Position bestMove = (whiteToMove ? this.bestMoveForWhite() : this.bestMoveForBlack());
        return bestMove.getEval();
    }
    
    public void setEval(double eval)
    {
        this.eval = eval;
    }
    
    public String getFEN()
    {
        return this.FEN;
    }

    public double worstScoreForPosition()
    {
        return (whiteToMove ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
    }
    
    public static boolean isWhite(Position position)
    {
        String fen = position.getFEN();
        
        String isWhite = fen.split(" ")[1];
        
        return isWhite.equals("w");
    }
    
    public Position bestMoveForWhite()
    {
        return nextPositions.getHeadItem();
    }
    
    public Position bestMoveForBlack()
    {
        return nextPositions.getTailItem();
    }
    
    /**
     * @return the nextPositions
     */
    public Position[] getNextPositions() {
        return nextPositions.OrderedMoves(whiteToMove);
    }
    
    public Position getBestNextPosition() 
    {
        return whiteToMove ? bestMoveForWhite() : bestMoveForBlack();
    }
    
    public Position getWorstNextPosition()
    {
        return !whiteToMove ? bestMoveForWhite() : bestMoveForBlack();
    }

    /**
     * @return the childFound
     */
    public boolean isChildrenFound() {
        return childFound;
    }
    public void childrenFound() {
        this.childFound = true;
    }

    /**
     * @return the depth
     */

    void resortPositions() {
        Position[] positionsToResort = this.getNextPositions();
        nextPositions = new SortedLinkedList();
        for (Position thisPosition : positionsToResort)
        {
            nextPositions.addNode(thisPosition.getEval(), thisPosition);
        }
    }
}
