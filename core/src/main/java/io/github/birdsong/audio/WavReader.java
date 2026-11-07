package io.github.birdsong.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class WavReader {

    private File wavFile;
    private AudioInputStream ais;
    private AudioFormat format;
    private double[] samples;
    private Random r;

    public WavReader(String wavDir) {
        wavFile = new File(wavDir);
        r = new Random();
        init();
        readSamples();
        System.out.println(wavDir);
    }

    private void init() {
        try {
            ais = createDecodedStream();
            format = ais.getFormat();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AudioInputStream createDecodedStream()
            throws UnsupportedAudioFileException, IOException {

        return AudioSystem.getAudioInputStream(wavFile);
    }

    private void readSamples() {
        try {
            ais = createDecodedStream();
            byte[] buffer = ais.readAllBytes();
            int numSamples = buffer.length / 2;
            samples = new double[numSamples];

            for (int i = 0; i < numSamples; i++) {
                int low = buffer[i * 2] & 0xFF;
                int high = buffer[i * 2 + 1];

                short sample = (short) ((high << 8) | low);

                samples[i] = sample / 32768.0;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AudioFormat getFormat() {
        return format;
    }

    public double[] getSamples() {
        if (samples == null) {
            getSamples();
        }

        return samples;
    }

    private double[][] addGaussianNoise(double[][] spectrogram) {
        if (spectrogram == null) {
            return null;
        }

        double[][] copy = new double[spectrogram.length][spectrogram[0].length];

        for (int i = 0; i < spectrogram.length; i++) {
            copy[i] = spectrogram[i].clone();
        }

        for (int i = 0; i < copy.length; i++) {
            for (int j = 0; j < copy[0].length; j++) {
                copy[i][j] = Math.max(0.0, Math.min(1.0, spectrogram[i][j] + r.nextGaussian() * (0.01 + r.nextDouble() * 0.05)));
            }
        }

        return copy;
    }

    private double[][] shiftX(double[][] spectrogram) {
        if (spectrogram == null) {
            return null;
        }

        double[][] shifted = new double[spectrogram.length][spectrogram[0].length];

        int shift = r.nextInt(20, 50);
        int width = spectrogram[0].length;
        int length = width - shift;

        if (r.nextInt(0, 2) == 0) {
            //shift left
            for (int row = 0; row < spectrogram.length; row++) {
                System.arraycopy(spectrogram[row], shift, shifted[row], 0, length);
            }
        } else {
            //shift right
            for (int row = 0; row < spectrogram.length; row++) {
                System.arraycopy(spectrogram[row], 0, shifted[row], shift, length);
            }
        }

        return shifted;
    }

    public double[][][] generateAugmented(double[][] spectrogram, int shifts, int noises) {
        if (spectrogram == null) {
            return null;
        }

        //output will have original, only shifted, only with noise and shifted with noise
        int outputs = (shifts * 2) + noises + 1;
        int index = 1;
        double[][][] augmented = new double[outputs][spectrogram.length][spectrogram[0].length];
        augmented[0] = spectrogram;

        for (int s = 0; s < shifts; s++) {
            augmented[index] = shiftX(spectrogram);
            index++;
        }

        for (int s = 0; s < shifts; s++) {
            augmented[index] = addGaussianNoise((shiftX(spectrogram)));
            index++;
        }

        for (int n = 0; n < noises; n++) {
            augmented[index] = addGaussianNoise(spectrogram);
            index++;
        }

        return augmented;
    }

    public double[][] flipSpectrogram(double[][] input) {
        double[][] out = new double[input.length][input[0].length];

        for (int y = 0; y < input.length; y++) {
            for (int x = 0; x < input[0].length; x++) {
                out[out.length - y - 1][x] = input[y][x];
            }
        }

        return out;
    }
}
