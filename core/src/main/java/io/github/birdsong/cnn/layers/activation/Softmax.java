package io.github.birdsong.cnn.layers.activation;

public class Softmax {
    double[] output;

    public double[] forwardPass(double[] input) {
        output = new double[input.length];
        double sum = 0;
        double max = input[0];

        for (int i = 1; i < input.length; i++) {
            if (input[i] > max) {
                max = input[i];
            }
        }

        for (int i = 0; i < input.length; i++) {
            output[i] = Math.exp(input[i] - max);
            sum += output[i];
        }

        for (int i = 0; i < input.length; i++) {
            output[i] /= (sum + 1e-10);
        }

        return output;
    }
}
