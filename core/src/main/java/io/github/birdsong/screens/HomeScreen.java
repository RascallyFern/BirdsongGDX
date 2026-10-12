package io.github.birdsong.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.birdsong.BirdsongApp;
import io.github.birdsong.analysis.AnalysisController;
import io.github.birdsong.audio.AudioPlayer;
import io.github.birdsong.audio.SpectrogramGenerator;
import io.github.birdsong.audio.WavReader;
import io.github.birdsong.cnn.networks.CNNConv3;
import io.github.birdsong.ui.BirdsongSkin;
import io.github.birdsong.ui.SpectrogramWidget;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

public class HomeScreen implements Screen {

    private final BirdsongApp app;
    private AudioPlayer player;
    private Stage stage;
    private Skin skin;
    private SpectrogramWidget widget;
    private TextButton fileButton, playPauseButton;
    private TextField dirField;
    private Window filePopup;
    private Label status, timeLabel;
    private Slider playbackSlider;
    private AnalysisController analyser;
    private CNNConv3 network;

    public HomeScreen(BirdsongApp app) {
        this.app = app;
        stage = new Stage(new ScreenViewport());
        skin = new BirdsongSkin();
        player = new AudioPlayer();
        network = new CNNConv3(128, 128);

        status = new Label("No recording loaded", skin);
        dirField = new TextField("", skin);

        buildUI();
        initialiseNetwork();

        Gdx.input.setInputProcessor(stage);
    }

    private void initialiseNetwork() {
        try {
            network.importFromDat("../assets/birdsExportWithLabels.dat");
        } catch (IOException e) {
            System.err.println("Could not initialise network: " + e);
        }
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.top().left();

        widget = new SpectrogramWidget();
        Table menuBar = createMenuBar();
        Table controls = createPlaybackControls();

        root.add(menuBar).growX().left().top().row();
        root.add(widget).growX().height(300).left().top().row();
        root.add(controls).growX().pad(12).row();

        stage.addActor(root);
    }


    private Table createMenuBar() {
        Table menuBar = new Table();
        menuBar.top().left().pad(12);
        menuBar.defaults().left();

        fileButton = new TextButton("Upload", skin);

        TextButton analysisButton = new TextButton("Analyse", skin);
        menuBar.background(new TextureRegionDrawable(new TextureRegion(createSolidColour(new Color(0.1f, 0.1f, 0.1f, 1f)))));

        menuBar.add(analysisButton).padRight(8);
        menuBar.add(fileButton).padRight(8);
        menuBar.add().expandX();
        menuBar.add(status).right().padRight(12);

        fileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showFileMenu(fileButton);
            }
        });

        analysisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (network == null) {
                    return;
                }
                if (analyser == null) {
                    analyser = new AnalysisController(network);
                }
                if (new File(dirField.getText()).isFile()) {
                    Thread thread = new Thread(() -> {
                        Gdx.app.postRunnable(() -> {
                            analyser.loadFile(dirField.getText());
                            analyser.analyse();
                        });
                    });

                    thread.setDaemon(true);
                    thread.start();
                }
            }
        });

        return menuBar;
    }

    private Table createPlaybackControls() {
        Table controls = new Table();
        playPauseButton = new TextButton("Play", skin);
        timeLabel = new Label("00:00 / 00:00", skin);
        playbackSlider = new Slider(0, 1, 0.001f, false, skin, "default-horizontal");

        controls.add(playPauseButton).width(90).padRight(12);
        controls.add(timeLabel).padRight(12);
        controls.add(playbackSlider).growX();

        playPauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.togglePlayback();

                playPauseButton.setText(player.isPlaying() ? "Pause" : "Play");
            }
        });

        playbackSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (playbackSlider.isDragging()) {
                    long dur = player.getDurMicros();
                    player.setPosMicros((long) (playbackSlider.getValue() * dur));
                    widget.setTime(player.getPosMicros() / 1000000.0);
                }
            }
        });

        return controls;
    }

    public void browseForDir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Audio files (*.wav)", "wav"));

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            dirField.setText(file.getAbsolutePath());
        }
    }

    private void loadPath(String path) {
        if (path.isBlank()) {
            return;
        }

        File file = new File(path);

        if (!file.exists()) {
            System.err.println("Path does not exist: " + path);
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".wav"));

            if (files == null || files.length == 0) {
                System.err.println("No .wav files found.");
                return;
            }

            loadRecording(files[0]);
        } else {
            loadRecording(file);
        }
    }

    public void loadRecording(File file) {
        status.setText("Loading...");
        Thread worker = new Thread(() -> {
            try {
                WavReader reader = new WavReader(file.getAbsolutePath());
                player.load(file);
                SpectrogramGenerator sg = new SpectrogramGenerator();
                double[][] spectogram = sg.generateSpectrogram(reader);
                Gdx.app.postRunnable(() -> {
                    widget.loadSpectrogram((spectogram));
                    status.setText(file.getName());
                });
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> status.setText("Could not load recording: " + e.getMessage()));
            }
        });

        worker.setDaemon(true);
        worker.start();
    }

    private void updatePlaybackUI() {
        long pos = player.getPosMicros();
        long dur = player.getDurMicros();

        if (dur > 0) {
            float progress = (float) pos / dur;

            if (!playbackSlider.isDragging()) {
                playbackSlider.setValue(progress);
            }

            timeLabel.setText(formatTime(pos) + " / " + formatTime(dur));

            widget.setTime(pos / 1000000.0);
        }

        playPauseButton.setText(player.isPlaying() ? "Pause" : "Play");
    }

    public void showFileMenu(TextButton button) {
        if (filePopup != null) {
            filePopup.remove();
            filePopup = null;
            return;
        }

        filePopup = new Window("", skin);
        filePopup.setMovable(true);

        TextButton browseButton = new TextButton("Browse", skin);
        TextButton loadButton = new TextButton("Load", skin);

        Table inputRow = new Table();
        inputRow.add(browseButton).growX().pad(10);
        inputRow.add(loadButton).growX().pad(10).row();
        filePopup.add(dirField).growX().height(40).pad(20, 8, 2, 8).row();
        filePopup.add(inputRow).growX().pad(6).row();


        filePopup.pack();

        Vector2 pos = button.localToStageCoordinates(
            new Vector2(0, 0)
        );

        float popupX = Math.max(0, Math.min(pos.x, 0));
        float popupY = Math.max(0, Math.min(pos.y - filePopup.getHeight(), pos.y - filePopup.getHeight()));

        filePopup.setPosition(popupX, popupY);
        stage.addActor(filePopup);
        filePopup.toFront();

        browseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                browseForDir();
            }
        });

        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadPath(dirField.getText().trim());
                filePopup.remove();
                filePopup = null;
            }
        });
    }

    public Texture createSolidColour(Color colour) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(colour);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

    private String formatTime(long micros) {
        long secs = micros / 1000000;
        return String.format("%02d:%02d", secs / 60, secs % 60);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            player.setPosMicros(player.getPosMicros() + 3000000);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            player.setPosMicros(player.getPosMicros() - 3000000);
        }

        updatePlaybackUI();
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
