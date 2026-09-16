package io.github.birdsong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.birdsong.cnn.networks.CNNConv3;
import io.github.birdsong.ui.InterfaceDrawer;

import java.io.IOException;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private InterfaceDrawer id;

    @Override
    public void create() {
        batch = new SpriteBatch();
        try {
            id = new InterfaceDrawer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        id.loadSpectrogram("../audio/BlueTit_5.wav");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        id.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
