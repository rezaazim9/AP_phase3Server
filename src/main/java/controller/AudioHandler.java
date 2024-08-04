package controller;

import model.Profile;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import static controller.constants.DefaultMethods.getVolumeDB;
import static controller.constants.FilePaths.*;

public abstract class AudioHandler {
    public static final ConcurrentMap<Clip, SoundEffectType> clips = new ConcurrentHashMap<>();
    public static final Random random=new Random();

    public static synchronized float playSoundEffect(SoundEffectType type, int i) {
        ClipControlled clipControlled = playSoundEffect(getSoundEffectPath(type, i));
        clips.put(clipControlled.clip, type);
        setVolume(clipControlled.clip);
        return clipControlled.length;
    }

    public static synchronized void playSoundEffect(SoundEffectType type, ActionListener actionListener, AtomicReference<Boolean> flag) {
        final boolean flagSave = flag.get();
        Clip clip = playSoundEffect(type);
        assert clip != null;
        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            if (flag.get() == flagSave) {
                if (!clip.isRunning()) clip.start();
                actionListener.actionPerformed((new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, null)));
            } else {
                clip.stop();
            }
        });
        timer.start();
    }

    public static synchronized void playSoundEffect(SoundEffectType type, Container container) {
        AtomicReference<Boolean> atomicReference = new AtomicReference<>(true);
        ActionListener actionListener = e -> {
            if (container != null) atomicReference.set(container.isVisible());
        };
        playSoundEffect(type, actionListener, atomicReference);
    }

    public static synchronized Clip playSoundEffect(SoundEffectType type) {
        String address = getSoundEffectPath(type);
        boolean loop = type.equals(SoundEffectType.GAME_THEME) || type.equals(SoundEffectType.MENU_THEME);
        Clip clip = playSoundEffect(address).clip;
        if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
        clips.put(clip, type);
        setVolume(clip);
        return clip;
    }

    public static synchronized ClipControlled playSoundEffect(String address) {
        Clip clip;
        float length;
        try {
            File file = new File(address);
            long audioFileLength = file.length();
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL());
            AudioFormat format = audioIn.getFormat();
            AudioFormat newFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16, format.getChannels(),
                    format.getChannels() * 2, format.getSampleRate(), false);
            AudioInputStream newAudioIn = AudioSystem.getAudioInputStream(newFormat, audioIn);
            clip = AudioSystem.getClip();
            clip.open(newAudioIn);
            Clip finalClip = clip;

            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();
            length = audioFileLength / (frameSize * frameRate);
            new Thread(finalClip::start).start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {throw new UnsupportedOperationException("Playback failed for: "+address);}
        return new ClipControlled(clip, length);
    }

    public static String getSoundEffectPath(AudioHandler.SoundEffectType type) {
        return getSoundEffectPath(type, random.nextInt(0, Integer.MAX_VALUE));
    }

    public static String getSoundEffectPath(AudioHandler.SoundEffectType type, int i) {
        String address = switch (type) {
            case HIT -> HIT_SOUND_EFFECTS_PATH.getValue();
            case DOWN -> DOWN_SOUND_EFFECTS_PATH.getValue();
            case SHOOT -> SHOOT_SOUND_EFFECTS_PATH.getValue();
            case COUNTDOWN -> COUNTDOWN_EFFECTS_PATH.getValue();
            case MENU_THEME -> MENU_THEME_PATH.getValue();
            case GAME_THEME -> GAME_THEME_PATH.getValue();
            case XP -> XP_SOUND_EFFECTS_PATH.getValue();
        };
        int numberOfEffects = switch (type) {
            case HIT, DOWN, SHOOT, COUNTDOWN, XP -> Objects.requireNonNull(new File(address).list()).length;
            case MENU_THEME, GAME_THEME -> -1;
        };
        if (numberOfEffects == -1) return address;
        return address + type.name() + i % numberOfEffects + ".ogg";
    }

    public static void setVolume(Clip clip) {
        if (clip == null) return;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float volume = getVolumeDB(clip) * Profile.getCurrent().getSoundScale();
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    public static void setAllVolumes() {
        for (Clip clip : clips.keySet()) {
            if (!clip.isRunning()) clips.remove(clip);
            else setVolume(clip);
        }
    }

    public enum SoundEffectType {
        HIT, DOWN, SHOOT, COUNTDOWN, MENU_THEME, GAME_THEME, XP
    }

    public static class ClipControlled {
        Clip clip;
        float length;

        public ClipControlled(Clip clip, float length) {
            this.clip = clip;
            this.length = length;
        }
    }
}