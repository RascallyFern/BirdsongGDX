package io.github.birdsong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.birdsong.audio.SpectrogramGenerator;
import io.github.birdsong.audio.WavReader;
import io.github.birdsong.ui.InputBox;
import io.github.birdsong.ui.SpectrogramDisplay;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private SpectrogramDisplay sd;
    private InputBox ib;
    private int prevMouseX;


    @Override
    public void create() {
        batch = new SpriteBatch();
        sd = new SpectrogramDisplay();
        sd.loadSpectrogram("../audio/BlueTit_5.wav");
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

        if (ib != null) {
            ib.dispose();
        }
    }

    public void manageInputs() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sd.shiftRegion(-8);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sd.shiftRegion(8);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            if (ib == null) {
                ib = new InputBox();
            }
        }

        if (ib != null) {
            if (ib.getText() != null) {
                sd.loadSpectrogram(ib.getText());
                prevMouseX = -1;
                ib = null;
            }
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
