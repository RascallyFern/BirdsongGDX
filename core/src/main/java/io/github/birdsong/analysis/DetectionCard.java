package io.github.birdsong.analysis;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.birdsong.ui.BirdsongSkin;

public class DetectionCard extends Table {
    public DetectionCard(Detection detection, Skin skin, final Runnable onClick) {
        setBackground(skin.getDrawable("detection-card"));
        pad(12, 16, 12, 16);

        add(new Label(detection.getSpecies(), skin)).growX().left().row();
        add(new Label(String.format("Confidence: %.2f", detection.getConfidence()), skin)).growX().left().padTop(5).row();
        add(new Label(String.format(detection.formatTime((long) detection.getStartTime()) + " - " + detection.formatTime((long) detection.getEndTime()), detection.getConfidence()), skin)).growX().left().padTop(3);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
//
//            @Override
//            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                if (pointer == -1 && !isWithinCard(fromActor)) {
//                    setBackground(skin.getDrawable("detection-card-hover"));
//                }
//            }
//
//            @Override
//            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                if (pointer == -1 && !isWithinCard(toActor)) {
//                    setBackground(skin.getDrawable("detection-card"));
//                }
//            }
        });
    }

    private boolean isWithinCard(Actor actor) {
        while (actor != null) {
            if (actor == this) {
                return true;
            }

            actor = actor.getParent();
        }

        return false;
    }
}
