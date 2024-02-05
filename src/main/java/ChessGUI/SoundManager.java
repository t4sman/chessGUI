/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {

    private static final String directory = "./assets/sounds/";

    private static final File endGame;
    private static final File startGame;
    private static final File movePiece;
    private static final File inCheck;

    static {
        try {
            endGame = new File(directory + "endGame.wav");
            startGame = new File(directory + "startGame.wav");
            movePiece = new File(directory + "pieceMove.wav");
            inCheck = new File(directory + "check.wav");
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            throw new RuntimeException("Error initializing SoundManager");
        }
    }

    public static void endGame() {
        try {
            playSound(AudioSystem.getAudioInputStream(endGame));
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(SoundManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void startGame() {
        try {
            playSound(AudioSystem.getAudioInputStream(startGame));
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(SoundManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void movePiece() {
        try {
            playSound(AudioSystem.getAudioInputStream(movePiece));
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(SoundManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void inCheck() {
        try {
            playSound(AudioSystem.getAudioInputStream(inCheck));
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(SoundManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void playSound(AudioInputStream audioStream) {
        Clip newClip = null;
        try {
            newClip = AudioSystem.getClip();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(SoundManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        final Clip clip = newClip;
        
        Thread soundPlayer = new Thread(){
            @Override
            public void run()
            { 
                if (clip == null) return;
            
                try {
                    clip.open(audioStream);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(SoundManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SoundManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                clip.start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SoundManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                clip.stop();
            }
        };
        
    }
}
