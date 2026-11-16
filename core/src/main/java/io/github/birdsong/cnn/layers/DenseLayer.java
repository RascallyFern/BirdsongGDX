package io.github.birdsong.cnn.layers;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class DenseLayer {

    private double[] input, output, bias;
    private double[] dInput;
    private double[][] weights;
    private int inputSize, outputSize;
    private int inChannels, inHeight, inWidth;

    private Random r = new Random();

    public DenseLayer(int inputSize, int outputSize) {
        initVars(inputSize, outputSize);
        initWeights();
    }

    private void initWeights() {
        // Xavier init for weights
        weights = new double[outputSize][inputSize];
        double limit = Math.sqrt(6.0 / (inputSize + outputSize));

        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = (r.nextDouble() * 2 * limit) - limit;
            }
        }
    }

    private void initVars(int inputSize, int outputSize) {
        input = new double[inputSize];
        output = new double[outputSize];
        bias = new double[outputSize];

        this.inputSize = inputSize;
        this.outputSize = outputSize;
        Arrays.fill(bias, 0.0);
    }

    public double[] forwardPass(double[] input) {
        this.input = input;

        for (int i = 0; i < outputSize; i++) {
            double sum = 0;

            for (int j = 0; j < inputSize; j++) {
                sum += weights[i][j] * input[j];
            }

            output[i] = sum + bias[i];
        }

        return output;
    }

    public double[] forwardPass(double[][][] input) {
        inChannels = input.length;
        inHeight = input[0].length;
        inWidth = input[0][0].length;

        forwardPass(flatten(input));
        return output;
    }

    private double[] flatten(double[][][] input) {
        double[] flattened = new double[inChannels * inHeight * inWidth];

        for (int i = 0; i < inChannels; i++) {
            for (int j = 0; j < inHeight; j++) {
                for (int k = 0; k < inWidth; k++) {
                    flattened[k + (j * inWidth) + (i * inHeight * inWidth)] = input[i][j][k];
                }
            }
        }

        return flattened;
    }

    private double[][][] reshape(double[] input) {
        int count = 0;
        double[][][] output = new double[inChannels][inHeight][inWidth];

        for (int i = 0; i < inChannels; i++) {
            for (int j = 0; j < inHeight; j++) {
                for (int k = 0; k < inWidth; k++) {
                    output[i][j][k] = input[count];
                    count++;
                }
            }
        }

        return output;
    }

    public double[][][] backwardPass3D(double[] dOutput, double learningRate) {
        return reshape(backwardPass1D(dOutput, learningRate));
    }

    public double[] backwardPass1D(double[] dOutput, double learningRate) {
        dInput = new double[inputSize];

        //w.r.t bias
        for (int i = 0; i < outputSize; i++) {
            bias[i] -= learningRate * dOutput[i];
        }

        //w.r.t input
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                dInput[i] += weights[j][i] * dOutput[j];
            }
        }

        //w.r.t weights
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] -= learningRate * dOutput[i] * input[j];
            }
        }

        return dInput;
    }

    public double[] getOutput() {
        return output;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void exportToCSV(BufferedWriter bw) throws IOException {
        //int inputSize, int outputSize
        bw.write(String.format("dense,%d,%d\n",inputSize,outputSize));
        for (int out = 0; out < outputSize; out++) {
            bw.write(Arrays.toString(weights[out]).replaceAll("\\[","").replaceAll("\\]", "").replace(" ", "") + "\n");
        }
        bw.write(Arrays.toString(bias).replaceAll("\\[","").replaceAll("\\]", "").replace(" ", "") + "\n");
    }

    public void exportToDat(DataOutputStream dos) throws IOException {
        dos.writeInt(inputSize);
        dos.writeInt(outputSize);

        for (int out = 0; out < outputSize; out++) {
            for (int w = 0; w < inputSize; w++) {
                dos.writeDouble(weights[out][w]);
            }
        }

        for (int i = 0; i < bias.length; i++) {
            dos.writeDouble(bias[i]);
        }
    }

    public void importFromDat(DataInputStream dis) throws IOException {
        int inputSize = dis.readInt();
        int outputSize = dis.readInt();

        initVars(inputSize, outputSize);

        for (int out = 0; out < outputSize; out++) {
            for (int w = 0; w < inputSize; w++) {
                weights[out][w] = dis.readDouble();
            }
        }

        for (int i = 0; i < bias.length; i++) {
            bias[i] = dis.readDouble();
        }
    }
}

