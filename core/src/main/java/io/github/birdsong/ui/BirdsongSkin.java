package io.github.birdsong.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

        Texture background = createSolidColour(new Color(0.15f, 0.15f, 0.15f, 1f));
        Texture backgroundFocused = createSolidColour(new Color(0.2f, 0.2f, 0.2f, 1f));

        add("field-background", background, Texture.class);
        add("field-focused", backgroundFocused, Texture.class);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = new TextureRegionDrawable(new TextureRegion(createSolidColour(Color.WHITE)));
        textFieldStyle.selection = new TextureRegionDrawable(new TextureRegion(createSolidColour(new Color(0.5f, 0.5f, 0.6f, 1f))));
        textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(background));
        textFieldStyle.focusedBackground = new TextureRegionDrawable(new TextureRegion(backgroundFocused));

        add("default", textFieldStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        add("default", labelStyle);
        Label.LabelStyle labelStyleSecondary = new Label.LabelStyle();
        labelStyleSecondary.font = font;
        labelStyleSecondary.fontColor = new Color(1f, 0f, 0f, 1f);
        add("secondary", labelStyleSecondary);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = new TextureRegionDrawable(new TextureRegion(createSolidColour(new Color(0.1f, 0.1f, 0.1f, 1f))));
        add("default", windowStyle);

        Texture trackTexture = createSolidColour(new Color(0.2f, 0.2f, 0.2f, 1f));
        Texture knobTexture = createSolidColour(new Color(0.25f, 0.25f, 0.25f, 1f));
        Texture knobSelectedTexture = createSolidColour(new Color(0.3f, 0.3f, 0.3f, 1f));

        TextureRegionDrawable trackDrawable = new TextureRegionDrawable(new TextureRegion(trackTexture));
        trackDrawable.setMinHeight(5);
        TextureRegionDrawable knobDrawable = new TextureRegionDrawable(new TextureRegion(knobTexture));
        knobDrawable.setMinSize(16, 16);
        TextureRegionDrawable knobSelectedDrawable = new TextureRegionDrawable(new TextureRegion(knobSelectedTexture));
        knobSelectedDrawable.setMinSize(16, 16);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = trackDrawable;
        sliderStyle.knob = knobDrawable;
        sliderStyle.knobDown = knobSelectedDrawable;
        add("default-horizontal", sliderStyle);

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = trackDrawable;
        scrollPaneStyle.hScroll = trackDrawable;
        scrollPaneStyle.vScroll = trackDrawable;
        scrollPaneStyle.hScrollKnob = trackDrawable;
        scrollPaneStyle.vScrollKnob = trackDrawable;
        add("default", scrollPaneStyle);

    }

    public Texture createSolidColour(Color colour) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(colour);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

    @Override
    public void dispose() {
        super.dispose();
        buttonTexture.dispose();
    }
}
