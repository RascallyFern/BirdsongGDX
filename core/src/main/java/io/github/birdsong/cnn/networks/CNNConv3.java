package io.github.birdsong.cnn.networks;

import io.github.birdsong.cnn.*;
import io.github.birdsong.cnn.layers.*;
import io.github.birdsong.cnn.layers.activation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class CNNConv3 {
    private ConvolutionLayer c1, c2, c3;
    private MaxPoolLayer p1, p2, p3;
    private DenseLayer d1, d2;
    private ReLU3D r1, r2, r3;
    private ReLU1D r4;
    private Sigmoid1D s1;

    private double[][][] trainImages, testImages;
    private int[] trainLabels, testLabels;
    private double initialLearningRate, learningRate, decayRate;
    private int inX, inY;
    private String exportName;
    private String[] labels;
    private File importFile, exportFile;

    public CNNConv3(int inX, int inY) {
        this.inX = inX;
        this.inY = inY;

        exportName = "exportTemp";
        initialLearningRate = 0.005;
        decayRate = 0.90;

        try {
            c1 = new ConvolutionLayer(inX, inY, 1, 20, 3, 1);
            r1 = new ReLU3D();
            p1 = new MaxPoolLayer(c1.getOutX(), c1.getOutY(), c1.getFilterCount(), 2, -1);
            c2 = new ConvolutionLayer(p1.getOutX(), p1.getOutY(), p1.getFilterCount(), 40, 3, 1);
            r2 = new ReLU3D();
            p2 = new MaxPoolLayer(c2.getOutX(), c2.getOutY(), c2.getFilterCount(), 2, -1);
            c3 = new ConvolutionLayer(p2.getOutX(), p2.getOutY(), p2.getFilterCount(), 80, 3, 1);
            r3 = new ReLU3D();
            p3 = new MaxPoolLayer(c3.getOutX(), c3.getOutY(), c3.getFilterCount(), 2, -1);
            d1 = new DenseLayer(p3.getFlattenedSize(), 64);
            r4 = new ReLU1D();
            d2 = new DenseLayer(d1.getOutputSize(), 20);
            s1 = new Sigmoid1D();
        } catch (Exception e) {
            throw new Error("Layers initialised unsuccessfully!");
        }
    }

    public double[] forward(double[][] input) {
        double[][][] x = new double[1][inY][inX];

        x[0] = input;

        x = c1.forwardPass(x);
        x = r1.forwardPass(x);
        x = p1.forwardPass(x);

        x = c2. forwardPass(x);
        x = r2.forwardPass(x);
        x = p2.forwardPass(x);

        x = c3.forwardPass(x);
        x = r3.forwardPass(x);
        x = p3.forwardPass(x);

        double[] y = d1.forwardPass(x);
        y = r4.forwardPass(y);
        y = d2.forwardPass(y);
        y = s1.forwardPass(y);

        return y;
    }

    private void backward(double[] loss) {
        double[] x = d2.backwardPass1D(loss, learningRate);
        x = r4.backwardPass(x);

        double[][][] y = d1.backwardPass3D(x, learningRate);
        y = p3.backwardPass(y);
        y = r3.backwardPass(y);
        y = c3.backwardPass(y, learningRate);
        y = p2.backwardPass(y);
        y = r2.backwardPass(y);
        y = c2.backwardPass(y, learningRate);
        y = p1.backwardPass(y);
        y = r1.backwardPass(y);
        y = c1.backwardPass(y, learningRate);

    }

    public void test() {
        int correct = 0;
        double[] predictions;

        for (int i = 0; i < testImages.length; i++) {
            predictions = forward(testImages[i]);
            if (mostConfidence(predictions) == testLabels[i]) {
                correct++;
            }
        }

        double acc = (double) correct / testImages.length;

        System.out.println("Initial Accuracy: " + acc);
    }

    public void train(int epochs) {
        double[] predictions;

        //how much progress bar increments
        int percent = 4;
        int percentMag = trainImages.length / (100 / percent);
        int count;

        StringBuilder bar;
        long totalStartTime = System.nanoTime();
        long epochTimeTotal = 0;

        for (int e = 0; e < epochs; e++) {

            learningRate = initialLearningRate * Math.pow(decayRate, e);
            System.out.println("\nEpoch " + (e+1) + " (Learning Rate: " + learningRate + "): ");
            test();

            long epochStartTime = System.nanoTime();

            count = 0;
            for (int i = 0; i < trainImages.length; i++) {
                if (i % percentMag == 0) {
                    count++;
                    bar = new StringBuilder("[");
                    for (int j = 0; j < (100 / percent); j++) {
                        if (j < count) {
                            bar.append("=");
                        } else {
                            bar.append("-");
                        }
                    }
                    bar.append("]");
                    System.out.print("\rTraining Progress: " + bar);
                }
                predictions = forward(trainImages[i]);
                backward(Functions.ceGradients(predictions, trainLabels[i]));
            }
            System.out.println();

            long epochEndTime = System.nanoTime();
            double epochTime = (epochEndTime - epochStartTime) / 60000000000.0; //mins
            epochTimeTotal += (epochEndTime - epochStartTime);
            System.out.printf("Epoch %d completed in %.2f mins.\n", e + 1, epochTime);
        }

        test();

        long totalEndTime = System.nanoTime();
        double totalTime = (totalEndTime - totalStartTime) / 60000000000.0;
        double avgEpochTime = (epochTimeTotal / (double) epochs) / 60000000000.0;

        System.out.printf("\nTraining complete!\nTotal training time: %.2f mins\nAverage epoch time: %.2f mins", totalTime, avgEpochTime);
    }

    public int mostConfidence(double[] predictions) {
        double max = Double.NEGATIVE_INFINITY;
        int index = 0;

        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] > max) {
                max = predictions[i];
                index = i;
            }
        }

        return index;
    }

    public void setData(double[][][] trainImages, double[][][] testImages) {
        this.trainImages = trainImages;
        this.testImages = testImages;
    }

    public void setLabels(int[] trainLabels, int[] testLabels) {
        this.trainLabels = trainLabels;
        this.testLabels = testLabels;
    }

    public void exportToCSV() throws IOException {
        exportFile = new File(exportName + ".csv");

        if (!exportFile.createNewFile()) {
            System.out.printf("\n\nFile with name \"%s\" already exists! Overwriting file.", exportFile + ".csv");
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(exportFile));
        c1.exportToCSV(bw);
        p1.exportToCSV(bw);
        c2.exportToCSV(bw);
        p2.exportToCSV(bw);
        c3.exportToCSV(bw);
        p3.exportToCSV(bw);
        d1.exportToCSV(bw);
        d2.exportToCSV(bw);
        bw.close();
    }

    public void exportToDat(String exportName, String[] labels) throws IOException {
        this.exportName = exportName;
        exportToDat(labels);
    }

    public void exportToDat(String[] labels) throws IOException {
        exportFile = new File(exportName + ".dat");

        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(exportFile)));

        dos.writeUTF("Conv3");

        c1.exportToDat(dos);
        p1.exportToDat(dos);
        c2.exportToDat(dos);
        p2.exportToDat(dos);
        c3.exportToDat(dos);
        p3.exportToDat(dos);
        d1.exportToDat(dos);
        d2.exportToDat(dos);

        dos.writeInt(labels.length);

        for (String label : labels) {
            dos.writeUTF(label);
        }

        dos.close();
    }

    public void importFromCSV(String importDir) throws IOException {
        importFile = new File(importDir);

        BufferedReader br = new BufferedReader(new FileReader(importFile));
        c1.importFromCSV(br);
        p1.importFromCSV(br);
        c2.importFromCSV(br);
        p2.importFromCSV(br);
        c3.importFromCSV(br);
        p3.importFromCSV(br);
        d1.importFromCSV(br);
        d2.importFromCSV(br);
    }

    public void importFromDat(String importDir) throws IOException {
        if (!importDir.contains(".dat")) {
            importDir += ".dat";
        }

        importFile = new File(importDir);

        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(importFile)));

        String networkType = dis.readUTF();

        if (!networkType.equals("Conv3")) {
            System.out.println("Import does not match the Conv3 model!");
            return;
        }

        c1.importFromDat(dis);
        p1.importFromDat(dis);
        c2.importFromDat(dis);
        p2.importFromDat(dis);
        c3.importFromDat(dis);
        p3.importFromDat(dis);
        d1.importFromDat(dis);
        d2.importFromDat(dis);

        int labelCount = dis.readInt();
        labels = new String[labelCount];

        for (int i = 0; i < labelCount; i++) {
            labels[i] = dis.readUTF();
        }

        dis.close();
    }

    public void forwardCSV(String dir) throws IOException {
        File csv = new File(dir);
        String[] line;
        BufferedReader br = new BufferedReader(new FileReader(csv));

        double[][] image = new double[128][128];

        for (int i = 0; i < image.length; i++) {
            line = br.readLine().split(",");
            for (int j = 0; j < image[0].length; j++) {
                image[i][j] = Double.parseDouble(line[j]);
            }
        }

        System.out.println(Arrays.toString(forward(image)));
    }

    public static double[][] pngToArray(String path) throws IOException {

        BufferedImage image = ImageIO.read(new File(path));

        int width = image.getWidth();
        int height = image.getHeight();

        double[][] spectrogram = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int rgb = image.getRGB(x, y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                double value = (red + green + blue) / (3.0 * 255.0);

                spectrogram[height - y - 1][x] = value;
            }
        }

        return spectrogram;
    }
}
