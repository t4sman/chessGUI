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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {

    private static final String directory = "assets/sounds/";

    private static final File endGame;
    private static final File startGame;
    private static final File movePiece;
    private static final File inCheck;

    static {
        endGame = loadAudioFile("endGame.wav");
        startGame = loadAudioFile("startGame.wav");
        movePiece = loadAudioFile("pieceMove.wav");
        inCheck = loadAudioFile("check.wav");
    }

    private static File loadAudioFile(String fileName) {
        try {
            // Load the audio file from classpath resources
            InputStream inputStream = SoundManager.class.getResourceAsStream("/" + directory + fileName);

            if (inputStream == null) {
                throw new IOException("Audio file not found in classpath: " + fileName);
            }

            // Create a temporary file
            File audioFile = File.createTempFile(fileName, ".wav");

            // Copy the audio data from InputStream to the temporary file
            Files.copy(inputStream, audioFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return audioFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading audio file: " + fileName);
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
