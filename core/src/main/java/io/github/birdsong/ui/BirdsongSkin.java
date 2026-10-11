package io.github.birdsong.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class BirdsongSkin extends Skin {
    private final Texture buttonTexture;

    public BirdsongSkin() {
        BitmapFont font = new BitmapFont();
        Pixmap colour = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        add("default-font", font);

        buttonTexture = new Texture(Gdx.files.internal("birdsong.png"));

        TextureRegion buttonUp = new TextureRegion(buttonTexture, 0, 0, 140, 70);
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

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        colour.setColor(Color.BLACK);
        textFieldStyle.background = new TextureRegionDrawable(new Texture(colour));
        colour.setColor(Color.GRAY);
        textFieldStyle.focusedBackground = new TextureRegionDrawable(new Texture(colour));

        add("default", buttonStyle);
        add("default", textFieldStyle);
        load(Gdx.files.internal("birdsong.json"));
    }

    @Override
    public void dispose() {
        super.dispose();
        buttonTexture.dispose();
    }
}
