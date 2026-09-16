package io.github.birdsong.cnn;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Dataset {

    private Scanner s, counter;
    private double[][][] data;
    private int[] label;
    private int imageCount, width, height;
    private String dir;

    public Dataset(String dir) {
        this.dir = dir;
        try {
            s = new Scanner(new File(dir));
            counter = new Scanner(new File(dir));
            readBirdFiles();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void readBirdFiles() {
        String[] split;

        width = 128;
        height = 128;

        imageCount = 0;
        while (counter.hasNext()) {
            if (counter.nextLine().split(",").length == 1) {
                imageCount++;
            }
        }

        label = new int[imageCount];
        data = new double[imageCount][height][width];

        for (int img = 0; img < imageCount; img++) {
            label[img] = Integer.parseInt(s.nextLine());
            for (int y = 0; y < height; y++) {
                split = s.nextLine().split(",");
                for (int x = 0; x < width; x++) {
                    data[img][y][x] = Double.parseDouble(split[x]);
                }
            }
        }

        System.out.println("File [" + dir + "] read successfully!");
    }

    private void readMNISTFile() {
        String[] split;
        int line = 0;

        if (dir.contains("/mnistcsv/")) {
            s.nextLine();
        }

        width = 28;
        height = 28;

        imageCount = 0;
        while (counter.hasNext()) {
            counter.nextLine();
            imageCount++;
        }

        label = new int[imageCount];
        data = new double[imageCount][height][width];

        while (s.hasNext()) {
            split = s.nextLine().split(",");
            label[line] = Integer.parseInt(split[0]) + ((dir.contains("emnist")) ? -1 : 0);
            if (!(label[line] + 1 <= 0)) {
                for (int i = 0; i < split.length - 1; i++) {
                    data[line][(int) Math.floor((i) / height)][(i % width)] = Double.parseDouble(split[i+1]) / 255;
                }
                line++;
            }

        }

        System.out.println("File [" + dir + "] read successfully!");
    }

    public int[] getLabel() {
        return label;
    }

    public double[][][] getData() {
        return data;
    }

    public int getImageCount() {
        return imageCount;
    }
}
