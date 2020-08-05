/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.sound;

import editor.MainFrame;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import utils.LambdaUtils.VoidInterface;

/**
 *
 * @author Trifindo
 */
public class SoundPlayer extends Thread {

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final int BUFFER_SIZE = 128000;
    private String filename;
    private InputStream inputStream;
    private InputStream bufferedIn;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    private VoidInterface endAction;

    public void init(String filename, VoidInterface endAction) {
        this.filename = filename;
        this.endAction = endAction;
    }

    /**
     * @param filename the name of the file that is going to be played
     */
    private void playSound() {
        try {
            if (filename != null) {
                try {
                    inputStream = MainFrame.class.getResourceAsStream(filename);
                    bufferedIn = new BufferedInputStream(inputStream);
                } catch (Exception e) {
                    System.out.println("ERROR NUMERO 1");
                }
                
                try {
                    audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                } catch (Exception e) {
                    System.out.println("ERROR NUMERO 2");
                }

                audioFormat = audioStream.getFormat();

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                try {
                    sourceLine = (SourceDataLine) AudioSystem.getLine(info);
                    sourceLine.open(audioFormat);
                } catch (Exception e) {
                    System.out.println("ERROR NUMERO 3");
                }

                sourceLine.start();

                int nBytesRead = 0;
                byte[] abData = new byte[BUFFER_SIZE];
                while (nBytesRead != -1 && running.get()) {
                    try {
                        nBytesRead = audioStream.read(abData, 0, abData.length);
                    } catch (IOException e) {
                        System.out.println("ERROR NUMERO 4");
                    }
                    if (nBytesRead >= 0) {
                        @SuppressWarnings("unused")
                        int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
                    }
                }

                sourceLine.drain();
                sourceLine.close();
            }
        } catch (Exception ex) {

        } finally {
            endAction.action();
        }

    }

    @Override
    public void run() {
        System.out.println("Running");
        running.set(true);
        playSound();
        System.out.println("Finished");
    }

    public void stopPlayer() {
        System.out.println("Stopping");
        running.set(false);
        if(sourceLine != null){
            sourceLine.stop();
        }
        if(sourceLine != null){
            sourceLine.close();
        }
        
    }
}
