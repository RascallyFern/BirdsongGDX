package io.github.birdsong.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WavReader {

    private File wavFile;
    private AudioInputStream ais;
    private AudioFormat format;
    private double[] samples;

    public WavReader(String wavDir) {
        wavFile = new File(wavDir);
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
}
