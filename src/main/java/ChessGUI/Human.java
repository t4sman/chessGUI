/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

/**
 *
 * @author tasma
 */
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Human extends Player {

    private Move moveToPlay = null;
    private boolean hasMoveToPlay = false;
    private final Lock lock = new ReentrantLock();
    private final Condition movePlayed = lock.newCondition();

    public Human(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public Move toMove() {
        lock.lock();
        try {
            while (!hasMoveToPlay) {
                movePlayed.await(); // Wait until a move is played
            }

            Move move = moveToPlay;
            moveToPlay = null;
            hasMoveToPlay = false;

            return move;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null; 
        } finally {
            lock.unlock();
        }
    }

    public void playMove(Move move) {
        lock.lock();
        try {
            moveToPlay = move;
            hasMoveToPlay = true;
            movePlayed.signal(); // Signal that a move has been played
        } finally {
            lock.unlock();
        }
    }
}

