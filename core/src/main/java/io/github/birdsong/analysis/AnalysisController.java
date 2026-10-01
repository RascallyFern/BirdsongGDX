package io.github.birdsong.analysis;

import io.github.birdsong.audio.*;
import io.github.birdsong.cnn.networks.*;

import java.util.List;

public class AnalysisController {
    private WavReader wavReader;
    private double[][] spectrogram;
    private List<Detection> detections;

    private final SpectrogramGenerator spectrogramGenerator;
    private final CNNConv3 network;

    public AnalysisController(CNNConv3 network) {
        this.network = network;
        this.spectrogramGenerator = new SpectrogramGenerator();
    }

    public void loadFile(String dir) {
        wavReader = new WavReader(dir);
        spectrogram = spectrogramGenerator.generateSpectrogram(wavReader);
    }

    public void analyse() {

    }

    public List<Detection> getDetections() {
        return detections;
    }

    public double[][] getSpectrogram() {
        return spectrogram;
    }
}
