package Gaufre.Vue;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import Gaufre.Configuration.ResourceLoader;

public class EffetsSonores {
    public static void playSound(String s) {
        Clip clip;
        try {
            AudioInputStream audioStream = AudioSystem
                    .getAudioInputStream(ResourceLoader.getResourceAsStream("sons/" + s + ".wav"));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Sound effect " + s + " uses an unsupported file encoding. Try 24KHz wave.");
        } catch (NullPointerException e) {
            System.err.println("Sound effect " + s + " not found. Disabling music.");
        }
    }
}
