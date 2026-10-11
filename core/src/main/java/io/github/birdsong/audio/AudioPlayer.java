package io.github.birdsong.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer implements AutoCloseable{
    private Clip clip;

    public void load(File file) throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        stopAndClose();

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
            clip = AudioSystem.getClip();
            clip.open(stream);
        }
    }

    public void play() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void togglePlayback() {
        if (clip == null) {
            return;
        }

        if (clip.isRunning()) {
            pause();
        } else {
            play();
        }
    }

    public void setPosMicros(long pos) {
        if (clip != null) {
            long bounded = Math.max(0, Math.min(pos, clip.getMicrosecondLength()));
            clip.setMicrosecondPosition(bounded);
        }
    }

    public long getPosMicros() {
        return (clip == null ? 0 : clip.getMicrosecondPosition());
    }

    public long getDurMicros() {
        return (clip == null ? 0 : clip.getMicrosecondLength());
    }

    public boolean isPlaying() {
        return (clip != null && clip.isRunning());
    }

    public void stopAndClose() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    @Override
    public void close() throws Exception {
        stopAndClose();
    }
}
