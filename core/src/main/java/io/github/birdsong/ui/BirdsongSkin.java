package io.github.birdsong.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class BirdsongSkin extends Skin {
    private final Texture buttonTexture;

    public BirdsongSkin() {
        BitmapFont font = new BitmapFont();
        add("default-font", font);

        buttonTexture = new Texture(Gdx.files.internal("birdsong.png"));

        TextureRegion buttonUp = new TextureRegion(buttonTexture, 0, 0, 100, 50);
        TextureRegion buttonDown = new TextureRegion(buttonTexture, 110, 0, 100, 50);
        TextureRegion buttonHover = new TextureRegion(buttonTexture, 0, 60, 100, 50);

        TextureRegionDrawable up = new TextureRegionDrawable(buttonUp);
        TextureRegionDrawable down = new TextureRegionDrawable(buttonDown);
        TextureRegionDrawable hover = new TextureRegionDrawable(buttonHover);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = up;
        buttonStyle.down = down;
        buttonStyle.over = hover;

        add("default", buttonStyle);
        load(Gdx.files.internal("birdsong.json"));
    }

    @Override
    public void dispose() {
        super.dispose();
        buttonTexture.dispose();
    }
}
