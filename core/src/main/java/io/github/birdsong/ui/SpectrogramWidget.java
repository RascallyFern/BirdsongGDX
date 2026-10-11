package io.github.birdsong.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import io.github.birdsong.audio.WavReader;

public class SpectrogramWidget extends Actor {
    private TextureRegion textureRegion;
    private Texture background;
    private Texture[] textures;
    private double[][] spectrogram;
    int prevMouseX, spectrogramX, textureWidth;
    boolean heldDown, loading;

    private static final int HOP_SIZE = 512;
    private static final int SAMPLE_RATE = 48000;

    public SpectrogramWidget() {
        loading = false;
        prevMouseX = -1;
        spectrogramX = 0;
        textureWidth = Gdx.graphics.getWidth() * 2;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(0.20f, 0.20f, 0.20f, 1f);
        pixmap.fill();
        background = new Texture(pixmap);

        heldDown = false;
        addListener( new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                heldDown = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (spectrogram == null || textures == null || textures.length == 0 || textureRegion == null) {
                    return;
                }

                int currMouseX = Gdx.input.getX();
                if (prevMouseX == -1) {
                    prevMouseX = currMouseX;
                    return;
                }

                spectrogramX += (prevMouseX - currMouseX);
                prevMouseX = currMouseX;

                int visibleFrames = textureRegion.getRegionWidth();
                int maxScroll = Math.max(0, spectrogram[0].length - visibleFrames);
                spectrogramX = Math.max(0, Math.min(spectrogramX, maxScroll));

                updateTextureRegion();

                super.touchDragged(event, x, y, pointer);
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                prevMouseX = -1;
                heldDown = false;
            }
        });
    }

    public void setTime(double secs) {
        if (spectrogram == null || textures == null || textures.length == 0 || textureRegion == null) {
            return;
        }

        if (!Double.isFinite(secs)) {
            return;
        }

        int visibleFrames = textureRegion.getRegionWidth();
        int maxScroll = Math.max(0, spectrogram[0].length - visibleFrames);
        int targetFrame = (int) (Math.max(0, secs) * SAMPLE_RATE / HOP_SIZE);

        spectrogramX = Math.min(targetFrame, maxScroll);

        updateTextureRegion();
    }

    private void updateTextureRegion() {
        if (spectrogram == null || textures == null || textures.length == 0 || textureRegion == null) {
            return;
        }

        int chunkStep = textureWidth / 2;

        if (chunkStep <= 0) {
            return;
        }

        int chunkIdx = spectrogramX / chunkStep;
        int offsetInChunk = spectrogramX % chunkStep;

        if (chunkIdx >= textures.length) {
            chunkIdx = textures.length - 1;
            offsetInChunk = 0;
        }

        Texture texture = textures[chunkIdx];
        int availableWidth = texture.getWidth() - offsetInChunk;
        int visibleWidth = Math.min(Gdx.graphics.getWidth(), availableWidth);

        if (visibleWidth <= 0) {
            return;
        }

        textureRegion.setRegion(new TextureRegion(texture), offsetInChunk, 0, visibleWidth, texture.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (textures == null || textureRegion == null || loading) {
            batch.draw(
                background,
                getX(),
                getY(),
                getWidth(),
                getHeight()
            );
            return;
        }

        batch.draw(
            textureRegion,
            getX(),
            getY(),
            getWidth(),
            getHeight()
        );
    }

    public void loadSpectrogram(double[][] spectrogram) {
        this.spectrogram = spectrogram;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            loading = true;
                            createTextures();
                            loading = false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void createTextures() {
        if (spectrogram == null || spectrogram.length == 0 || spectrogram[0] == null || spectrogram[0].length == 0) {
            return;
        }

        int frameCount = spectrogram[0].length;
        int chunkStep = Math.max(1, textureWidth / 2);

        if (textures != null) {
            for (Texture texture : textures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }

        int chunkCount = Math.max(1, (frameCount + chunkStep - 1) / chunkStep);
        textures = new Texture[chunkCount];

        for (int i = 0; i < chunkCount; i++) {
            int startFrame = i * chunkStep;
            int width = Math.min(textureWidth, frameCount - startFrame);

            Pixmap pixmap = new Pixmap(width, spectrogram.length, Pixmap.Format.RGB888);

            for (int y = 0; y < spectrogram.length; y++) {
                for (int x = 0; x < width; x++) {
                    float intensity = (float) spectrogram[y][startFrame + x];
                    intensity = Math.max(0f, Math.min(1f, intensity));

                    pixmap.setColor(intensity, intensity, intensity, 1);
                    pixmap.drawPixel(x, spectrogram.length - 1 - y);
                }
            }

            textures[i] = new Texture(pixmap);
            pixmap.dispose();
        }

        spectrogramX = 0;

        int visibleWidth = Math.min(Gdx.graphics.getWidth(), textures[0].getWidth());
        textureRegion = new TextureRegion(textures[0], 0, 0, visibleWidth, textures[0].getHeight());
    }
}
