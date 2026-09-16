package io.github.birdsong.cnn.layers.activation;

import java.util.Arrays;

public class ReLU1D {

    private double[] dInput;
    private double[] output;

    private int size;

    public double[] forwardPass(double[] input) {
        size = input.length;

        output = new double[size];
        dInput = new double[size];

        for (int i = 0; i < size; i++) {
            output[i] = (input[i] > 0 ? input[i] : 0);
        }

        return output;
    }

    public double[] backwardPass(double[] dOutput) {
        for (int i = 0; i < size; i++) {
            dInput[i] = (output[i] > 0 ? dOutput[i] : 0);
        }

        return dInput;
    }

    public double[] getOutput() {
        return output;
    }
}
