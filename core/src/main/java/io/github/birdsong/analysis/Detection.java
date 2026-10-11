package io.github.birdsong.analysis;

public class Detection {
    private final String species;
    private final double confidence;
    private final double startTime;
    private final double endTime;

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
}
