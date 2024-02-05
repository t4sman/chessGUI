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
public class Square {
    
    public static final Square A1 = new Square(0, 0);
    public static final Square A2 = new Square(0, 1);
    public static final Square A3 = new Square(0, 2);
    public static final Square A4 = new Square(0, 3);
    public static final Square A5 = new Square(0, 4);
    public static final Square A6 = new Square(0, 5);
    public static final Square A7 = new Square(0, 6);
    public static final Square A8 = new Square(0, 7);

    public static final Square B1 = new Square(1, 0);
    public static final Square B2 = new Square(1, 1);
    public static final Square B3 = new Square(1, 2);
    public static final Square B4 = new Square(1, 3);
    public static final Square B5 = new Square(1, 4);
    public static final Square B6 = new Square(1, 5);
    public static final Square B7 = new Square(1, 6);
    public static final Square B8 = new Square(1, 7);

    public static final Square C1 = new Square(2, 0);
    public static final Square C2 = new Square(2, 1);
    public static final Square C3 = new Square(2, 2);
    public static final Square C4 = new Square(2, 3);
    public static final Square C5 = new Square(2, 4);
    public static final Square C6 = new Square(2, 5);
    public static final Square C7 = new Square(2, 6);
    public static final Square C8 = new Square(2, 7);

    public static final Square D1 = new Square(3, 0);
    public static final Square D2 = new Square(3, 1);
    public static final Square D3 = new Square(3, 2);
    public static final Square D4 = new Square(3, 3);
    public static final Square D5 = new Square(3, 4);
    public static final Square D6 = new Square(3, 5);
    public static final Square D7 = new Square(3, 6);
    public static final Square D8 = new Square(3, 7);

    public static final Square E1 = new Square(4, 0);
    public static final Square E2 = new Square(4, 1);
    public static final Square E3 = new Square(4, 2);
    public static final Square E4 = new Square(4, 3);
    public static final Square E5 = new Square(4, 4);
    public static final Square E6 = new Square(4, 5);
    public static final Square E7 = new Square(4, 6);
    public static final Square E8 = new Square(4, 7);

    public static final Square F1 = new Square(5, 0);
    public static final Square F2 = new Square(5, 1);
    public static final Square F3 = new Square(5, 2);
    public static final Square F4 = new Square(5, 3);
    public static final Square F5 = new Square(5, 4);
    public static final Square F6 = new Square(5, 5);
    public static final Square F7 = new Square(5, 6);
    public static final Square F8 = new Square(5, 7);

    public static final Square G1 = new Square(6, 0);
    public static final Square G2 = new Square(6, 1);
    public static final Square G3 = new Square(6, 2);
    public static final Square G4 = new Square(6, 3);
    public static final Square G5 = new Square(6, 4);
    public static final Square G6 = new Square(6, 5);
    public static final Square G7 = new Square(6, 6);
    public static final Square G8 = new Square(6, 7);

    public static final Square H1 = new Square(7, 0);
    public static final Square H2 = new Square(7, 1);
    public static final Square H3 = new Square(7, 2);
    public static final Square H4 = new Square(7, 3);
    public static final Square H5 = new Square(7, 4);
    public static final Square H6 = new Square(7, 5);
    public static final Square H7 = new Square(7, 6);
    public static final Square H8 = new Square(7, 7);
    
    private static final Square[] board = new Square[]{
        A1, B1, C1, D1, E1, F1, G1, H1,
        A2, B2, C2, D2, E2, F2, G2, H2,
        A3, B3, C3, D3, E3, F3, G3, H3,
        A4, B4, C4, D4, E4, F4, G4, H4,
        A5, B5, C5, D5, E5, F5, G5, H5,
        A6, B6, C6, D6, E6, F6, G6, H6,
        A7, B7, C7, D7, E7, F7, G7, H7,
        A8, B8, C8, D8, E8, F8, G8, H8
        };
    
    public static Square inverse(Square square)//the same square but from blacks perspective
    {
        return Square.at(7 - square.getX(),7 - square.getY());
    }

    
    public static Square at(int x,int y)
    {
        if ((x>=0&&x<=7)&&(y>=0&&y<=7)) //ensures that instatiated coordinates are on the chessboard
        {
            return board[x + (y*8)];
        }
        return null;
    }
    
    public static Square at(String algebraic)
    {
        int[] coords = toCoords(algebraic);
        return Square.at(coords[0], coords[1]);
    }
    
    public static int[] toCoords(String algebraic)
    {
        char[] coords = algebraic.toCharArray();
        int[] out = new int[2];
        out[0] = ((int)(coords[0] - 'a'));
        out[1] = ((int)(coords[1] - '1'));
        return out;
    }
    
    public String toAlgebraic() 
    {
        String out = "";
        out += String.valueOf((char)(this.x + 'a'));
        out += String.valueOf((char)(this.y + '1'));
        return out;
    }
    
    private int x;
    private int y;
    
    protected Square(int x,int y) 
    {
        this.x = x;
        this.y = y;
    }
    
    public int getX() 
    {
        return this.x;
    }
    
    public int getY()
    {
        return this.y;
    }
    
    @Override
    public boolean equals(Object square) //checks if two coordinates are referencing the same square
    {
        Square newSquare;
        try{
             newSquare = (Square)square;
        } catch (Exception e) {
            return false;
        }
        return this.hashCode() == newSquare.hashCode();
    }
    
    public int hashCode() {
        return this.x + (this.y * 8);
    }
    
    protected void updateLocation(Square newLocation)
    {
        this.x = newLocation.getX();
        this.y = newLocation.getY();    
    }
    
    
}
