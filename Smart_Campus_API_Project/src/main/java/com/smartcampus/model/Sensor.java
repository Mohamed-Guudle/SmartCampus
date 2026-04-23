package com.smartcampus.model;



import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sensor device deployed in a campus room.
 * Each sensor has a unique ID, type (e.g., Temperature, CO2),
 * status (ACTIVE, MAINTENANCE, OFFLINE), and links to a parent room.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class Sensor {

    // Unique identifier for the sensor, e.g., "TEMP-001"
    private String id;

    // Sensor category, e.g., "Temperature", "Occupancy", "CO2"
    private String type;

    // Current state: "ACTIVE", "MAINTENANCE", or "OFFLINE"
    private String status;

    // The most recent measurement recorded
    private double currentValue;

    // Foreign key linking to the Room where the sensor is located
    private String roomId;

    // Internal list of readings (not exposed in JSON to avoid infinite recursion)
    
    private List<SensorReading> readings = new ArrayList<>();

    // Default constructor required for JSON deserialization
    public Sensor() {
    }

    // Parameterized constructor for convenience
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    
    public List<SensorReading> getReadings() {
        return readings;
    }

    
    public void setReadings(List<SensorReading> readings) {
        this.readings = readings;
    }

    /**
     * Helper method creates a new reading to this sensor.
     * Also updates the currentValue to maintain consistency.
     */
    public void addReading(SensorReading reading) {
        this.readings.add(reading);
        this.currentValue = reading.getValue();
    }
}
