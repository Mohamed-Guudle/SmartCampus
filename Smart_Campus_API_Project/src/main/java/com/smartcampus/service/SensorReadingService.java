package com.smartcampus.service;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing SensorReading entities.
 * Handles the creation of new readings and retrieval of historical data.
 * Ensures that readings can only be added to sensors that are in ACTIVE status.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class SensorReadingService {

    // Singleton instance
    private static final SensorReadingService INSTANCE = new SensorReadingService();

    private SensorReadingService() {
        // Private constructor to enforce singleton pattern
    }

    public static SensorReadingService getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves all readings for a specific sensor.
     *
     * @param sensorId the sensor ID
     * @return list of readings for the sensor, or empty list if not found
     */
    public List<SensorReading> getReadingsForSensor(String sensorId) {
        Sensor sensor = SensorService.getInstance().getSensorById(sensorId);
        if (sensor == null) {
            return new java.util.ArrayList<>();
        }
        return sensor.getReadings();
    }

    /**
     * Creates a new reading for a sensor.
     * Side effect: updates the parent sensor's currentValue field.
     *
     * @param sensorId the sensor ID to add the reading to
     * @param reading  the reading data (id and timestamp may be auto-generated)
     * @return the created reading
     * @throws SensorUnavailableException if the sensor is not in ACTIVE status
     * @throws IllegalArgumentException   if the sensor does not exist
     */
    public SensorReading createReading(String sensorId, SensorReading reading) {
        SensorService sensorService = SensorService.getInstance();
        Sensor sensor = sensorService.getSensorById(sensorId);

        // Validate sensor exists
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor with ID '" + sensorId + "' not found.");
        }

        // Validate sensor is available (not in MAINTENANCE or OFFLINE)
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()) ||
                "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        // Auto-generate ID if not provided
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        // Set timestamp to current time if not provided
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Add reading to sensor (this also updates currentValue)
        sensor.addReading(reading);

        return reading;
    }
}
