package io.github.birdsong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.birdsong.screens.HomeScreen;
import io.github.birdsong.ui.BirdsongSkin;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BirdsongApp extends Game {
    @Override
    public void create() {
        setScreen(new HomeScreen(this));
    }

    @Override
    public void render() {
        ScreenUtils.clear(BirdsongSkin.BACKGROUND);
        super.render();
    }

    @Override
    public void dispose() {

    }
}
