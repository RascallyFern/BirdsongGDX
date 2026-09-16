package io.github.birdsong.fft;

public class Complex {
    private double re;
    private double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public Complex add(Complex b) {
        return new Complex(re + b.getRe(), im + b.getIm());
    }

    public Complex sub(Complex b) {
        return new Complex(re - b.getRe(), im - b.getIm());
    }

    public Complex mult(Complex b) {
        return new Complex(
                re * b.getRe() - im * b.getIm(),
                re * b.getIm() + im * b.getRe()
        );
    }

    public double mag() {
        return Math.sqrt(re * re + im * im);
    }

    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }
}
