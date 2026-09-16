package io.github.birdsong.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.birdsong.audio.AudioTools;
import io.github.birdsong.audio.SpectrogramAnalyser;

import java.io.IOException;
import java.util.Scanner;

public class InterfaceDrawer {

    private final int SCREEN_WID = Gdx.graphics.getWidth();
    private final int SCREEN_HEI = Gdx.graphics.getHeight();
    private SpectrogramDisplay sd;
    private AudioTools at;
    private SpectrogramAnalyser sa;

    public InterfaceDrawer() throws IOException {
        sd = new SpectrogramDisplay(Gdx.graphics.getWidth(), 128, 2);
        at = new AudioTools();
    }

    public void loadSpectrogram(String dir) {
        at.convertAllToWav(dir);
        sd.loadSpectrogram(dir);
    }

    public void draw(Batch b) {
        sd.updateRegion();

        b.draw(sd.getSpectrogramDisp(), 0, Gdx.graphics.getHeight() - 128 * 2);
    }
}
