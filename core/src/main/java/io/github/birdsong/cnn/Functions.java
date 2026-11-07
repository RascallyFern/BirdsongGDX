package io.github.birdsong.cnn;

public class Functions {
    public static double[] ceGradients(double[] p, double y) {
        // p - predictions, y - actual labels

        double[] grad = new double[p.length];

        //simplified with sigmoid/softmax as final layer
        for (int i = 0; i < p.length; i++) {
            double actual = (y == i ? 1 : 0);
            grad[i] = p[i] - actual;
        }

        return grad;
    }
}
