package io.github.birdsong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.birdsong.BirdsongApp;
import io.github.birdsong.audio.SpectrogramGenerator;
import io.github.birdsong.audio.WavReader;
import io.github.birdsong.ui.BirdsongSkin;
import io.github.birdsong.ui.SpectrogramWidget;

public class HomeScreen implements Screen {

    private final BirdsongApp app;
    private Stage stage;
    private Skin skin;
    private SpectrogramGenerator sg;
    SpectrogramWidget widget;

    public HomeScreen(BirdsongApp app) {
        this.app = app;
        stage = new Stage(new ScreenViewport());
        skin = new BirdsongSkin();
        sg = new SpectrogramGenerator();
        buildUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildUI() {
        Table root = new Table();

        root.setFillParent(true);
        root.align(Align.top);

        widget = new SpectrogramWidget();
        widget.loadSpectrogram(sg.flipSpectrogram(sg.generateSpectrogram(new WavReader("../audio/BlueTit_2.wav"))));
        root.add(widget).width(Gdx.graphics.getWidth()).height(200).top();

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void show() {}
}
