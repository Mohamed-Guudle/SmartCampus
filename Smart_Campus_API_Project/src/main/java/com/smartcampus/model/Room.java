package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical room on campus.
 * Each room has a unique ID, human-readable name, maximum capacity,
 * and a collection of sensor IDs deployed within it.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class Room {

    // Unique identifier for the room, e.g., "LIB-301"
    private String id;

    // Human-readable name, e.g., "Library Quiet Study"
    private String name;

    // Maximum occupancy for safety regulations
    private int capacity;

    // Collection of sensor IDs deployed in this room
    private List<String> sensorIds = new ArrayList<>();

    // Default constructor required for JSON deserialization
    public Room() {
    }

    // Parameterized constructor for convenience
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    /**
     * Helper method to add a sensor ID to this room.
     * Maintains the bidirectional relationship.
     */
    public void addSensorId(String sensorId) {
        if (!this.sensorIds.contains(sensorId)) {
            this.sensorIds.add(sensorId);
        }
    }

    /**
     * Helper method to remove a sensor ID from this room.
     */
    public void removeSensorId(String sensorId) {
        this.sensorIds.remove(sensorId);
    }
}
