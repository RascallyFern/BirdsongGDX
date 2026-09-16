package io.github.birdsong.fft;

public class FFT {
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        if (N == 1) {
            return new Complex[]{x[0]};
        }

        if ((Math.log(N) / Math.log(2)) % 1 != 0) {
            throw new IllegalArgumentException("N must be a power of 2");
        }

        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];

        for (int i = 0; i < N / 2; i++) {
            even[i] = x[i * 2];
            odd[i] = x[i * 2 + 1];
        }

        Complex[] E = fft(even);
        Complex[] O = fft(odd);

        Complex[] X = new Complex[N];

        for (int i = 0; i < N / 2; i++) {
            double angle = -2 * Math.PI * ((double) i / N);
            Complex W = new Complex(Math.cos(angle), Math.sin(angle));
            Complex t = W.mult(O[i]);
            X[i] = E[i].add(t);
            X[i + N/2] = E[i].sub(t);
        }

        return X;
    }
}
