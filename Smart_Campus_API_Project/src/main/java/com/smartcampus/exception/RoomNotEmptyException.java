package com.smartcampus.exception;

/**
 * Custom exception thrown when attempting to delete a room that still
 * has sensors assigned to it.
 *
 * This exception is mapped to HTTP 409 Conflict to indicate that the
 * request could not be completed due to a conflict with the current
 * state of the resource.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class RoomNotEmptyException extends RuntimeException {

    // The ID of the room that could not be deleted
    private final String roomId;

    // Number of sensors still assigned to the room
    private final int sensorCount;

    /**
     * Constructs a new RoomNotEmptyException.
     *
     * @param roomId      the ID of the room that cannot be deleted
     * @param sensorCount the number of sensors still assigned to the room
     */
    public RoomNotEmptyException(String roomId, int sensorCount) {
        super(String.format("Room '%s' cannot be deleted because it still has %d sensor(s) assigned to it. "
                + "Please remove all sensors before deleting the room.", roomId, sensorCount));
        this.roomId = roomId;
        this.sensorCount = sensorCount;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getSensorCount() {
        return sensorCount;
    }
}
