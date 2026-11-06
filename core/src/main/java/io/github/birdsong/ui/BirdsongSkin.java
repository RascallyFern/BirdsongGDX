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
    private final BitmapFont font;

    private final Texture buttonTexture;
    private final Texture backgroundTexture;
    private final Texture backgroundFocusedTexture;
    private final Texture cursorTexture;
    private final Texture selectionTexture;
    private final Texture windowTexture;
    private final Texture trackTexture;
    private final Texture knobTexture;
    private final Texture knobSelectedTexture;

    public static final Color BACKGROUND = new Color(0.063f, 0.106f, 0.169f, 1f);
    public static final Color PANEL = new Color(0.098f, 0.157f, 0.231f, 1f);
    public static final Color INPUT = new Color(0.129f, 0.204f, 0.294f, 1f);
    public static final Color HOVER = new Color(0.176f, 0.275f, 0.388f, 1f);
    public static final Color ACCENT = new Color(0.396f, 0.725f, 0.839f, 1f);
    public static final Color TEXT = new Color(0.902f, 0.929f, 0.961f, 1f);
    public static final Color SECONDARY_TEXT = new Color(0.620f, 0.686f, 0.749f, 1f);

    public BirdsongSkin() {
        font = new BitmapFont();
        add("default-font", font);

        buttonTexture = createSolidColour(PANEL);
        backgroundTexture = createSolidColour(INPUT);
        backgroundFocusedTexture = createSolidColour(HOVER);
        cursorTexture = createSolidColour(ACCENT);
        selectionTexture = createSolidColour(new Color(0.25f, 0.43f, 0.57f, 1f));
        windowTexture = createSolidColour(BACKGROUND);
        trackTexture = createSolidColour(new Color(0.12f, 0.19f, 0.27f, 1f));
        knobTexture = createSolidColour(ACCENT);
        knobSelectedTexture = createSolidColour(new Color(0.55f, 0.82f, 0.92f, 1f));

        registerDrawable("menu-background", BACKGROUND);
        registerDrawable("spectrogram-background", PANEL);
        registerDrawable("detection-card", INPUT);
        registerDrawable("detection-card-hover", HOVER);
        registerDrawable("detection-card-selected", new Color(0.16f, 0.29f, 0.4f, 1f));

        TextureRegionDrawable buttonUp = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        TextureRegionDrawable buttonDown = new TextureRegionDrawable(new TextureRegion(backgroundFocusedTexture));
        TextureRegionDrawable buttonHover = new TextureRegionDrawable(createSolidColour(HOVER));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        buttonStyle.font = font;
        buttonStyle.fontColor = TEXT;
        buttonStyle.downFontColor = ACCENT;
        buttonStyle.overFontColor = TEXT;
        buttonStyle.up = buttonUp;
        buttonStyle.down = buttonDown;
        buttonStyle.over = buttonHover;
        buttonStyle.up.setLeftWidth(14);
        buttonStyle.up.setRightWidth(14);
        buttonUp.setMinWidth(120);
        buttonUp.setMinHeight(52);
        buttonHover.setMinWidth(120);
        buttonHover.setMinHeight(52);

        add("default", buttonStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();

        textFieldStyle.font = font;
        textFieldStyle.fontColor = TEXT;
        textFieldStyle.cursor = new TextureRegionDrawable(new TextureRegion(cursorTexture));
        textFieldStyle.selection = new TextureRegionDrawable(new TextureRegion(selectionTexture));
        textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        textFieldStyle.focusedBackground = new TextureRegionDrawable(new TextureRegion(backgroundFocusedTexture));

        add("default", textFieldStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = TEXT;
        add("default", labelStyle);

        Label.LabelStyle labelStyleSecondary = new Label.LabelStyle();
        labelStyleSecondary.font = font;
        labelStyleSecondary.fontColor = SECONDARY_TEXT;
        add("secondary", labelStyleSecondary);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = TEXT;
        windowStyle.background = new TextureRegionDrawable(new TextureRegion(windowTexture));
        add("default", windowStyle);

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

        TextureRegionDrawable test = new TextureRegionDrawable(new TextureRegion(knobTexture));
        test.setMinWidth(10);

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = new TextureRegionDrawable(new TextureRegion(trackTexture));
        scrollPaneStyle.hScroll = new TextureRegionDrawable(new TextureRegion(trackTexture));
        scrollPaneStyle.vScroll = new TextureRegionDrawable(new TextureRegion(trackTexture));
        scrollPaneStyle.hScrollKnob = new TextureRegionDrawable(new TextureRegion(knobTexture));
        scrollPaneStyle.vScrollKnob = test;
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

    private void registerDrawable(String name, Color colour) {
        Texture texture = createSolidColour(colour);

        add(name, texture, Texture.class);
        add(name, new TextureRegionDrawable(new TextureRegion(texture)));
    }

    @Override
    public void dispose() {
        super.dispose();

        font.dispose();
        buttonTexture.dispose();
        backgroundTexture.dispose();
        backgroundFocusedTexture.dispose();
        cursorTexture.dispose();
        selectionTexture.dispose();
        windowTexture.dispose();
        trackTexture.dispose();
        knobTexture.dispose();
        knobSelectedTexture.dispose();
    }
}
