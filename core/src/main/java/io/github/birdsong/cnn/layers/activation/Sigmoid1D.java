package io.github.birdsong.cnn.layers.activation;

public class Sigmoid1D {

    private double[] output;
    private int size;

    public double[] forwardPass(double[] input) {
        size = input.length;

        output = new double[size];

        for (int i = 0; i < size; i++) {
            output[i] = 1 / (1 + (Math.pow(Math.E, -input[i])));
        }

        return output;
    }

    public double[] getOutput() {
        return output;
    }
}
