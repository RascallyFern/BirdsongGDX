package io.github.birdsong.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.birdsong.audio.SpectrogramGenerator;
import io.github.birdsong.audio.WavReader;

public class SpectrogramDisplay {
    private double[][] spectrogram;
    private SpectrogramGenerator sg;
    private Pixmap pixmap;
    private Texture[] textures;
    private TextureRegion textureRegion;
    private int currentX = 0;
    private int textureWidth;
    private boolean loading = false;
    private boolean error = false;
    private BitmapFont font;

    public SpectrogramDisplay() {
        sg = new SpectrogramGenerator();
        font = new BitmapFont();
        font.getData().setScale(2);
        textureWidth = Gdx.graphics.getWidth() * 2;
    }

    public void loadSpectrogram(String audioDir) {
        if (loading) { return; }
        loading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WavReader wr = new WavReader(audioDir);
                    error = false;
                    spectrogram = sg.flipSpectrogram(sg.generateSpectrogram(wr));

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            createTextures();
                        }
                    });
                } catch (Exception e) {
                    error = true;
                    loading = false;
                }

            }
        }).start();
    }


    private void createTextures() {
        int spectrogramX;

        Color colour;
        float intensity;
        textures = new Texture[(spectrogram[0].length / Gdx.graphics.getWidth()) - 1];

        for (int i = 0; i < textures.length; i++) {
            pixmap = new Pixmap(textureWidth, spectrogram.length, Pixmap.Format.RGB888);

            for (int y = 0; y < spectrogram.length; y++) {
                for (int x = 0; x < textureWidth; x++) {
                    spectrogramX = i * (textureWidth / 2);
                    if (spectrogramX + x >= spectrogram[0].length) {
                        colour = new Color(0, 0, 0, 1);
                    } else {
                        intensity = (float) spectrogram[y][spectrogramX + x];
                        colour = new Color(intensity, intensity, intensity, 1);
                    }
                    pixmap.setColor(colour);
                    pixmap.drawPixel(x, y);
                }
            }
            textures[i] = new Texture(pixmap);
        }

        textureRegion = new TextureRegion(textures[0], Gdx.graphics.getWidth(), 128);
        loading = false;
    }

    public void draw(Batch batch) {
        if (error) {
            font.draw(batch, "Error Loading Audio...", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() - 64);
        } else if (!loading) {
            batch.draw(textureRegion, 0, Gdx.graphics.getHeight() - 128);
        } else {
            font.draw(batch, "LOADING...", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() - 64);
        }
    }

    public void shiftRegion(int deltaX) {
        if (!loading) {

            currentX += deltaX;

            currentX = Math.max(0, Math.min(currentX, spectrogram[0].length - textureWidth));

            textureRegion.setRegion(new TextureRegion(textures[currentX / (textureWidth / 2)]), currentX % (textureWidth / 2), 0, Gdx.graphics.getWidth(), textures[0].getHeight());
        }
    }

    public boolean isLoading() { return loading; }
}
