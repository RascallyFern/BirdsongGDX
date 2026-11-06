package io.github.birdsong.analysis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    public void analyse(Table container, final Runnable onClick) {
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

                    label = network.getLabels()[network.mostConfidence(predictions)];
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

                        smoothDetections();
                        detections = groupDetections();

                        for (Detection d : detections) {
                            System.out.println(d);
                            container.add(new DetectionCard(d, onClick)).left().row();
                        }

                        analysed = true;
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

    private List<Detection> groupDetections() {
        List<Detection> grouped = new ArrayList<>();

        if (detections.size() <= 1) {
            return detections;
        }

        int currIdx = 0;
        int acc = 1;
        grouped.add(detections.get(0));

        for (int i = 1; i < detections.size(); i++) {
            if (grouped.get(currIdx).getSpecies().equals(detections.get(i).getSpecies())) {
                if (Math.abs(grouped.get(currIdx).getEndTime() - detections.get(i).getStartTime()) < 2000000) {
                    grouped.get(currIdx).setEndTime(detections.get(i).getEndTime());
                    grouped.get(currIdx).setConfidence((grouped.get(currIdx).getConfidence() * acc + detections.get(i).getConfidence()) / (acc + 1));
                    acc++;
                } else {
                    grouped.add(detections.get(i));
                    acc = 1;
                    currIdx++;
                }
            } else {
                grouped.add(detections.get(i));
                acc = 1;
                currIdx++;
            }
        }

        return grouped;
    }

    private void smoothDetections() {
        List<Detection> smoothed = new ArrayList<>();

        if (detections.size() <= 1) {
            return;
        }

        for (int i = 1; i < detections.size(); i++) {
           if (i + 1 < detections.size()) {
               if (detections.get(i - 1).getSpecies().equals(detections.get(i + 1).getSpecies())) {
                   if (detections.get(i - 1).getEndTime() == detections.get(i).getStartTime()) {
                       detections.get(i).setSpecies(detections.get(i + 1).getSpecies());
                   }
               }
           }
        }
    }
}
