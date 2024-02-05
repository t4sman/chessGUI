package ChessGUI;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;
/**
 *
 *
 *
 * @author Tasman Keenan 
 * 
 *
 */
public class StockfishConnector {
    private final Process stockfishProcess;
    private final Scanner stockfishInput;
    private final PrintWriter stockfishOutput;

    public StockfishConnector() throws IOException {
        
        stockfishProcess = new ProcessBuilder("stockfish.exe").start();
        stockfishInput = new Scanner(new InputStreamReader(stockfishProcess.getInputStream()));
        stockfishOutput = new PrintWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
        stockfishInput.nextLine();
    }

    public void sendCommand(String command) {
        stockfishOutput.println(command);
        stockfishOutput.flush();
    }
    
    public synchronized Object[] getEval(String FEN, int depth)
    {
        int centiPawnIndex = 9;
        this.sendCommand("position fen " + FEN);
        this.sendCommand("go depth " + String.valueOf(depth));
        
        String[] lineBefore = null;
        String[] fenParts = FEN.split(" ");
        boolean isWhiteToMove = fenParts[1].equals("w");
        String[] stockfishOutput = this.getResponse().split(" ");
        while(!stockfishOutput[0].equals("bestmove"))
        {
            lineBefore = stockfishOutput;
            stockfishOutput = this.getResponse().split(" ");
        }
        String bestmove = stockfishOutput[1];
        int eval = 0;
        
        try{
            if (lineBefore[8].equals("mate"))
            {
                if (Integer.parseInt(lineBefore[9]) > 0)
                {
                    if (isWhiteToMove)
                    {
                        return new Object[]{bestmove, 1000};
                    } else {
                        return new Object[]{bestmove, -1000};
                    }   
                } else {
                    if (!isWhiteToMove)
                    {
                        return new Object[]{bestmove, 1000};
                    } else {
                        return new Object[]{bestmove, -1000};
                    } 
                }
            } else {
                if (isWhiteToMove)
                    {
                        eval = Integer.parseInt(lineBefore[centiPawnIndex]);
                    } else {
                        eval = -1 * Integer.parseInt(lineBefore[centiPawnIndex]);
                    } 
            }
        } catch (Exception e)
        {
            if (lineBefore[4].equals("mate"))
            {
                if (Integer.parseInt(lineBefore[5]) > 0)
                {
                    if (isWhiteToMove)
                    {
                        return new Object[]{bestmove, 1000};
                    } else {
                        return new Object[]{bestmove, -1000};
                    }    
                } else {
                    if (!isWhiteToMove)
                    {
                        return new Object[]{bestmove, 1000};
                    } else {
                        return new Object[]{bestmove, -1000};
                    } 
                }
            } else {
                return  new Object[]{bestmove, 0};
            }
        }
        
        if (eval > 0)
        {
           return new Object[]{bestmove,Math.min(eval / 3, 1000)}; 
        } else {
           return new Object[]{bestmove,Math.max(eval / 3, -1000)}; 
        }
    }

    private String getResponse(){
        return stockfishInput.nextLine();
    }
    
    public synchronized String getBestMove(String FEN, int strength) {
        this.sendCommand("position fen " + FEN);
        this.sendCommand("go movetime " + String.valueOf(strength));
        
        String[] output = this.getResponse().split(" ");
        while(!output[0].equals("bestmove"))
        {
            output = this.getResponse().split(" ");
        }
        return output[1];
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println(new StockfishConnector().getEval("r5k1/p2R1pp1/bpp1n2p/8/1P6/1BP5/P4rPP/2KR4 b - - 0 22", 16));
    }
}
