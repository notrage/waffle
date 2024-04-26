package Gaufre.Vue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.util.IllegalFormatException;

import Gaufre.Configuration.ResourceLoader;

public class Musique {

    private Clip clip;

    public Musique(String audioFilePath) {
        try {
            AudioInputStream audioStream = AudioSystem
                    .getAudioInputStream(ResourceLoader.getResourceAsStream(audioFilePath));
            clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (LineEvent.Type.STOP.equals(event.getType())) {
                    clip.close();
                }
            });
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        } catch (IllegalFormatException e) {
            System.err.println("Music " + audioFilePath + "ses an unsupported file encoding. Try 24KHz wave.");
        } catch (NullPointerException e) {
            System.err.println("Music " + audioFilePath + " not found. Disabling music.");
        }
    }

    public void play() {
        if (clip == null) {
            System.err.println("Music not loaded, maybe due to missing file.");
            return;
        }
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null) {
            System.err.println("Music not loaded, maybe due to missing file.");
            return;
        }
        clip.stop();
    }
}
