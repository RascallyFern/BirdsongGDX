package io.github.birdsong.audio;

import io.github.birdsong.cnn.networks.CNNConv3;

import java.io.IOException;

public class SpectrogramAnalyser {

    private final int WINDOW_WIDTH = 128;
    private final int STEP_SIZE = WINDOW_WIDTH / 2;

    private CNNConv3 network;
    private int[] markers;

    public SpectrogramAnalyser() throws IOException {
        network = new CNNConv3(128, 128);
        network.importFromDat("../birdsExportWithLabels.dat");
    }

    public int[] analyseSpectrogram(double[][] spectrogram) {
        double[][] window = new double[spectrogram.length][WINDOW_WIDTH];
        int label;
        markers = new int[(spectrogram[0].length / STEP_SIZE) - 1];

        for (int m = 0; m < markers.length; m++) {
            for (int y = 0; y < spectrogram.length; y++) {
                System.arraycopy(spectrogram[y], m * STEP_SIZE, window[y], 0, WINDOW_WIDTH);
            }

            label = analysePredictions(network.forward(spectrogram));
            markers[m] = label;
        }

        return markers;
    }

    private int analysePredictions(double[] predictions) {
        int idX = 0;
        double max = 0;

        for (int index = 0; index < predictions.length; index++) {
            double p = predictions[index];
            if (p > max) {
                max = p;
                idX = index;
            }
        }

        if (max > 0.98) {
            return idX;
        } else {
            return -1;
        }
    }

}
