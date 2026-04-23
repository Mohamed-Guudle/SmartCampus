package com.smartcampus.model;

/**
 * Represents a single sensor reading event.
 * Each reading has a unique event ID, timestamp, and the measured value.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class SensorReading {

    // Unique reading event ID (UUID recommended)
    private String id;

    // Epoch time (ms) when the reading was captured
    private long timestamp;

    // The actual metric value recorded by the hardware
    private double value;

    // Default constructor required for JSON deserialization
    public SensorReading() {
    }

    // Parameterized constructor for convenience
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
