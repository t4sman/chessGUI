/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;



/**
 *
 * @author tasma
 */
public class ChessClock extends Thread {
    
    private double whiteSeconds; //amount of seconds white has to start
    private double blackSeconds; //amount of seconds black has to start
    
    double timeTakenWhiteToMove = 0; //amount of time white has spent making moves
    double timeTakenBlackToMove = 0; //amount of time black has spent making moves
    
    double timeTakenToMoveThisTurn = 0; //amount of time taken on the current players turn    
    
    Chess chess;//link to chess instance
    
    private boolean isOn = false; //if the clock is on
    
    private boolean turnFinished = false; //set to true once the turn is over and set to false after this is acknowledged
    
    private boolean whiteToMove; //if it is white to move
    
    
    ChessClock(Chess chess)
    {
        this.chess = chess; //set link to chess instance
        this.whiteToMove = chess.isWhiteToMove(); //if it is white to move
    }
    
    public void stopClock()
    {
        isOn = false;
    }
    
    public void reset() //reset the clock
    {
        isOn = false; //turn off the clock
        timeTakenWhiteToMove = 0; //reset the amount of time white has taken to move to 0
        timeTakenBlackToMove = 0; //reset the amount of time black has taken to move to 0
    }
    
    public void setTime(long whiteSeconds, long blackSeconds) //sets the amount of time on the clock
    {
        this.whiteSeconds = whiteSeconds;
        this.blackSeconds = blackSeconds;
    }
    
    private static boolean wait(int milliseconds) //returns true after waiting an amount of time in milliseconds
    {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            return false;
        }
        
        return true;
    }
    
    @Override 
    public void run() //start the clock thread
    {
        isOn = true; //turn on the clock
        while(chess.getClockPanel() == null); //we might not have the clockpanel yet, wait
        while(!chess.isEndOfGame() && isOn) //keep changing moves while its not the end of the game and the clock is on
        {
            toMove();
        }
    }
    
    private boolean outOfTime(boolean isWhite) //if a player is out of time
    {
        return getTimeLeft(isWhite) <= 0; //return true if their time is less than 0
    }
    
    private double getTimeLeft(boolean isWhite) //returns the amount of time left for a player
    {
        return (isWhite ? whiteSeconds - timeTakenWhiteToMove : blackSeconds - timeTakenBlackToMove) - (isWhite == whiteToMove ? timeTakenToMoveThisTurn : 0); //
    }
    
    public String getTimeToString(boolean isWhite) //returns the amount of time left in clock format, to display on the clock panel
    {
        double timeLeft = getTimeLeft(isWhite); //get the amount of time left        
        int minutes = (int)(timeLeft / 60); //convert to minutes
        double seconds = (timeLeft % 60); //calculate the remaining seconds from the minutes
        
        if (minutes > 0) { //if we have less than a minute
            if (outOfTime(isWhite)) { //if we are out of time
                return "0"; //return "0"
            } else {
                return String.format("%02d : %02.1f", Math.abs(minutes), Math.abs(seconds)); //return seconds format to 2dp
            }
        } else {
            return String.format("%02.2f", seconds); //return minutes and seconds to 1dp
}
    }
    
    private void toMove()
    {
        long startTimer = System.currentTimeMillis(); //starts the timer right now
        
        while(this.wait(10) && !turnFinished && !outOfTime(whiteToMove) && isOn) //wait 10 milliseconds and check if the player has finished their turn, if they are out of time, or if the clock was turned off
        {
            timeTakenToMoveThisTurn = ((double)(System.currentTimeMillis() - startTimer)) / 1000; //calculate time taken, storing it for when we exit the while loop
            chess.getClockPanel().repaint(); //repaint the clock
        }
        
        //the player has made a move, or the player is out of time, or the clock was turned off
        
        if (!isOn) return; //if the clock was turned off
        
        else if (!turnFinished) //if the turn is still active, we are out of time
        {
            chess.outOfTime(whiteToMove); //tell chess that we are out of time, end game
            return; //return 
        } else if (whiteToMove) //the turn finished as expect and the game is to continue, update time taken
        {
            timeTakenWhiteToMove += timeTakenToMoveThisTurn;
        } else {
            timeTakenBlackToMove += timeTakenToMoveThisTurn;
        }
        
        
        
        turnFinished = false; //turn is over, set it to false again to wait in the loop until the turn is over again
        
        whiteToMove = !whiteToMove; //flip the turn
    }
    
    public void endTurn() //tells the clock when the player is done making a move
    {
        turnFinished = true; //turn is finished, exit the loop in toMove()
    }
    
    
}
