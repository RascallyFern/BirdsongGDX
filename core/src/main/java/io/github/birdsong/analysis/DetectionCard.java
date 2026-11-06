package io.github.birdsong.analysis;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.birdsong.ui.BirdsongSkin;

public class DetectionCard extends Table {
    private BirdsongSkin skin;

    public DetectionCard(Detection detection, final Runnable onClick) {
        skin = new BirdsongSkin();
        Pixmap background = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        background.setColor(0.3f, 0.3f, 0.3f, 1);
        background.fill();

        pad(10, 20, 10, 20);
        setBackground(new TextureRegionDrawable(new Texture(background)));
        add(new Label(detection.getSpecies(), skin)).growX().row();
        add(new Label(String.format("Confidence: %.2f", detection.getConfidence()), skin)).growX().row();
        add(new Label(String.format(detection.formatTime((long) detection.getStartTime()) + " - " + detection.formatTime((long) detection.getEndTime()), detection.getConfidence()), skin)).growX().row();

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        });
    }
}
