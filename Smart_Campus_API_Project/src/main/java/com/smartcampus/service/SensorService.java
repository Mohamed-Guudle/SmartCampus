package com.smartcampus.service;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service class for managing Sensor entities.
 * Uses a ConcurrentHashMap for thread-safe in-memory storage.
 *
 * Handles sensor registration, retrieval, and linking to rooms.
 * Validates that referenced rooms exist before creating sensors.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class SensorService {

    // Thread-safe map storing sensors keyed by their ID
    private final ConcurrentMap<String, Sensor> sensors = new ConcurrentHashMap<>();

    // Singleton instance
    private static final SensorService INSTANCE = new SensorService();

    private SensorService() {
        // Private constructor to enforce singleton pattern
        initializeSampleData();
    }

    public static SensorService getInstance() {
        return INSTANCE;
    }

    /**
     * Pre-populates the system with sample sensors.
     * These sensors reference the sample rooms created in RoomService.
     */
    private void initializeSampleData() {
        // These will link to rooms that exist in RoomService
        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001", "CO2", "ACTIVE", 450.0, "LIB-301");
        Sensor sensor3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "LAB-102");
        Sensor sensor4 = new Sensor("LIGHT-001", "Lighting", "ACTIVE", 75.0, "LEC-HALL-A");

        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);
        sensors.put(sensor3.getId(), sensor3);
        sensors.put(sensor4.getId(), sensor4);

        // Link sensors to their rooms
        RoomService roomService = RoomService.getInstance();
        roomService.addSensorToRoom("LIB-301", "TEMP-001");
        roomService.addSensorToRoom("LIB-301", "CO2-001");
        roomService.addSensorToRoom("LAB-102", "OCC-001");
        roomService.addSensorToRoom("LEC-HALL-A", "LIGHT-001");
    }

    /**
     * Retrieves all sensors in the system.
     *
     * @return collection of all sensors
     */
    public Collection<Sensor> getAllSensors() {
        return sensors.values();
    }

    /**
     * Retrieves sensors filtered by type.
     *
     * @param type the sensor type to filter by (case-insensitive)
     * @return collection of matching sensors
     */
    public Collection<Sensor> getSensorsByType(String type) {
        return sensors.values().stream()
                .filter(s -> s.getType() != null &&
                        s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific sensor by its ID.
     *
     * @param id the sensor ID
     * @return the Sensor object, or null if not found
     */
    public Sensor getSensorById(String id) {
        return sensors.get(id);
    }

    /**
     * Checks if a sensor with the given ID exists.
     *
     * @param id the sensor ID to check
     * @return true if the sensor exists
     */
    public boolean sensorExists(String id) {
        return sensors.containsKey(id);
    }

    /**
     * Creates a new sensor in the system.
     * Validates that the referenced room exists before creation.
     *
     * @param sensor the sensor to create
     * @return the created sensor
     * @throws LinkedResourceNotFoundException if the referenced room does not exist
     */
    public Sensor createSensor(Sensor sensor) {
        // Validate that the referenced room exists
        RoomService roomService = RoomService.getInstance();
        if (sensor.getRoomId() == null || !roomService.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "roomId",
                    sensor.getRoomId(),
                    "Room"
            );
        }

        sensors.put(sensor.getId(), sensor);

        // Link the sensor to its room
        roomService.addSensorToRoom(sensor.getRoomId(), sensor.getId());

        return sensor;
    }

    /**
     * Deletes a sensor from the system.
     * Also removes the sensor reference from its parent room.
     *
     * @param id the ID of the sensor to delete
     */
    public void deleteSensor(String id) {
        Sensor sensor = sensors.get(id);
        if (sensor != null) {
            // Remove sensor reference from the room
            RoomService roomService = RoomService.getInstance();
            roomService.removeSensorFromRoom(sensor.getRoomId(), id);

            sensors.remove(id);
        }
    }

    /**
     * Updates the current value of a sensor.
     *
     * @param sensorId the sensor ID
     * @param value    the new value
     */
    public void updateSensorValue(String sensorId, double value) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(value);
        }
    }
}
