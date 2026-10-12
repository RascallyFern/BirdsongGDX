package io.github.birdsong.audio;
import  io.github.birdsong.fft.*;

public class SpectrogramGenerator {

    private final int windowSize = 2048;
    private final int hopSize = 512;
    private final int minFreq = 0;
    private final int maxFreq = 13000;
    private final int melBands = 128;

    private WavReader wav;
    private double[] samples, spectrum;
    private double[][] image;
    private int sampleRate;
    private int[] melBins;

    public double[][] generateSpectrogram(WavReader wav) {
        return generateSpectrogram(wav.getSamples(), (int) wav.getFormat().getSampleRate());
    }

    public double[][] generateSpectrogram(double[] samples, int sampleRate) {
        this.samples = samples;
        this.sampleRate = sampleRate;
        buildMelFilterbank();

        int frames = ((samples.length - windowSize) / hopSize) + 1;
        image = new double[melBands][frames];

        for (int i = 0; i < frames; i++) {
            double[] window = new double[windowSize];
            System.arraycopy(samples, i * hopSize, window, 0, windowSize);
            double[] mel = getSpectrum(window);

            for (int m = 0; m < melBands; m++) {
                image[m][i] = mel[m];
            }
        }

        double minVal = 0.0;
        double maxVal = 10.0;


        for (int y = 0; y < melBands; y++) {
            for (int x = 0; x < frames; x++) {
                image[y][x] = (image[y][x] - minVal) / (maxVal - minVal);
                image[y][x] = Math.max(0.0, Math.min(1.0, image[y][x]));
            }
        }

        return image;
    }

    private void buildMelFilterbank() {
        melBins = new int[melBands + 2];
        double minMel = hzToMel(minFreq);
        double maxMel = hzToMel(maxFreq);

        for (int i = 0; i < melBands + 2; i++) {
            double mel = minMel + (i * (maxMel - minMel) / (melBands + 1));
            double hz = melToHz(mel);

            melBins[i] = (int) Math.floor(hz * windowSize / sampleRate);
        }
    }

    private double[] getSpectrum(double[] window) {
        int sampleCount = window.length;
        double hann;

        Complex[] complexSamples = new Complex[sampleCount];

        for (int i = 0; i < sampleCount; i++) {
            hann = 0.5 * (1 - Math.cos(2 * Math.PI * i / (sampleCount - 1)));
            complexSamples[i] = new Complex(window[i] * hann, 0);
        }

        Complex[] complexSpectrum = FFT.fft(complexSamples);

        double[] pow = new double[complexSpectrum.length / 2];

        for (int i = 0; i < pow.length; i++) {
            double mag = complexSpectrum[i].mag();
            pow[i] = mag * mag;
        }

        spectrum = new double[melBands];

        for (int m = 0; m < melBands; m++) {
            int left = Math.max(0, melBins[m]);
            int center = melBins[m + 1];
            int right = Math.min(pow.length - 1, melBins[m + 2]);

            if (center <= left || right <= center) {
                continue;
            }

            double energy = 0;

            for (int i = left; i < center; i++) {
                energy += pow[i] * ((double) (i - left) / (center - left));
            }

            for (int i = center; i < right; i++) {
                energy += pow[i] * ((double) (right - i) / (right - center));
            }

            spectrum[m] = Math.log(1 + energy);
        }

        return spectrum;
    }

    public double[][][] splitIntoSongs(double[][] spectrogram) {
        int windowLength = 128;
        int boundary = 10;
        int windowStep = windowLength / 2;
        int frames = (spectrogram[0].length - windowLength) / windowStep + 1;
        double rsmTop, rsmBottom;
        double[][][] songs = new double[frames][spectrogram.length][windowLength];

        for (int f = 0; f < frames; f++) {
            double[][] window = new double[spectrogram.length][windowLength];
            rsmTop = 0;
            rsmBottom = 0;
            for (int y = 0; y < spectrogram.length; y++) {
                System.arraycopy(spectrogram[y], f * windowStep, window[y], 0, windowLength);
            }

            for (int y = 0; y < spectrogram.length; y++) {
                for (int x = 0; x < windowLength; x++) {
                    if (y < boundary) {
                        rsmBottom += window[y][x] * window[y][x];
                    } else {
                        rsmTop += window[y][x] * window[y][x];
                    }
                }
            }

            rsmTop = Math.sqrt(rsmTop / ((spectrogram.length - boundary) * windowLength));
            rsmBottom = Math.sqrt(rsmBottom / (boundary * windowLength));

            if (rsmBottom < 0.02 && rsmTop > 0.08) {
                songs[f] = window.clone();
            } else {
                songs[f] = null;
            }
        }
        return songs;
    }

    public double[][][] splitIntoSnippets(double[][] spectrogram, double stepFactor) {
        int windowLength = 128;
        int windowStep = (int) (windowLength * stepFactor);
        int frames = (spectrogram[0].length - windowLength) / windowStep + 1;

        double[][][] snippets = new double[frames][spectrogram.length][windowLength];

        for (int f = 0; f < frames; f++) {
            double[][] window = new double[spectrogram.length][windowLength];
            for (int y = 0; y < spectrogram.length; y++) {
                System.arraycopy(spectrogram[y], f * windowStep, window[y], 0, windowLength);
            }
            snippets[f] = window;
        }

        return snippets;
    }

    private double hzToMel(double hz) {
        return 2595 * Math.log10(1 + hz / 700);
    }

    private double melToHz(double mel) {
        return 700 * (Math.pow(10, mel / 2595) - 1);
    }

    public double[][] flipSpectrogram(double[][] in) {
        double[][] temp = new double[in.length][in[0].length];

        for (int y = 0; y < in.length; y++) {
            for (int x = 0; x < in[0].length; x++) {
                temp[in.length - y - 1][x] = in[y][x];
            }
        }

        return temp;
    }
}
