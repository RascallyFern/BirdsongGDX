package io.github.birdsong.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SpectrogramWidget extends Actor {
    private Texture texture;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (texture == null) {
            return;
        }

        batch.draw(
            texture,
            getX(),
            getY(),
            getWidth(),
            getHeight()
        );
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
