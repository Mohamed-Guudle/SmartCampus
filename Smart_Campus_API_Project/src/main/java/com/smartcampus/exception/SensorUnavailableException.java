package com.smartcampus.exception;

/**
 * Custom exception thrown when attempting to interact with a sensor
 * that is currently unavailable.
 *
 * For example, when attempting to add a reading to a sensor that is
 * in MAINTENANCE or OFFLINE status.
 *
 * This exception is mapped to HTTP 403 Forbidden to indicate that
 * the server understood the request but refuses to authorize it
 * due to the current state of the sensor.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class SensorUnavailableException extends RuntimeException {

    // The ID of the sensor that is unavailable
    private final String sensorId;

    // The current status of the sensor
    private final String currentStatus;

    /**
     * Constructs a new SensorUnavailableException.
     *
     * @param sensorId      the ID of the unavailable sensor
     * @param currentStatus the current status of the sensor
     */
    public SensorUnavailableException(String sensorId, String currentStatus) {
        super(String.format("Sensor '%s' is currently in '%s' status and cannot accept new readings. "
                + "Please wait until the sensor returns to ACTIVE status.", sensorId, currentStatus));
        this.sensorId = sensorId;
        this.currentStatus = currentStatus;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}
