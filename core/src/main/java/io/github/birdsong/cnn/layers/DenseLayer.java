package io.github.birdsong.cnn.layers;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class DenseLayer {

    private double[] input, output, bias;
    private double[] dInput, dOutput;
    private double[][] weights;
    private int inputSize, outputSize;
    private int inChannels, inHeight, inWidth;
    private double learningRate;

    private Random r = new Random();

    public DenseLayer(int inputSize, int outputSize) {
        initVars(inputSize, outputSize);

        Arrays.fill(bias, 0.0);

        // Xavier init for weights
        double limit = Math.sqrt(6.0 / (inputSize + outputSize));

        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = (r.nextDouble() * 2 * limit) - limit;
            }
        }
    }

    public DenseLayer() {}

    private void initVars(int inputSize, int outputSize) {
        input = new double[inputSize];
        output = new double[outputSize];
        weights = new double[outputSize][inputSize];
        bias = new double[outputSize];

        this.inputSize = inputSize;
        this.outputSize = outputSize;
        learningRate = 0.001;
    }

    public double[] forwardPass(double[] input) {
        this.input = input;

        for (int i = 0; i < output.length; i++) {
            double sum = 0;

            for (int j = 0; j < input.length; j++) {
                sum += weights[i][j] * input[j];
            }

            output[i] = sum + bias[i];
        }

        return output;
    }

    public double[] forwardPass(double[][][] input) {
        forwardPass(flatten(input));
        inChannels = input.length;
        inHeight = input[0].length;
        inWidth = input[0][0].length;
        return output;
    }

    private double[] flatten(double[][][] input) {
        double[] flattened = new double[input.length * input[0].length * input[0][0].length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                for (int k = 0; k < input[i][j].length; k++) {
                    flattened[k + (j * input[0][0].length) + (i * input[0].length * input[0][0].length)] = input[i][j][k];
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
        this.dOutput = dOutput;

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

    public void importFromCSV(BufferedReader br) throws IOException {
        String line = br.readLine();
        String[] split = line.split(",");
        String[] filterRow;
        int[] vars = new int[split.length - 1];

        if (!split[0].equals("dense")) {
            System.out.println("Import does not match dense layer!");
            return;
        }

        for (int i = 0; i < split.length - 1; i++) {
            vars[i] = Integer.parseInt(split[i+1]);
        }

        initVars(vars[0], vars[1]);

        for (int out = 0; out < outputSize; out++) {
            filterRow = br.readLine().split(",");
            for (int w = 0; w < filterRow.length; w++) {
                weights[out][w] = Double.parseDouble(filterRow[w]);
            }
        }

        String[] biasImport = br.readLine().split(",");
        for (int i = 0; i < bias.length; i++) {
            bias[i] = Double.parseDouble(biasImport[i]);
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

