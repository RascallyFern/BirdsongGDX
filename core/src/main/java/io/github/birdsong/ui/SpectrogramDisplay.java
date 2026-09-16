package io.github.birdsong.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.birdsong.audio.SpectrogramAnalyser;
import io.github.birdsong.audio.SpectrogramGenerator;
import io.github.birdsong.audio.WavReader;

import java.io.IOException;

public class SpectrogramDisplay {
    private int wid, hei, scale;
    private int prevMouseX, spectrogramX;
    private SpectrogramAnalyser sa;
    private Pixmap spectrogramPix;
    private Texture spectrogramTex;
    private TextureRegion spectrogramDisp;
    private SpectrogramGenerator sg;


    public SpectrogramDisplay(int wid, int hei, int scale) throws IOException {
        sg = new SpectrogramGenerator();
        prevMouseX = -1;
        spectrogramX = 0;
        this.wid = wid;
        this.hei = hei;
        this.scale = scale;
        sa = new SpectrogramAnalyser();
    }

    public void loadSpectrogram(String dir) {
        WavReader wr = new WavReader(dir);
        double[][] spectrogram = sg.generateSpectrogram(wr);
        int[] markers = sa.analyseSpectrogram(spectrogram);
        float val;

        spectrogramPix = new Pixmap(spectrogram[0].length * scale, spectrogram.length * scale, Pixmap.Format.RGBA8888);

        for (int y = 0; y < spectrogram.length * scale; y++) {
            for (int x = 0; x < spectrogram[0].length * scale; x++) {
                val = (float) spectrogram[y / scale][x / scale];
                if (markers[(x / scale) / 64] > - 1) {
                    spectrogramPix.drawPixel(x, (hei * scale) - y - 1, Color.rgba8888(1, val, val, 1));
                } else {
                    spectrogramPix.drawPixel(x, (hei * scale) - y - 1, Color.rgba8888(val, val, val, 1));
                }

            }
        }

        spectrogramTex = new Texture(spectrogramPix);
        spectrogramDisp = new TextureRegion(spectrogramTex, wid, hei * scale);
    }

    public void updateRegion() {
        if (spectrogramDisp == null) {
            return;
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int currMouseX = Gdx.input.getX();

            if (prevMouseX == -1) {
                prevMouseX = currMouseX;
            }

            if (currMouseX != prevMouseX) {
                spectrogramX += (prevMouseX - currMouseX);
                prevMouseX = currMouseX;
            }
        } else {
            prevMouseX = -1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            spectrogramX -= 7 * scale;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            spectrogramX += 7 * scale;
        }

        if (spectrogramX > spectrogramTex.getWidth() - Gdx.graphics.getWidth()) {
            spectrogramX = spectrogramTex.getWidth() - Gdx.graphics.getWidth();
        }
        if (spectrogramX < 0) {
            spectrogramX = 0;
        }

        spectrogramDisp.setRegion(spectrogramX, 0, wid, hei * scale);
    }

    public TextureRegion getSpectrogramDisp() {
        return spectrogramDisp;
    }
}
