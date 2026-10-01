package io.github.birdsong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.birdsong.BirdsongApp;
import io.github.birdsong.ui.BirdsongSkin;

public class HomeScreen implements Screen {

    private final BirdsongApp app;
    private Stage stage;
    private Skin skin;

    public HomeScreen(BirdsongApp app) {
        this.app = app;
        stage = new Stage(new ScreenViewport());
        skin = new BirdsongSkin();
        buildUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);

        Label title = new Label("Birdsong", skin);
        TextButton analyseButton = new TextButton("Analyse Recording", skin);
        root.add(title).padBottom(30).row();
        root.add(analyseButton).width(250).height(60);

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
