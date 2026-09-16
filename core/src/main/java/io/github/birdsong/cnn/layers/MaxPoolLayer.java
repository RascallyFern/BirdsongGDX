package io.github.birdsong.cnn.layers;

import java.io.*;
import java.util.Arrays;

public class MaxPoolLayer {
    private double[][][] input, dInput;
    private double[][][] output, dOutput;
    private int inX, inY, channels, filterSize, stride, outX, outY;

    public MaxPoolLayer(int inX, int inY, int channels, int filterSize, int stride) {
        initVars(inX, inY, channels, filterSize, stride);
    }

    public MaxPoolLayer() {}

    private void initVars(int inX, int inY, int channels, int filterSize, int stride) {
        this.inX = inX;
        this.inY = inY;
        this.channels = channels;
        this.filterSize = filterSize;
        this.stride = (stride > 0 ? stride : filterSize); // e.g. if -1 given then matches filter size
        this.outX = ((inX - filterSize) / this.stride) + 1;
        this.outY = ((inY - filterSize) / this.stride) + 1;
        output = new double[channels][outY][outX];
    }

    public double[][][] forwardPass(double[][][] input) {
        this.input = input;

        for (int c = 0; c < channels; c++) {
            for (int y = 0; (y/stride) < outY; y += stride) {
                for (int x = 0; (x/stride) < outX; x += stride) {

                    double max = Double.NEGATIVE_INFINITY;

                    for (int fY = 0; fY < filterSize; fY++) {
                        for (int fX = 0; fX < filterSize; fX++) {
                            max = Math.max(max, input[c][y + fY][x + fX]);
                        }
                    }

                    output[c][y/stride][x/stride] = max;
                }
            }
        }

        return output;
    }

    public double[][][] backwardPass(double[][][] dOutput) {
        this.dOutput = dOutput;
        dInput = new double[channels][inY][inX];

        for (int c = 0; c < channels; c++) {
            for (int y = 0; (y/stride) < outY; y += stride) {
                for (int x = 0; (x/stride) < outX; x += stride) {

                    double max = output[c][y/stride][x/stride];

                    int lastX = -1, lastY = -1;

                    for (int fY = 0; fY < filterSize; fY++) {
                        for (int fX = 0; fX < filterSize; fX++) {
                            if (input[c][y +  fY][x + fX] == max) {
                                lastX = fX;
                                lastY = fY;
                            }
                        }
                    }
                    dInput[c][y + lastY][x + lastX] = dOutput[c][y/stride][x/stride];
                }
            }
        }
        return dInput;
    }

    public double[][][] getOutput() {
        return output;
    }

    public int getOutX() {
        return outX;
    }

    public int getOutY() {
        return outY;
    }

    public int getFlattenedSize() {
        return channels * outY * outX;
    }

    public int getFilterCount() { return channels; }

    public void exportToCSV(BufferedWriter bw) throws IOException {
        //int inputSize, int channels, int filterSize, int stride
        bw.write(String.format("pool,%d,%d,%d,%d,%d\n",inX,inY,channels,filterSize,stride));
    }

    public void exportToDat(DataOutputStream dos) throws IOException {
        dos.writeInt(inX);
        dos.writeInt(inY);
        dos.writeInt(channels);
        dos.writeInt(filterSize);
        dos.writeInt(stride);
    }

    public void importFromCSV(BufferedReader br) throws IOException {
        String line = br.readLine();
        String[] split = line.split(",");
        int[] vars = new int[split.length - 1];

        if (!split[0].equals("pool")) {
            System.out.println("Import does not match pooling layer!");
            return;
        }

        for (int i = 0; i < split.length - 1; i++) {
            vars[i] = Integer.parseInt(split[i+1]);
        }

        initVars(vars[0], vars[1], vars[2], vars[3], vars[4]);
    }

    public void importFromDat(DataInputStream dis) throws IOException {
        int inX = dis.readInt();
        int inY = dis.readInt();
        int channels = dis.readInt();
        int filterSize = dis.readInt();
        int stride = dis.readInt();

        initVars(inX, inY, channels, filterSize, stride);
    }
}
