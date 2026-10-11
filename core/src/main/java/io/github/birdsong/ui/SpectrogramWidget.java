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
    private Texture[] textures;
    private double[][] spectrogram;
    int prevMouseX, spectrogramX, textureWidth;
    boolean heldDown, loading;

    public SpectrogramWidget() {
        loading = false;
        prevMouseX = -1;
        spectrogramX = 0;
        textureWidth = Gdx.graphics.getWidth() * 2;
        heldDown = false;
        addListener( new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                heldDown = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                int currMouseX = Gdx.input.getX();
                if (prevMouseX == -1) {
                    prevMouseX = currMouseX;
                }

                if (currMouseX != prevMouseX) {
                    spectrogramX += (prevMouseX - currMouseX);
                    prevMouseX = currMouseX;
                }

                spectrogramX = Math.max(0, Math.min(spectrogramX, spectrogram[0].length - textureRegion.getRegionWidth()));

                if (spectrogramX / (textureWidth / 2) < textures.length) {
                    textureRegion.setRegion(new TextureRegion(textures[spectrogramX / (textureWidth / 2)]), spectrogramX % (textureWidth / 2), 0, Gdx.graphics.getWidth(), textures[0].getHeight());
                }

                super.touchDragged(event, x, y, pointer);
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                prevMouseX = -1;
                heldDown = false;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (textures == null || textureRegion == null || loading) {
            return;
        }

        detectInputs();

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

    private void detectInputs() {
        float delta = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            delta += 14;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            delta -= 14;
        }

        spectrogramX += delta * Gdx.graphics.getDeltaTime() * Gdx.graphics.getFramesPerSecond();

        spectrogramX = Math.max(0, Math.min(spectrogramX, spectrogram[0].length - textureRegion.getRegionWidth()));

        if (spectrogramX / (textureWidth / 2) < textures.length) {
            textureRegion.setRegion(new TextureRegion(textures[spectrogramX / (textureWidth / 2)]), spectrogramX % (textureWidth / 2), 0, Gdx.graphics.getWidth(), textures[0].getHeight());
        }
    }

    private void createTextures() {
        int spectrogramX;
        Pixmap pixmap;

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
    }
}
