package io.github.birdsong.analysis;

public class Detection {
    private final String species;
    private final double confidence;
    private final double startTime;
    private double endTime;

    public Detection(String species, double confidence, double startTime, double endTime) {
        this.species = species;
        this.confidence = confidence;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getSpecies() {
        return species;
    }

    public double getConfidence() {
        return confidence;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getDuration() {
        return endTime - startTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    private String formatTime(long micros) {
        long secs = micros / 1000000;
        return String.format("%02d:%02d", secs / 60, secs % 60);
    }

    @Override
    public String toString() {
        String str = String.format("Species: %s | Confidence: %.4f | %s - %s", species, confidence, formatTime((long) startTime), formatTime((long) endTime));

        return str;
    }
}
