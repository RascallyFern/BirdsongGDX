package io.github.birdsong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.birdsong.audio.SpectrogramGenerator;
import io.github.birdsong.audio.WavReader;
import io.github.birdsong.ui.SpectrogramDisplay;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private SpectrogramDisplay sd;
    private SpectrogramGenerator sg;
    private int prevMouseX;


    @Override
    public void create() {
        batch = new SpriteBatch();
        sd = new SpectrogramDisplay();
        sg = new SpectrogramGenerator();

        sd.loadSpectrogram("../audio/Yellowhammer_21.wav");
        prevMouseX = -1;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        manageInputs();
        batch.begin();
        sd.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void manageInputs() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sd.shiftRegion(-8);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sd.shiftRegion(8);
        }

        if (Gdx.input.isTouched()) {
            if (prevMouseX == -1) {
                prevMouseX = Gdx.input.getX();
            } else {
                int currX = Gdx.input.getX();
                int dX = prevMouseX - currX;
                prevMouseX = currX;

                sd.shiftRegion(dX);
            }
        } else {
            prevMouseX = -1;
        }
    }
}
