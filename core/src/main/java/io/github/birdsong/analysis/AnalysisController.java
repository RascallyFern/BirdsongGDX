package io.github.birdsong.analysis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import io.github.birdsong.audio.*;
import io.github.birdsong.cnn.networks.*;

import java.util.ArrayList;
import java.util.List;

public class AnalysisController {
    private WavReader wavReader;
    private double[][] spectrogram;
    private String currDir;
    private List<Detection> detections;
    private boolean analysed;

    private static final int HOP_SIZE = 512;
    private static final int SAMPLE_RATE = 48000;
    private final AsyncExecutor executor;

    private final SpectrogramGenerator spectrogramGenerator;
    private final CNNConv3 network;

    public AnalysisController(CNNConv3 network) {
        this.network = network;
        this.spectrogramGenerator = new SpectrogramGenerator();
        executor = new AsyncExecutor(1);
        detections = new ArrayList<Detection>();
        analysed = false;
    }

    public void loadFile(String dir) {
        if (currDir == null) {
            currDir = dir;
            wavReader = new WavReader(dir);
            spectrogram = spectrogramGenerator.generateSpectrogram(wavReader);
            analysed = false;
        } else if (!currDir.equals(dir)){
            wavReader = new WavReader(dir);
            spectrogram = spectrogramGenerator.generateSpectrogram(wavReader);
            analysed = false;
        } else {
            System.out.println("This directory has already been loaded.");
        }
    }

    public void analyse() {
        if (!analysed) {
            analysed = true;
        } else {
            return;
        }

        double stepFactor = 0.5;
        double windowLength = stepFactor * 128 * 1000000 * (double) HOP_SIZE / (double) SAMPLE_RATE;

        executor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                double[] predictions;
                double confidence;
                String label;
                double windowStartPos;
                double[][][] snippets = spectrogramGenerator.splitIntoSnippets(spectrogram, stepFactor);

                List<Detection> localDetections = new ArrayList<>();

                int i = 0;
                for (double[][] window : snippets) {
                    predictions = network.forward(window);
                    confidence = predictions[network.mostConfidence(predictions)];

                    label = network.getLabels()[network.mostConfidence(network.forward(window))];
                    if (!label.equals("Noise")) {
                        if (confidence > 0.99) {
                            windowStartPos = windowLength * i;
                            localDetections.add(new Detection(label, confidence, windowStartPos, windowStartPos + windowLength));
                        }
                    }

                    i++;
                }

                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        detections = localDetections;

                        for (Detection d : detections) {
                            System.out.println(d);
                        }

                        System.out.println();
                        groupDetections();

                        for (Detection d : detections) {
                            System.out.println(d);
                        }
                    }
                });

                return null;
            }
        });
    }

    public List<Detection> getDetections() {
        return detections;
    }

    public double[][] getSpectrogram() {
        return spectrogram;
    }

    private void groupDetections() {
        List<Detection> grouped = new ArrayList<>();
        int detectionIdx = -1;

        for (Detection d : detections) {
            if (detectionIdx < 0) {
                grouped.add(d);
                detectionIdx++;
            } else {
                if (d.getStartTime() <= grouped.get(detectionIdx).getEndTime()) {
                    grouped.get(detectionIdx).setEndTime(d.getEndTime());
                } else {
                    detectionIdx++;
                    grouped.add(d);
                }
            }
        }
        detections = grouped;
    }
}
