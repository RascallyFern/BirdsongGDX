package io.github.birdsong.cnn.layers;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class ConvolutionLayer {
    //3D array, x and y for feature map, z axis for each individual feature map from different kernels
    //e.g. input[0] for 1st fm, input[2][1][1] for (1,1) in fm 2.

    private double[][][][] filters;
    private double[][][] input, dInput;
    private double[][][] output, dOutput;

    private double[] bias;
    private int inX, inY, outX, outY, filterSize, filterNum, channels, stride;

    private Random r = new Random();

    public ConvolutionLayer(int inX, int inY, int channels, int filterNum, int filterSize, int stride) {
        initVars(inX, inY, channels, filterNum, filterSize, stride);
        initLayer(filterNum);
    }

    public ConvolutionLayer() {}

    private void initVars(int inX, int inY, int channels, int filterNum, int filterSize, int stride) {
        this.filterSize = filterSize;
        this.inX = inX;
        this.inY = inY;
        this.filterNum = filterNum;
        this.channels = channels;
        this.stride = stride;
        this.outX = ((inX - filterSize) / stride) + 1;
        this.outY = ((inY - filterSize) / stride) + 1;
        output = new double[filterNum][outY][outX];
        filters = new double[filterNum][channels][filterSize][filterSize];

        bias = new double[filterNum];
        Arrays.fill(bias, 0.0);
    }

    private void initLayer(int filterNum) {
        // He init for weights
        double fanIn = channels * filterSize * filterSize;
        double stdv = Math.sqrt(2.0 / fanIn);

        for (int filter = 0; filter < filters.length; filter++) {
            for (int z = 0; z < channels; z++) {
                for (int y = 0; y < filterSize; y++) {
                    for (int x = 0; x < filterSize; x++) {
                        filters[filter][z][y][x] = (r.nextGaussian() * stdv);
                    }
                }
            }
        }
    }

    public double[][][] forwardPass(double[][][] input) {
        this.input = input;

        for (int f = 0; f < filterNum; f++) {
            for (int oY = 0; oY < outY; oY++) {
                int y = oY * stride;
                for (int oX = 0; oX < outX; oX++) {
                    double sum = 0;
                    int x = oX * stride;

                    for (int c = 0; c < channels; c++) {
                        for (int fY = 0; fY < filterSize; fY++) {
                            for (int fX = 0; fX < filterSize; fX++) {
                                sum += input[c][y + fY][x + fX] * filters[f][c][fY][fX];
                            }
                        }
                    }

                    output[f][oY][oX] = sum + bias[f];

                }
            }
        }

        return output;
    }

    public double[][][] backwardPass(double[][][] dOutput, double learningRate) {
        this.dOutput = dOutput;
        dInput = new double[channels][inY][inX];
        double sum;
        int pad = (filterSize - 1);
        double[][][] dOutPadded = new double[dOutput.length][dOutput[0].length + 2 * pad][dOutput[0][0].length + 2 * pad];

        for (int c = 0; c < dOutput.length; c++) {
            for (int y = 0; y < dOutPadded[0].length; y++) {
                for (int x = 0; x < dOutPadded[0][0].length; x++) {
                    if (!(x < pad || y < pad || x >= (dOutPadded[0][0].length - pad) || y >= (dOutPadded[0].length - pad))) {
                        dOutPadded[c][y][x] = dOutput[c][y - pad][x - pad];
                    }
                }
            }
        }

        //loss wrt input
        for (int c = 0; c < channels; c++) {
            for (int f = 0; f < filterNum; f++) {
                for (int y = 0; y < dInput[0].length; y++) {
                    for (int x = 0; x < dInput[0][0].length; x++) {
                        sum = 0;
                        for (int wY = 0; wY < filterSize; wY++) {
                            for (int wX = 0; wX < filterSize; wX++) {
                                sum += dOutPadded[f][y + wY][x + wX] * filters[f][c][wY][wX];
                            }
                        }
                        dInput[c][y][x] += sum;
                    }
                }
            }
        }

        //loss wrt weights
        for (int f = 0; f < dOutput.length; f++) {
            for (int y = 0; y < filterSize; y++) {
                for (int x = 0; x < filterSize; x++) {
                    for (int c = 0; c < channels; c++) {
                        sum = 0;
                        for (int doutY = 0; doutY < dOutput[0].length; doutY++) {
                            for (int doutX = 0; doutX < dOutput[0][0].length; doutX++) {
                                sum += input[c][y + doutY][x + doutX] * dOutput[f][doutY][doutX];
                            }
                        }
                        filters[f][c][y][x] -= learningRate * sum;
                    }
                }
            }
        }

        //lost wrt bias
        for (int b = 0; b < dOutput.length; b++) {
            sum = 0;
            for (int y = 0; y < dOutput[0].length; y++) {
                for (int x = 0; x < dOutput[0][0].length; x++) {
                    sum += dOutput[b][y][x];
                }
            }
            bias[b] -= learningRate * sum;
        }

        return dInput;
    }

    public void exportToCSV(BufferedWriter bw) throws IOException {
        //int inputSize, int channels, int filterNum, int filterSize, int stride
        bw.write(String.format("conv,%d,%d,%d,%d,%d,%d\n",inX,inY,channels,filterNum,filterSize,stride));
        for (int f = 0; f < filterNum; f++) {
            for (int c = 0; c < channels; c++) {
                for (int row = 0; row < filterSize; row++) {
                    bw.write(Arrays.toString(filters[f][c][row]).replaceAll("\\[","").replaceAll("\\]", "").replace(" ", "") + "\n");
                }
            }
        }
        bw.write(Arrays.toString(bias).replaceAll("\\[","").replaceAll("\\]", "").replace(" ", "") + "\n");
    }

    public void exportToDat(DataOutputStream dos) throws IOException {
        dos.writeInt(inX);
        dos.writeInt(inY);
        dos.writeInt(channels);
        dos.writeInt(filterNum);
        dos.writeInt(filterSize);
        dos.writeInt(stride);

        for (int f = 0; f < filterNum; f++) {
            for (int c = 0; c < channels; c++) {
                for (int row = 0; row < filterSize; row++) {
                    for (int w = 0; w < filterSize; w++) {
                        dos.writeDouble(filters[f][c][row][w]);
                    }
                }
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

        if (!split[0].equals("conv")) {
            System.out.println("Import does not match convolution layer!");
            return;
        }

        for (int i = 0; i < split.length - 1; i++) {
            vars[i] = Integer.parseInt(split[i+1]);
        }

        //int inputSize, int channels, int filterNum, int filterSize, int stride
        initVars(vars[0], vars[1], vars[2], vars[3], vars[4], vars[5]);

        for (int f = 0; f < filterNum; f++) {
            for (int c = 0; c < channels; c++) {
                for (int row = 0; row < filterSize; row++) {
                    filterRow = br.readLine().split(",");
                    for (int w = 0; w < filterSize; w++) {
                        filters[f][c][row][w] = Double.parseDouble(filterRow[w]);
                    }
                }
            }
        }

        String[] biasImport = br.readLine().split(",");
        for (int i = 0; i < bias.length; i++) {
            bias[i] = Double.parseDouble(biasImport[i]);
        }
    }

    public void importFromDat(DataInputStream dis) throws IOException {
        int inX = dis.readInt();
        int inY = dis.readInt();
        int channels = dis.readInt();
        int filterNum = dis.readInt();
        int filterSize = dis.readInt();
        int stride = dis.readInt();

        initVars(inX, inY, channels, filterNum, filterSize, stride);

        for (int f = 0; f < filterNum; f++) {
            for (int c = 0; c < channels; c++) {
                for (int row = 0; row < filterSize; row++) {
                    for (int w = 0; w < filterSize; w++) {
                        filters[f][c][row][w] = dis.readDouble();
                    }
                }
            }
        }

        for (int i = 0; i < bias.length; i++) {
            bias[i] = dis.readDouble();
        }
    }

    public double[][][][] getFilters() {
        return filters;
    }

    public double[] getBias() {
        return bias;
    }

    public double[][][] getOutput() {
        return output;
    }

    public int getOutX() {
        return outX;
    }

    public int getOutY() { return outY; }

    public int getFilterCount() { return filters.length; }
}
