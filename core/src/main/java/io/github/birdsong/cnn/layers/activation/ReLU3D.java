package io.github.birdsong.cnn.layers.activation;

public class ReLU3D {
    private double[][][] input, dInput;
    private double[][][] output;

    public double[][][] forwardPass(double[][][] input) {
        this.input = input;

        output = new double[input.length][input[0].length][input[0][0].length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                for (int k = 0; k < input[i][j].length; k++) {
                    output[i][j][k] = Math.max(0, input[i][j][k]);
                }
            }
        }

        return output;
    }

    public double[][][] getOutput() {
        return output;
    }

    public double[][][] backwardPass(double[][][] dOutput) {
        dInput = new double[input.length][input[0].length][input[0][0].length];

        for (int i = 0; i < dInput.length; i++) {
            for (int j = 0; j < dInput[0].length; j++) {
                for (int k = 0; k < dInput[0][0].length; k++) {
                    dInput[i][j][k] = (output[i][j][k] > 0 ? dOutput[i][j][k] : 0);
                }
            }
        }

        return dInput;
    }
}
